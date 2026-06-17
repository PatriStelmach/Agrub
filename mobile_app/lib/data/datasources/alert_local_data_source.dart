import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:alert_app/data/models/alert_model.dart';

class AlertLocalDataSource {
  final SharedPreferences sharedPreferences;

  // Keys to local memory storage
  static const cachedAlertsKey = 'CACHED_ALERTS';
  static const notifiedAlertsKey = 'NOTIFIED_ALERT_IDS';

  AlertLocalDataSource({required this.sharedPreferences});

  Future<List<Alert>> getOfflineAlerts() async {
    final jsonString = sharedPreferences.getString(cachedAlertsKey);

    if (jsonString != null) {
      final List<dynamic> decodedJson = json.decode(jsonString);
      return decodedJson.map((jsonMap) => Alert.fromJson(jsonMap)).toList();
    } else {
      return [];
    }
  }

  Future<void> cacheAlerts(List<Alert> alertsToCache) async {
    final List<Map<String, dynamic>> jsonList = alertsToCache
        .map((alert) => alert.toJson())
        .toList();

    final jsonString = json.encode(jsonList);
    await sharedPreferences.setString(cachedAlertsKey, jsonString);
  }

  Future<bool> isAlertAlreadyNotified(int alertId) async {
    final List<String> notifiedIds =
        sharedPreferences.getStringList(notifiedAlertsKey) ?? [];
    return notifiedIds.contains(alertId.toString());
  }

  Future<void> markAlertAsNotified(int alertId) async {
    final List<String> notifiedIds =
        sharedPreferences.getStringList(notifiedAlertsKey) ?? [];

    if (!notifiedIds.contains(alertId.toString())) {
      notifiedIds.add(alertId.toString());
      await sharedPreferences.setStringList(notifiedAlertsKey, notifiedIds);
    }
  }
}
