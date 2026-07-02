import 'package:alert_app/data/models/alert_action_model.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:flutter/material.dart';

class ActionHistory extends StatelessWidget {
  final int alertId;
  final AlertsViewModel alertsViewModel;
  final AppLocalizations t;

  const ActionHistory({
    super.key,
    required this.alertId,
    required this.alertsViewModel,
    required this.t,
  });

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<AlertAction?>(
      future: alertsViewModel.getLatestActionForAlert(alertId),
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
            Icons.history,
            color: theme.iconTheme.color?.withValues() ?? Colors.grey,
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
              latestAction.ack ? t.yes : t.no,
              latestAction.author,
            ),
            style: theme.textTheme.bodyMedium?.copyWith(
              color: theme.textTheme.bodyMedium?.color?.withValues(alpha: 0.7),
            ),
          ),
        );
      },
    );
  }
}
