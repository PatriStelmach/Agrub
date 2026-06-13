import 'package:alert_app/data/models/history_alert_model.dart';
import 'package:alert_app/data/models/problem_action_model.dart';
import 'package:alert_app/logic/alert_history_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import 'package:alert_app/l10n/app_localizations.dart';

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
        context.read<AlertsHistoryViewModel>().fetchInitialHistoryAlerts();
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
      context.read<AlertsHistoryViewModel>().fetchInitialHistoryAlerts();
    }
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("--- REBUILD ALERT HISTORY SCREEN ---");
    final historyViewModel = context.watch<AlertsHistoryViewModel>();
    final t = AppLocalizations.of(context)!;

    if (historyViewModel.isLoading && historyViewModel.alertsList.isEmpty) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (historyViewModel.alertsList.isEmpty) {
      return Scaffold(body: Center(child: Text(t.alerts_no_alerts_found)));
    }

    final sortedList = historyViewModel.alertsList;

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
      AlertsHistoryViewModel viewModel,
      AppLocalizations t,
      ) {
    final currentSort = viewModel.currentSortProperty;
    final isAsc = viewModel.isAscending;

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
                viewModel.sortAlertsBy(newValue);
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
                value: 'createdAt',
                child: Text(t.alerts_sort_closed_at, style: const TextStyle(fontSize: 20)),
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
            viewModel.sortAlertsBy(currentSort, ascending: !isAsc);
          },
        ),
      ],
    );
  }

  Widget _buildAlertCard(
      BuildContext context,
      HistoryAlert alert,
      AlertsHistoryViewModel viewModel,
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
                  _buildActionHistory(context, alert.id, viewModel, t),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAlertDetails(HistoryAlert alert, Color textColor, AppLocalizations t) {
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
                  ? DateFormat('dd.MM.yyyy HH:mm:ss').format(alert.createdAt)
                  : t.alerts_tile_unknown_time,
              style: TextStyle(color: textColor),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              alert.closedAt != null
                  ? DateFormat('dd.MM.yyyy HH:mm:ss').format(alert.closedAt)
                  : t.alerts_tile_unknown_time,
              style: TextStyle(color: textColor),
            ),
          ],
        ),
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
      AlertsHistoryViewModel viewModel,
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

}