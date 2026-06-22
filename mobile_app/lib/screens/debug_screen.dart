import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/logic/debug_view_model.dart';
import 'package:alert_app/data/services/navigation_service.dart';
import 'package:provider/provider.dart';
import 'package:alert_app/data/services/push_notification_service.dart';

class DebugScreen extends StatelessWidget {
  const DebugScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final navigation = AlarmService();
    final debugViewModel = DebugViewModel(navigationService: navigation);
    return Column(
      children: [
        const Text("BACKEND DEBUG", style: TextStyle(fontSize: 30)),

        Row(
          children: [
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                  onPressed: () {},
                  child: const Text('Former ping, now placeholder'),
                ),
              ),
            ),
          ],
        ),

        const Text("MOCK DEBUG FUNCTIONS", style: TextStyle(fontSize: 30)),

        Row(
          children: [
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                  onPressed: () {
                    debugViewModel.alarmPressed();
                  },
                  child: const Text('Trigger extreme alert alarm'),
                ),
              ),
            ),
          ],
        ),

        Row(
          children: [
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                  onPressed: () {
                    Provider.of<PluginRepository>(
                      context,
                      listen: false,
                    ).fetchAllPlugins();
                  },
                  child: const Text(' Push - new plugins'),
                ),
              ),
            ),
          ],
        ),
        Row(
          children: [
            Expanded(
              child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: ElevatedButton(
                  onPressed: () {
                    Provider.of<AlertRepository>(
                      context,
                      listen: false,
                    ).fetchAllAlerts();
                  },
                  child: const Text('Push - new alerts(with extreme)'),
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }
}
