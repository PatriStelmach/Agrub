import 'dart:async';
import 'dart:convert';
import 'package:alert_app/data/datasources/alert_local_data_source.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/models/problem_action_model.dart';
import 'package:alert_app/data/datasources/alert_remote_data_source.dart';

///Getting alerts data from remote and local sources and feeding it to Alerts View Model
class AlertRepository {
  final AlertRemoteDataSource remoteDataSource;
  final AlertLocalDataSource localDataSource;
  AlertRepository({
    required this.remoteDataSource,
    required this.localDataSource,
  });

  ///Getting all active alerts via alert data source from the server
  Future<List<Alert>> fetchAllAlerts() async {
    try {
      final alerts = await remoteDataSource.fetchActiveAlerts();

      await localDataSource.cacheAlerts(alerts);

      return alerts;
    } catch (e) {
      debugPrint("REPO ERROR: Couldn't fetch alerts: $e");
      return await getOfflineAlerts();
    }
  }

  ///Getting all locally stored alerts
  Future<List<Alert>> getOfflineAlerts() async {
    return await localDataSource.getOfflineAlerts();
  }

  /// Send ack/comment and fetch actual list
  Future<void> acknowledgeAlert({
    required int alertId,
    required String author,
    required String message,
    required int newSeverity,
    required bool isAck,
  }) async {
    await remoteDataSource.acknowledgeAlert(
      alertId: alertId,
      author: author,
      message: message,
      newSeverity: newSeverity,
      isAck: isAck,
    );
  }

  /// Fetch newest action for an alert
  Future<ProblemAction?> getLatestActionForAlert(int alertId) async {
    return await remoteDataSource.fetchLatestActionForAlert(alertId);
  }

  /// Fetch Stream via data source
  Stream<dynamic> getAlertsUpdateStream({
    required String userGroup,
    required String token,
  }) {
    return remoteDataSource
        .getAlertsStream(userGroup: userGroup, token: token)
        .map((event) {
          if (event.data == null || event.data!.isEmpty) return null;

          final String rawData = event.data!.trim();
          if (!rawData.startsWith('{') && !rawData.startsWith('[')) return null;

          try {
            final Map<String, dynamic> decodedData = jsonDecode(rawData);
            final String eventType = decodedData['eventType'] ?? '';
            final dynamic message = decodedData['message'];

            if (eventType == 'ALERT_UPDATE' ||
                eventType == 'ALERT_UPDATE_ONLY') {
              return message;
            }
          } catch (e) {
            debugPrint("REPO SSE ERROR: Błąd parsowania zdarzenia: $e");
          }
          return null;
        })
        .where((event) => event != null);
  }

  Future<void> saveAlertsToOfflineCache(List<Alert> alerts) async {
    try {
      await localDataSource.cacheAlerts(alerts);
    } catch (e) {
      debugPrint("ALERT REPOSITORY ERROR: Problem with saving offline: $e");
    }
  }

  Future<void> markAlertAsNotified(int alertId) async {
    await localDataSource.markAlertAsNotified(alertId);
    debugPrint("Saved $alertId as sounded by LocalDataSource.");
  }

  Future<bool> isAlertAlreadyNotified(int alertId) async {
    return await localDataSource.isAlertAlreadyNotified(alertId);
  }
}
