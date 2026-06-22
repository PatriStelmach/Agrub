import 'package:alert_app/data/models/alert_action_model.dart';
import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/services/navigation_service.dart';
import 'package:alert_app/locator.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart';
import 'package:flutter_client_sse/flutter_client_sse.dart';

class AlertRemoteDataSource {
  final Dio dio;

  AlertRemoteDataSource({required this.dio});

  /// Fetching all active alerts from backend endpoint and returning them as a map
  Future<List<Alert>> fetchActiveAlerts() async {
    try {
      final response = await dio.get('/api/alerts/active');
      final List<dynamic> rawData = response.data;

      return rawData
          .map((rawItem) => Alert.fromJson(rawItem as Map<String, dynamic>))
          .toList();
    } catch (e) {
      await locator<NavigationService>().showEmergencyOverlay('Connection');

      debugPrint(
        "ALERT REMOTE DATA SOURCE - Connection error - fetchActiveAlerts() failed",
      );
      rethrow;
    }
  }

  /// Sending ACK or comment for particular alert
  Future<void> acknowledgeAlert({
    required int alertId,
    required String author,
    required String message,
    required int newSeverity,
    required bool isAck,
  }) async {
    final requestBody = {
      "alertId": alertId,
      "author": author,
      "ack": isAck,
      "message": message,
      "newSeverity": newSeverity,
    };

    try {
      await dio.post('/api/alerts/$alertId/ack', data: requestBody);
    } catch (e) {
      debugPrint(
        "ALERT REMOTE DATA SOURCE - Connection error - acknowledgeAlert() failed",
      );
      await locator<NavigationService>().showEmergencyOverlay('Connection');

      rethrow;
    }
  }

  /// Fetching newest action or comment for particular alert
  Future<AlertAction?> fetchLatestAction(int alertId) async {
    try {
      final response = await dio.get('/api/alerts/$alertId/actions');
      if (response.data is List) {
        final List<dynamic> rawData = response.data as List<dynamic>;
        if (rawData.isNotEmpty) {
          return AlertAction.fromJson(rawData.first as Map<String, dynamic>);
        }
      }
    } catch (e) {
      debugPrint(
        "ALERT REMOTE DATA SOURCE - Connection error - fetchLatestAction() failed",
      );
      await locator<NavigationService>().showEmergencyOverlay('Connection');
    }
    return null;
  }

  /// Connecting to SSE and returning Stream
  Stream<SSEModel> getAlertsStream({
    required String userRole,
    required String token,
  }) {
    final String sseUrl =
        '${dio.options.baseUrl}/api/alerts/stream?groups=ADMINISTRATOR';

    return SSEClient.subscribeToSSE(
      method: SSERequestType.GET,
      url: sseUrl,
      header: {
        "Accept": "text/event-stream",
        "Cache-Control": "no-cache",
        "Authorization": "Bearer $token",
      },
    );
  }

  ///Ping check for manual pinging
  Future<bool> isBackendConnected() async {
    try {
      await fetchActiveAlerts();
      return true;
    } on DioException catch (_) {
      return false;
    }
  }
}
