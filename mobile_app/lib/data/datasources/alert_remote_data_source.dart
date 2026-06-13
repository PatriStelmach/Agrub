import 'package:alert_app/data/models/history_alert_model.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart';
import 'package:flutter_client_sse/flutter_client_sse.dart';
import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/models/problem_action_model.dart';

///
abstract class AlertRemoteDataSource {
  Future<List<Alert>> fetchActiveAlerts();
  Future<List<HistoryAlert>> fetchHistoryAlerts();

  Future<void> acknowledgeAlert({
    required int alertId,
    required String author,
    required String message,
    required int newSeverity,
    required bool isAck,
  });

  Future<ProblemAction?> fetchLatestActionForAlert(int alertId);

  Stream<SSEModel> getAlertsStream({
    required String userGroup,
    required String token,
  });
}

class AlertRemoteDataSourceImpl implements AlertRemoteDataSource {
  final Dio dio;

  AlertRemoteDataSourceImpl({required this.dio});

  /// Fetching all active alerts from backend endpoint and returning them as a map
  @override
  Future<List<Alert>> fetchActiveAlerts() async {
    try {
      final response = await dio.get('/api/alerts/active');
      final List<dynamic> data = response.data;

      return data
          .map((item) => Alert.fromJson(item as Map<String, dynamic>))
          .toList();
    } catch (e) {
      debugPrint(
        "ALERT REMOTE DATA SOURCE ERROR: Fetch all alerts error - $e ",
      );
      rethrow;
    }
  }

  /// Fetching all history alerts from backend endpoint and returning them as a map
  @override
  Future<List<HistoryAlert>> fetchHistoryAlerts() async {
    try {
      final response = await dio.get('/api/alerts/history/');
      final List<dynamic> data = response.data;

      return data
          .map((item) => HistoryAlert.fromJson(item as Map<String, dynamic>))
          .toList();
    } catch (e) {
      debugPrint(
        "ALERT REMOTE DATA SOURCE ERROR: Fetch all alerts error - $e ",
      );
      rethrow;
    }
  }

  /// Sending ACK or comment to particular alert
  @override
  Future<void> acknowledgeAlert({
    required int alertId,
    required String author,
    required String message,
    required int newSeverity,
    required bool isAck,
  }) async {
    try {
      final requestBody = {
        "alertId": alertId,
        "author": author,
        "ack": isAck,
        "message": message,
        "newSeverity": newSeverity,
      };
      await dio.post('/api/alerts/$alertId/ack', data: requestBody);
    } on DioException catch (e) {
      debugPrint(
        "ALERT REMOTE DATA SOURCE: Dio Error -  ${e.response?.statusCode} - ${e.message}",
      );

      rethrow;
    } catch (e) {
      debugPrint(
        "ALERT REMOTE DATA SOURCE ERROR: Acknowledge or comment error - $e ",
      );
      rethrow;
    }
  }

  /// Fetching newest action or comment for particular alert
  @override
  Future<ProblemAction?> fetchLatestActionForAlert(int alertId) async {
    try {
      final response = await dio.get('/api/alerts/$alertId/actions');

      if (response.data is List) {
        final List<dynamic> data = response.data as List<dynamic>;
        if (data.isNotEmpty) {
          return ProblemAction.fromJson(data.first as Map<String, dynamic>);
        }
      }
      return null;
    } on DioException catch (e) {
      debugPrint(
        "ALERT REMOTE DATA SOURCE: Dio Error -  ${e.response?.statusCode} - ${e.message}",
      );

      rethrow;
    } catch (e) {
      debugPrint(
        "ALERT REMOTE DATA SOURCE ERROR: Acknowledge or comment error - {$e} ",
      );
      rethrow;
    }
  }

  /// Connecting to SSE and returning Stream
  /// DataSource isn't a listener to the stream, just pass it to higher layers of the app
  @override
  Stream<SSEModel> getAlertsStream({
    required String userGroup,
    required String token,
  }) {
    final String sseUrl =
        '${dio.options.baseUrl}/api/alerts/stream?groups=$userGroup';

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
}
