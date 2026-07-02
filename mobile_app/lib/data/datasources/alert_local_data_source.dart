import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:alert_app/data/models/alert_model.dart';

/// Data source governing local storage of the mobile device, using Shared Preferences
class AlertLocalDataSource {
  final SharedPreferences sharedPreferences;

  static const _storedAlertsKey = 'ALERTS_STORAGE';
  static const _storedNotifiedAlertsKey = 'ALERTS_NOTIFIED_STORAGE';

  AlertLocalDataSource({required this.sharedPreferences});

  ///Reading stored alerts from Shared Preferences, if such stored record exists, otherwise returning empty List
  Future<List<Alert>> getStoredAlerts() async {
    final readAlerts = sharedPreferences.getString(_storedAlertsKey);

    if (readAlerts != null) {
      final List<dynamic> decodedAlerts = jsonDecode(readAlerts);

      return decodedAlerts.map((jsonMap) => Alert.fromJson(jsonMap)).toList();
    } else {
      return [];
    }
  }

  ///Saving Alerts to local storage in SharedPreferences
  Future<void> saveAlerts(List<Alert> alertsToSave) async {
    final List<Map<String, dynamic>> preparedData = alertsToSave
        .map((alert) => alert.toJson())
        .toList();

    final preparedJson = json.encode(preparedData);
    await sharedPreferences.setString(_storedAlertsKey, preparedJson);
  }

  ///Boolean method to check if alarm was played for a critical alert, prepared for no stored data
  Future<bool> wasAlertAlreadyNotified(int alertId) async {
    final List<String> notifiedAlertsIds =
        sharedPreferences.getStringList(_storedNotifiedAlertsKey) ?? [];
    return notifiedAlertsIds.contains(alertId.toString());
  }

  ///Marking notified alert
  Future<void> markAlertAsNotified(int alertId) async {
    final List<String> notifiedAlertsIds =
        sharedPreferences.getStringList(_storedNotifiedAlertsKey) ?? [];

    if (!notifiedAlertsIds.contains(alertId.toString())) {
      notifiedAlertsIds.add(alertId.toString());
    }
    await sharedPreferences.setStringList(
      _storedNotifiedAlertsKey,
      notifiedAlertsIds,
    );
  }
}
