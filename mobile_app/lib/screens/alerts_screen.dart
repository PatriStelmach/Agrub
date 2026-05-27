import 'package:alert_app/data/models/problem_action_model.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/data/models/alert_model.dart';

class AlertsScreen extends StatefulWidget {
  const AlertsScreen({super.key});

  @override
  State<AlertsScreen> createState() => _AlertsScreenState();
}

class _AlertsScreenState extends State<AlertsScreen>
    with WidgetsBindingObserver {
  String dropDownValue = 'id';

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);

    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<AlertsViewModel>().fetchInitialAlerts();
    });
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    // user wraca pushem do aplikacji
    if (state == AppLifecycleState.resumed) {
      debugPrint("Ekran: Aplikacja wybudzona! Synchronizuję dane...");
      context.read<AlertsViewModel>().fetchInitialAlerts();
    }
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("--- REBUILD ---");
    final alertsViewModel = context.watch<AlertsViewModel>();
    final t = AppLocalizations.of(context)!;

    if (alertsViewModel.isLoading && alertsViewModel.alertsList.isEmpty) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }
    final currentSort = alertsViewModel.currentSortProperty;
    final isAsc = alertsViewModel.isAscending;

    if (alertsViewModel.alertsList.isEmpty) {
      return Scaffold(body: Center(child: Text(t.alerts_no_alerts_found)));
    }

    final sortedList = alertsViewModel.sortedAlerts;

    return Column(
      children: [
        Row(
          children: [
            Text(t.alerts_sort_by, style: TextStyle(fontSize: 30)),
            Expanded(
              child: DropdownButton<String>(
                isExpanded: true,
                padding: EdgeInsets.all(6),
                borderRadius: BorderRadius.all(Radius.circular(16)),
                value: currentSort,
                icon: const Icon(Icons.menu),
                style: TextStyle(
                  color: Theme.of(context).textTheme.bodyLarge?.color,
                  fontSize: 18,
                  fontFamily: 'JetBrainsMono',
                ),
                onChanged: (String? newValue) {
                  if (newValue != null) {
                    alertsViewModel.sortAlertsBy(newValue);
                  }
                },
                items: [
                  DropdownMenuItem<String>(
                    value: 'id',
                    child: Text(
                      t.alerts_sort_id,
                      style: TextStyle(fontSize: 30),
                    ),
                  ),
                  DropdownMenuItem<String>(
                    value: 'title',
                    child: Text(
                      t.alerts_sort_title,
                      style: TextStyle(fontSize: 30),
                    ),
                  ),
                  DropdownMenuItem<String>(
                    value: 'createdAt',
                    child: Text(
                      t.alerts_sort_created_at,
                      style: TextStyle(fontSize: 30),
                    ),
                  ),
                  DropdownMenuItem<String>(
                    value: 'acknowledged',
                    child: Text(
                      t.alerts_sort_acknowledged,
                      style: TextStyle(fontSize: 30),
                    ),
                  ),
                  DropdownMenuItem<String>(
                    value: 'severity',
                    child: Text(
                      t.alerts_sort_severity,
                      style: TextStyle(fontSize: 30),
                    ),
                  ),
                ],
              ),
            ),
            IconButton(
              icon: Icon(isAsc ? Icons.arrow_upward : Icons.arrow_downward),
              onPressed: () {
                alertsViewModel.sortAlertsBy(currentSort, ascending: !isAsc);
              },
            ),
          ],
        ),

        Expanded(
          child: ListView.builder(
            itemCount: sortedList.length,
            itemBuilder: (context, index) {
              final alert = alertsViewModel.sortedAlerts[index];

              final cardBackgroundColor = alert.severityColor(context);

              final cardBrightness = ThemeData.estimateBrightnessForColor(
                cardBackgroundColor,
              );

              final textColor = cardBrightness == Brightness.dark
                  ? Colors.white
                  : const Color(0xFF1E1E1E);

              return Theme(
                data: Theme.of(context).copyWith(
                  brightness: cardBrightness,
                  iconTheme: IconThemeData(color: textColor),
                  textTheme: TextTheme(
                    titleLarge: TextStyle(
                      color: textColor,
                      fontWeight: FontWeight.bold,
                    ),
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
                    leading: Icon(Icons.warning, color: textColor),

                    title: Text(
                      alert?.subject ?? t.alerts_tile_no_title,
                      style: TextStyle(
                        color: textColor,
                        fontWeight: FontWeight.bold,
                      ),
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
                            Row(
                              children: [
                                Text(
                                  alert?.source ?? t.alerts_tile_unknown_host,
                                  style: TextStyle(color: textColor),
                                ),
                                const Spacer(),
                                Text(
                                  alert?.createdAt != null
                                      ? DateFormat(
                                          'dd.MM.yyyy HH:mm:ss',
                                        ).format(alert!.createdAt)
                                      : t.alerts_tile_unknown_time,
                                  style: TextStyle(color: textColor),
                                ),
                              ],
                            ),
                            const SizedBox(height: 8),
                            Row(
                              children: [
                                const Spacer(),
                                Text(
                                  t.alerts_tile_severity_prefix(
                                    alert!.severity.label,
                                  ),
                                  style: TextStyle(
                                    color: textColor,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 12),
                            SizedBox(
                              width: double.infinity,
                              child: ElevatedButton(
                                style: ElevatedButton.styleFrom(
                                  backgroundColor:
                                      cardBrightness == Brightness.dark
                                      ? Colors.white24
                                      : Colors.black12,
                                  foregroundColor: textColor,
                                  elevation: 0,
                                ),
                                onPressed: () {
                                  _openAckDialog(context, alert.id, t);
                                },
                                child: Text(t.alerts_button_actions),
                              ),
                            ),
                            FutureBuilder<ProblemAction?>(
                              future: context
                                  .read<AlertsViewModel>()
                                  .getLatestActionForAlert(alert.id),
                              builder: (context, snapshot) {
                                if (snapshot.connectionState ==
                                    ConnectionState.waiting) {
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
                                      style: theme.textTheme.bodyLarge
                                          ?.copyWith(
                                            fontWeight: FontWeight.w600,
                                          ),
                                    ),
                                  );
                                }

                                return ListTile(
                                  leading: Icon(
                                    Icons.history,
                                    color:
                                        theme.iconTheme.color?.withOpacity(
                                          0.7,
                                        ) ??
                                        Colors.grey,
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
                                      color: theme.textTheme.bodyMedium?.color
                                          ?.withOpacity(0.7),
                                    ),
                                  ),
                                );
                              },
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              );
            },
          ),
        ),
      ],
    );
  }
}

class AckDialog extends StatefulWidget {
  final int alertId;
  final AppLocalizations t;
  const AckDialog({super.key, required this.alertId, required this.t});

  @override
  State<AckDialog> createState() => _AckDialogState();
}

class _AckDialogState extends State<AckDialog> {
  late TextEditingController _controller;

  bool isAck = true;
  int selectedSeverity = 0;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();

    final alertsViewModel = context.read<AlertsViewModel>();
    final currentAlert = alertsViewModel.alertsList.firstWhere(
      (a) => a.id == widget.alertId,
      // Fallback if alert disappear in the meantime
      orElse: () => Alert(
        id: widget.alertId,
        subject: '',
        source: '',
        severity: AlertSeverity.info,
        status: AlertStatus.sent,
        createdAt: DateTime.now(),
        acknowledged: false,
      ),
    );

    selectedSeverity = currentAlert.severity.index;
    isAck = currentAlert.acknowledged;
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
      title: Text(t.alerts_dialog_title(widget.alertId.toString())),
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
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 5),

            DropdownButtonFormField<int>(
              initialValue: selectedSeverity,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                contentPadding: EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 5,
                ),
              ),
              items: [
                DropdownMenuItem(
                  value: 0,
                  child: Text(t.alerts_dialog_severity_info),
                ),
                DropdownMenuItem(
                  value: 1,
                  child: Text(t.alerts_dialog_severity_low),
                ),
                DropdownMenuItem(
                  value: 2,
                  child: Text(t.alerts_dialog_severity_low),
                ),
                DropdownMenuItem(
                  value: 3,
                  child: Text(t.alerts_dialog_severity_medium),
                ),
                DropdownMenuItem(
                  value: 4,
                  child: Text(t.alerts_dialog_severity_high),
                ),
                DropdownMenuItem(
                  value: 5,
                  child: Text(t.alerts_dialog_severity_extreme),
                ),
              ],
              onChanged: (int? newValue) {
                setState(() {
                  selectedSeverity = newValue ?? 0;
                });
              },
            ),
            CheckboxListTile(
              title: Text(t.alerts_dialog_check_ack),
              value: isAck,
              contentPadding: EdgeInsets.zero,
              onChanged: (bool? value) {
                setState(() {
                  isAck = value ?? false;
                });
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
            final String commentValue = _controller.text;
            final userViewModel = context.read<UserViewModel>();
            final currentUser = userViewModel.user;

            final String currentAuthor = currentUser != null
                ? currentUser.login
                : "Mobile User";
            context.read<AlertsViewModel>().acknowledgeAlert(
              widget.alertId,
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

void _openAckDialog(BuildContext context, int alertId, AppLocalizations t) {
  showDialog(
    context: context,
    builder: (context) => AckDialog(alertId: alertId, t: t),
  );
}
