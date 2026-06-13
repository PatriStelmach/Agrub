import 'package:alert_app/data/models/problem_action_model.dart';
import 'package:alert_app/logic/history_alerts_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/data/models/alert_model.dart';

class AlertHistoryScreen extends StatefulWidget {
  const AlertHistoryScreen({super.key});

  @override
  State<AlertHistoryScreen> createState() => _AlertHistoryScreenState();
}

class _AlertHistoryScreenState extends State<AlertHistoryScreen>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        context.read<HistoryAlertViewModel>().fetchAlertHistory();
      }
    });
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      debugPrint("Ekran Historii: Aplikacja wybudzona! Synchronizuję dane...");
      context.read<HistoryAlertViewModel>().fetchAlertHistory();
    }
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("--- REBUILD ALERT HISTORY SCREEN ---");
    final historyViewModel = context.watch<HistoryAlertViewModel>();
    final t = AppLocalizations.of(context)!;

    if (historyViewModel.isLoading && historyViewModel.historyList.isEmpty) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (historyViewModel.historyList.isEmpty) {
      return Scaffold(body: Center(child: Text(t.alerts_no_alerts_found)));
    }

    final sortedList = historyViewModel.sortedHistory;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Historia Alertów'),
      ),
      body: Column(
        children: [
          _buildSortHeader(context, historyViewModel, t),
          Expanded(
            child: ListView.builder(
              itemCount: sortedList.length,
              itemBuilder: (context, index) {
                final alert = sortedList[index];
                return _buildAlertCard(context, alert, historyViewModel, t);
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSortHeader(
      BuildContext context,
      HistoryAlertViewModel viewModel,
      AppLocalizations t,
      ) {
    final currentSort = viewModel.currentHistorySortProperty;
    final isAsc = viewModel.isHistoryAscending;

    return Row(
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 16.0),
          child: Text(t.alerts_sort_by, style: const TextStyle(fontSize: 20)),
        ),
        Expanded(
          child: DropdownButton<String>(
            isExpanded: true,
            padding: const EdgeInsets.all(6),
            borderRadius: const BorderRadius.all(Radius.circular(16)),
            value: currentSort,
            icon: const Icon(Icons.menu),
            style: TextStyle(
              color: Theme.of(context).textTheme.bodyLarge?.color,
              fontSize: 16,
              fontFamily: 'JetBrainsMono',
            ),
            onChanged: (String? newValue) {
              if (newValue != null) {
                viewModel.sortHistoryBy(newValue);
              }
            },
            items: [
              DropdownMenuItem(
                value: 'id',
                child: Text(t.alerts_sort_id, style: const TextStyle(fontSize: 20)),
              ),
              DropdownMenuItem(
                value: 'title',
                child: Text(t.alerts_sort_title, style: const TextStyle(fontSize: 20)),
              ),
              DropdownMenuItem(
                value: 'createdAt',
                child: Text(t.alerts_sort_created_at, style: const TextStyle(fontSize: 20)),
              ),
              DropdownMenuItem(
                value: 'acknowledged',
                child: Text(t.alerts_sort_acknowledged, style: const TextStyle(fontSize: 20)),
              ),
              DropdownMenuItem(
                value: 'severity',
                child: Text(t.alerts_sort_severity, style: const TextStyle(fontSize: 20)),
              ),
            ],
          ),
        ),
        IconButton(
          icon: Icon(isAsc ? Icons.arrow_upward : Icons.arrow_downward),
          onPressed: () {
            viewModel.sortHistoryBy(currentSort, ascending: !isAsc);
          },
        ),
      ],
    );
  }

  Widget _buildAlertCard(
      BuildContext context,
      Alert alert,
      HistoryAlertViewModel viewModel,
      AppLocalizations t,
      ) {
    final cardBackgroundColor = alert.severityColor(context);
    final cardBrightness = ThemeData.estimateBrightnessForColor(cardBackgroundColor);
    final textColor = cardBrightness == Brightness.dark
        ? Colors.white
        : const Color(0xFF1E1E1E);

    return Theme(
      data: Theme.of(context).copyWith(
        brightness: cardBrightness,
        iconTheme: IconThemeData(color: textColor),
        textTheme: TextTheme(
          titleLarge: TextStyle(color: textColor, fontWeight: FontWeight.bold),
          bodyLarge: TextStyle(color: textColor),
          bodyMedium: TextStyle(color: textColor),
          bodySmall: TextStyle(color: textColor),
        ),
      ),
      child: Card(
        color: cardBackgroundColor,
        child: ExpansionTile(
          iconColor: textColor,
          collapsedIconColor: textColor,
          leading: Icon(Icons.history, color: textColor),
          title: Text(
            alert.subject ?? t.alerts_tile_no_title,
            style: TextStyle(color: textColor, fontWeight: FontWeight.bold),
          ),
          subtitle: Text(
            alert.acknowledged
                ? t.alerts_status_acknowledged
                : t.alerts_status_not_acknowledged,
            style: TextStyle(color: textColor.withOpacity(0.8)),
          ),
          children: [
            Padding(
              padding: const EdgeInsets.all(12.0),
              child: Column(
                children: [
                  _buildAlertDetails(alert, textColor, t),
                  const SizedBox(height: 12),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: cardBrightness == Brightness.dark
                            ? Colors.white24
                            : Colors.black12,
                        foregroundColor: textColor,
                        elevation: 0,
                      ),
                      onPressed: () => _openAckDialog(context, alert, t),
                      child: Text(t.alerts_button_actions),
                    ),
                  ),
                  _buildActionHistory(context, alert.id, viewModel, t),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAlertDetails(Alert alert, Color textColor, AppLocalizations t) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              alert.source ?? t.alerts_tile_unknown_host,
              style: TextStyle(color: textColor),
            ),
            Text(
              alert.createdAt != null
                  ? DateFormat('dd.MM.yyyy HH:mm:ss').format(alert.createdAt!)
                  : t.alerts_tile_unknown_time,
              style: TextStyle(color: textColor),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            Text(
              t.alerts_tile_severity_prefix(alert.severity.label),
              style: TextStyle(color: textColor, fontWeight: FontWeight.w500),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildActionHistory(
      BuildContext context,
      int alertId,
      HistoryAlertViewModel viewModel,
      AppLocalizations t,
      ) {
    return FutureBuilder<ProblemAction?>(
      future: viewModel.getLatestActionForAlert(alertId),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Padding(
            padding: EdgeInsets.all(8.0),
            child: LinearProgressIndicator(),
          );
        }

        final theme = Theme.of(context);
        final latestAction = snapshot.data;

        if (latestAction == null) {
          return ListTile(
            title: Text(
              t.alerts_history_empty,
              style: theme.textTheme.bodyLarge?.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
          );
        }

        return ListTile(
          leading: Icon(
            Icons.history_edu,
            color: theme.iconTheme.color?.withOpacity(0.7) ?? Colors.grey,
          ),
          title: Text(
            latestAction.message.isNotEmpty
                ? latestAction.message
                : t.alerts_history_no_comment,
            style: theme.textTheme.bodyLarge?.copyWith(
              fontWeight: FontWeight.w600,
            ),
          ),
          subtitle: Text(
            t.alerts_history_subtitle(
              latestAction.author,
              latestAction.ack ? t.yes : t.no,
            ),
            style: theme.textTheme.bodyMedium?.copyWith(
              color: theme.textTheme.bodyMedium?.color?.withOpacity(0.7),
            ),
          ),
        );
      },
    );
  }

  void _openAckDialog(BuildContext context, Alert alert, AppLocalizations t) {
    showDialog(
      context: context,
      builder: (context) => HistoryAckDialog(alert: alert, t: t),
    );
  }
}
class HistoryAckDialog extends StatefulWidget {
  final Alert alert;
  final AppLocalizations t;

  const HistoryAckDialog({super.key, required this.alert, required this.t});

  @override
  State<HistoryAckDialog> createState() => _HistoryAckDialogState();
}

class _HistoryAckDialogState extends State<HistoryAckDialog> {
  late TextEditingController _controller;
  late bool isAck;
  late int selectedSeverity;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();
    selectedSeverity = widget.alert.severity.index;
    isAck = widget.alert.acknowledged;
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final t = widget.t;

    return AlertDialog(
      title: Text(t.alerts_dialog_title(widget.alert.id.toString())),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _controller,
              decoration: InputDecoration(
                labelText: t.alerts_dialog_field_comment,
              ),
            ),
            const SizedBox(height: 15),
            Text(
              t.alerts_dialog_section_severity,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 5),
            DropdownButtonFormField<int>(
              value: selectedSeverity,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                contentPadding: EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 5,
                ),
              ),
              items: [
                DropdownMenuItem(value: 0, child: Text(t.alerts_dialog_severity_info)),
                DropdownMenuItem(value: 1, child: Text(t.alerts_dialog_severity_low)),
                DropdownMenuItem(value: 2, child: Text(t.alerts_dialog_severity_low)),
                DropdownMenuItem(value: 3, child: Text(t.alerts_dialog_severity_medium)),
                DropdownMenuItem(value: 4, child: Text(t.alerts_dialog_severity_high)),
                DropdownMenuItem(value: 5, child: Text(t.alerts_dialog_severity_extreme)),
              ],
              onChanged: (int? newValue) {
                if (newValue != null) {
                  setState(() => selectedSeverity = newValue);
                }
              },
            ),
            CheckboxListTile(
              title: Text(t.alerts_dialog_check_ack),
              value: isAck,
              contentPadding: EdgeInsets.zero,
              onChanged: (bool? value) {
                if (value != null) {
                  setState(() => isAck = value);
                }
              },
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text(t.alerts_dialog_button_cancel),
        ),
        ElevatedButton(
          onPressed: () {
            final commentValue = _controller.text;
            final currentUser = context.read<UserViewModel>().user;
            final currentAuthor = currentUser?.login ?? "Mobile User";

            context.read<HistoryAlertViewModel>().acknowledgeAlert(
              widget.alert.id,
              author: currentAuthor,
              comment: commentValue,
              isAck: isAck,
            );

            Navigator.pop(context);
          },
          child: Text(t.alerts_dialog_button_update),
        ),
      ],
    );
  }
}