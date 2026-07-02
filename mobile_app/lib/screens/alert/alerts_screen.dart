import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/screens/alert/alert_card.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:alert_app/l10n/app_localizations.dart';

class AlertsScreen extends StatefulWidget {
  const AlertsScreen({super.key});

  @override
  State<AlertsScreen> createState() => _AlertsScreenState();
}

class _AlertsScreenState extends State<AlertsScreen>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        context.read<AlertsViewModel>().fetchInitialAlerts();
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
      debugPrint("ALERTS SCREEN - Application resumed, fetching alerts");
      context.read<AlertsViewModel>().fetchInitialAlerts();
    }
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("ALERTS SCREEN - Screen rebuild");
    final alertsViewModel = context.watch<AlertsViewModel>();
    final t = AppLocalizations.of(context)!;

    if (alertsViewModel.isLoading && alertsViewModel.alertsList.isEmpty) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (alertsViewModel.alertsList.isEmpty) {
      return Scaffold(body: Center(child: Text(t.alerts_no_alerts_found)));
    }

    final sortedList = alertsViewModel.sortAlerts(
      alertsViewModel.currentSortProperty,
      alertsViewModel.isAscending,
    );
    return Column(
      children: [
        _buildSortHeader(context, alertsViewModel, t),
        Expanded(
          child: RefreshIndicator(
            onRefresh: () async {
              await alertsViewModel.fetchInitialAlerts();
            },
            child: ListView.builder(
              itemCount: sortedList.length,
              itemBuilder: (context, index) {
                final alert = sortedList[index];
                return AlertCard(
                  alert: alert,
                  viewModel: alertsViewModel,
                  t: t,
                );
              },
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildSortHeader(
    BuildContext context,
    AlertsViewModel alertsViewModel,
    AppLocalizations t,
  ) {
    final currentSort = alertsViewModel.currentSortProperty;
    final isAsc = alertsViewModel.isAscending;

    return Row(
      children: [
        Text(t.alerts_sort_by, style: const TextStyle(fontSize: 25)),
        Expanded(
          child: DropdownButton<String>(
            isExpanded: true,
            padding: const EdgeInsets.all(6),
            borderRadius: const BorderRadius.all(Radius.circular(16)),
            value: currentSort,
            icon: const Icon(Icons.menu),
            style: TextStyle(
              color: Theme.of(context).textTheme.bodyLarge?.color,
              fontSize: 18,
              fontFamily: 'JetBrainsMono',
            ),
            onChanged: (String? newValue) {
              if (newValue != null) {
                alertsViewModel.sortAlertsBy(newValue, isAsc);
              }
            },
            items: [
              DropdownMenuItem(
                value: 'id',
                child: Text(
                  t.alerts_sort_id,
                  style: const TextStyle(fontSize: 25),
                ),
              ),
              DropdownMenuItem(
                value: 'title',
                child: Text(
                  t.alerts_sort_title,
                  style: const TextStyle(fontSize: 20),
                ),
              ),
              DropdownMenuItem(
                value: 'createdAt',
                child: Text(
                  t.alerts_sort_created_at,
                  style: const TextStyle(fontSize: 20),
                ),
              ),
              DropdownMenuItem(
                value: 'acknowledged',
                child: Text(
                  t.alerts_sort_acknowledged,
                  style: const TextStyle(fontSize: 20),
                ),
              ),
              DropdownMenuItem(
                value: 'severity',
                child: Text(
                  t.alerts_sort_severity,
                  style: const TextStyle(fontSize: 20),
                ),
              ),
            ],
          ),
        ),
        IconButton(
          icon: Icon(isAsc ? Icons.arrow_upward : Icons.arrow_downward),
          onPressed: () {
            alertsViewModel.sortAlertsBy(currentSort, !isAsc);
          },
        ),
      ],
    );
  }
}
