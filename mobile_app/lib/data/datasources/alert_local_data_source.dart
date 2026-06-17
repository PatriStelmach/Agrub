import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:alert_app/data/models/alert_model.dart';

abstract class AlertLocalDataSource {
  Future<List<Alert>> getOfflineAlerts();
  Future<void> cacheAlerts(List<Alert> alertsToCache);
  Future<bool> isAlertAlreadyNotified(int alertId);
  Future<void> markAlertAsNotified(int alertId);
  Future<void> clearAllData();
}

class AlertLocalDataSourceImpl implements AlertLocalDataSource {
  final SharedPreferences sharedPreferences;

  static const cachedAlertsKey = 'CACHED_ALERTS';
  static const notifiedAlertsKey = 'NOTIFIED_ALERT_IDS';

  AlertLocalDataSourceImpl({required this.sharedPreferences});

  @override
  Future<List<Alert>> getOfflineAlerts() async {
    final jsonString = sharedPreferences.getString(cachedAlertsKey);

    if (jsonString != null) {
      final List<dynamic> decodedJson = json.decode(jsonString);
      return decodedJson.map((jsonMap) => Alert.fromJson(jsonMap)).toList();
    } else {
      return [];
    }
  }

  @override
  Future<void> cacheAlerts(List<Alert> alertsToCache) async {
    final List<Map<String, dynamic>> jsonList = alertsToCache
        .map((alert) => alert.toJson())
        .toList();

    final jsonString = json.encode(jsonList);
    await sharedPreferences.setString(cachedAlertsKey, jsonString);
  }

  @override
  Future<bool> isAlertAlreadyNotified(int alertId) async {
    final List<String> notifiedIds =
        sharedPreferences.getStringList(notifiedAlertsKey) ?? [];
    return notifiedIds.contains(alertId.toString());
  }

  @override
  Future<void> markAlertAsNotified(int alertId) async {
    final List<String> notifiedIds =
        sharedPreferences.getStringList(notifiedAlertsKey) ?? [];

    if (!notifiedIds.contains(alertId.toString())) {
      notifiedIds.add(alertId.toString());
      await sharedPreferences.setStringList(notifiedAlertsKey, notifiedIds);
    }
  }

  @override
  Future<void> clearAllData() async {
    await sharedPreferences.remove(cachedAlertsKey);
    await sharedPreferences.remove(notifiedAlertsKey);
  }
}
