import 'package:alert_app/data/models/problem_action_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';

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

    _syncData();
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
      _syncData();
    }
  }

  void _syncData() async {
    final repo = context.read<AlertRepository>();
    await repo.syncCacheWithSharedPreferences();
    await repo.updateAllAlerts();
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("--- REBUILD ---");
    final alertsViewModel = context.watch<AlertsViewModel>();

    final currentSort = alertsViewModel.currentSortProperty;
    final isAsc = alertsViewModel.isAscending;

    if (alertsViewModel.alertsList.isEmpty) {
      return const Scaffold(
        body: Center(
          child: Text(
            "No alerts found. Consider checking the button in Debug Screen!",
          ),
        ),
      );
    }

    final sortedList = alertsViewModel.sortedAlerts;

    return Column(
      children: [
        Row(
          children: [
            Text("Sort by - ", style: TextStyle(fontSize: 30)),
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
                items: const [
                  DropdownMenuItem<String>(
                    value: 'id',
                    child: Text('ID', style: TextStyle(fontSize: 30)),
                  ),
                  DropdownMenuItem<String>(
                    value: 'title',
                    child: Text('Title', style: TextStyle(fontSize: 30)),
                  ),
                  DropdownMenuItem<String>(
                    value: 'createdAt',
                    child: Text('CreatedAt', style: TextStyle(fontSize: 30)),
                  ),
                  DropdownMenuItem<String>(
                    value: 'acknowledged',
                    child: Text('Acknowledged', style: TextStyle(fontSize: 30)),
                  ),
                  DropdownMenuItem<String>(
                    value: 'severity',
                    child: Text('Severity', style: TextStyle(fontSize: 30)),
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
                      alert?.subject ?? 'No Title',
                      style: TextStyle(
                        color: textColor,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    subtitle: Text(
                      alert.acknowledged ? 'ACKNOWLEDGED' : 'NOT ACKNOWLEDGED',
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
                                  alert?.source ?? 'Unknown Host',
                                  style: TextStyle(color: textColor),
                                ),
                                const Spacer(),
                                Text(
                                  alert?.createdAt != null
                                      ? DateFormat(
                                          'dd.MM.yyyy HH:mm:ss',
                                        ).format(alert!.createdAt)
                                      : 'Unknown Time',
                                  style: TextStyle(color: textColor),
                                ),
                              ],
                            ),
                            const SizedBox(height: 8),
                            Row(
                              children: [
                                const Spacer(),
                                Text(
                                  "Severity: ${alert!.severity.label}",
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
                                  _openAckDialog(context, alert.id);
                                },
                                child: const Text('Actions'),
                              ),
                            ),
                            FutureBuilder<ProblemAction?>(
                              future: context
                                  .read<AlertRepository>()
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
                                      "Brak historii alertu",
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
                                        : 'Brak komentarza',
                                    style: theme.textTheme.bodyLarge?.copyWith(
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                  subtitle: Text(
                                    "Autor: ${latestAction.author} | Status ACK: ${latestAction.ack ? 'Tak' : 'Nie'}",
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
  const AckDialog({super.key, required this.alertId});

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

    //TEMPORARY! Need to change cache to View Model!
    final alertRepo = context.read<AlertRepository>();
    final currentAlert = alertRepo.alertsCache[widget.alertId];

    if (currentAlert != null) {
      selectedSeverity = currentAlert.severity.index;
      isAck = currentAlert.acknowledged;
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text('Actions for Alert ${widget.alertId}'),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _controller,
              decoration: const InputDecoration(labelText: 'Your comment'),
            ),
            const SizedBox(height: 15),

            const Text(
              "Change Severity:",
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
              items: const [
                DropdownMenuItem(value: 0, child: Text('0 - Info')),
                DropdownMenuItem(value: 1, child: Text('1 - Low')),
                DropdownMenuItem(value: 2, child: Text('2 - Low')),
                DropdownMenuItem(value: 3, child: Text('3 - Medium')),
                DropdownMenuItem(value: 4, child: Text('4 - High')),
                DropdownMenuItem(value: 5, child: Text('5 - Extreme')),
              ],
              onChanged: (int? newValue) {
                setState(() {
                  selectedSeverity = newValue ?? 0;
                });
              },
            ),
            CheckboxListTile(
              title: const Text("Acknowledge alert"),
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
          child: const Text('Cancel'),
        ),

        ElevatedButton(
          onPressed: () {
            final String commentValue = _controller.text;
            context.read<AlertsViewModel>().acknowledgeAlert(
              widget.alertId,
              comment: commentValue,
              isAck: this.isAck,
            );
            Navigator.pop(context);
          },
          child: const Text('Update'),
        ),
      ],
    );
  }
}

void _openAckDialog(BuildContext context, int alertId) {
  showDialog(
    context: context,
    builder: (context) => AckDialog(alertId: alertId),
  );
}
