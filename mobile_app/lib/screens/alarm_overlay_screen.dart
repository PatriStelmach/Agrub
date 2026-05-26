import 'package:flutter/material.dart';
import 'package:alert_app/data/services/navigation_service.dart';
import 'package:alert_app/l10n/app_localizations.dart';

class AlarmOverlayScreen extends StatelessWidget {
  const AlarmOverlayScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Container(
        alignment: Alignment.center,
        child: Material(
          color: Colors.red,
          borderRadius: BorderRadius.circular(20),
          child: Padding(
            padding: const EdgeInsets.all(40.0),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  t.overlay_extreme_alert,
                  style: TextStyle(color: Colors.white, fontSize: 30),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: () {
                    navigationService.stopAlarmAndDismiss();
                  },
                  child: Text(t.overlay_button_stop),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
