import 'package:alert_app/logic/general_layout_view_model.dart';
import 'package:alert_app/screens/home/nav_tile.dart';
import 'package:alert_app/screens/home/stat_tile.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:alert_app/l10n/app_localizations.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  void initState() {
    super.initState();
    final homeViewModel = context.read<HomeViewModel>();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      homeViewModel.triggerPingAndNotify();
    });
  }

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;
    final homeViewModel = context.watch<HomeViewModel>();

    final int activeAlertsCount = homeViewModel.activeAlertsCount;
    final criticalAlerts = homeViewModel.latestCriticalAlerts();
    final latestCritical = criticalAlerts.isNotEmpty
        ? criticalAlerts.first
        : null;

    return Scaffold(
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                StatCard(
                  title: homeViewModel.lastPing == false
                      ? t.ping_no_connection
                      : t.ping_last_ping,
                  value: homeViewModel.lastPing == false
                      ? t.ping_no_connection
                      : DateFormat('HH:mm:ss a').format(DateTime.now()),
                  icon: Icons.sync,
                  color: homeViewModel.lastPing == false
                      ? Colors.redAccent
                      : Colors.lightGreenAccent,
                ),

                const SizedBox(width: 12),
                StatCard(
                  title: t.statActiveAlerts,
                  value: activeAlertsCount.toString(),
                  icon: Icons.notifications_active,
                  color: activeAlertsCount > 0 ? Colors.orange : Colors.green,
                ),
              ],
            ),

            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: () => homeViewModel.triggerPingAndNotify(),
                    child: const Text("Ping!"),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),

            Text(
              t.latestCriticalSection,
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            if (latestCritical != null)
              Card(
                elevation: 4,
                color: latestCritical
                    .severityColor(context)
                    .withValues(alpha: 0.9),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                child: ListTile(
                  contentPadding: const EdgeInsets.all(16),
                  leading: const CircleAvatar(
                    backgroundColor: Colors.white,
                    child: Icon(Icons.priority_high, color: Colors.red),
                  ),
                  title: Text(
                    latestCritical.subject,
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 18,
                    ),
                  ),
                  subtitle: Text(t.alertStatusAcknowledged),
                  isThreeLine: true,
                ),
              )
            else
              Card(
                child: ListTile(
                  title: Text(t.noCriticalAlerts),
                  leading: const Icon(Icons.check_circle, color: Colors.green),
                ),
              ),

            const SizedBox(height: 24),

            Text(
              t.quickNavigationSection,
              style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                NavTile(
                  label: t.navAllAlerts,
                  icon: Icons.list_alt,
                  color: Colors.indigo,
                  onTap: () {
                    context.read<GeneralLayoutViewModel>().changePage(
                      AppScreen.alerts,
                    );
                  },
                ),
                const SizedBox(width: 12),
                NavTile(
                  label: t.navPlugins,
                  icon: Icons.extension,
                  color: Colors.teal,
                  onTap: () {
                    context.read<GeneralLayoutViewModel>().changePage(
                      AppScreen.plugins,
                    );
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
