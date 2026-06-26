import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/screens/alert/card_elements/ack_dialog.dart';
import 'package:alert_app/screens/alert/card_elements/action_history.dart';
import 'package:alert_app/screens/alert/card_elements/alert_details.dart';
import 'package:flutter/material.dart';

class AlertCard extends StatelessWidget {
  final Alert alert;
  final AlertsViewModel viewModel;
  final AppLocalizations t;

  const AlertCard({
    super.key,
    required this.alert,
    required this.viewModel,
    required this.t,
  });

  @override
  Widget build(BuildContext context) {
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
          leading: Icon(Icons.warning, color: textColor),
          title: Text(
            alert.subject,
            style: TextStyle(color: textColor, fontWeight: FontWeight.bold),
          ),
          subtitle: Text(
            alert.acknowledged
                ? t.alerts_status_acknowledged
                : t.alerts_status_not_acknowledged,
            style: TextStyle(color: textColor.withValues()),
          ),
          children: [
            Padding(
              padding: const EdgeInsets.all(12.0),
              child: Column(
                children: [
                  AlertDetails(alert: alert, textColor: textColor, t: t),
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
                  ActionHistory(
                    alertId: alert.id,
                    alertsViewModel: viewModel,
                    t: t,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _openAckDialog(BuildContext context, Alert alert, AppLocalizations t) {
    showDialog(
      context: context,
      builder: (context) => AckDialog(alert: alert, t: t),
    );
  }
}
