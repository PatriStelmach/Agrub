import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class AlertDetails extends StatelessWidget {
  final Alert alert;
  final Color textColor;
  final AppLocalizations t;

  const AlertDetails({
    super.key,
    required this.alert,
    required this.textColor,
    required this.t,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(alert.source, style: TextStyle(color: textColor)),
            Text(
              DateFormat('dd.MM.yyyy HH:mm:ss').format(alert.createdAt),

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
}
