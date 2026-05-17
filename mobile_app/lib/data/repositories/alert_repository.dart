import 'dart:async';
import 'dart:convert';
import 'package:alert_app/data/models/alert_model.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/services/navigation_service.dart';
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart';
import 'package:flutter_client_sse/flutter_client_sse.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AlertRepository extends ChangeNotifier {

final Dio dio;
AlertRepository({required this.dio});
final Map<int, Alert> alertsCache = {};
//remembering extreme alerts that triggered alert screen
final Set<int> notifiedAlertIds = {};
  final _storage = const FlutterSecureStorage();

StreamSubscription? _sseSubscription;

DateTime lastPing = DateTime.now();

AlertSeverity _mapIntToSeverity(int value) {
  switch (value) {
    case 5:
      return AlertSeverity.extreme;
    case 4:
      return AlertSeverity.high;
    case 3:
      return AlertSeverity.medium;
    case 2:
      return AlertSeverity.low;
    default:
      return AlertSeverity.info;
  }
}




Future<void> initSseConnection() async {
  const String sseUrl = 'http://10.0.2.2:10000/api/alerts/stream?groups=ADMIN';

  _sseSubscription?.cancel();

  String? token = await _storage.read(key: 'jwt_token');

  if (token == null) {
    debugPrint("SSE ERROR: No JWT Token.");
    return;
  }

// KK:pobranie tokenu

SSEClient.subscribeToSSE(
      method: SSERequestType.GET,
      url: sseUrl,
      header: {
        "Accept": "text/event-stream",
        "Cache-Control": "no-cache",
        "Authorization": "Bearer $token",
      },
    ).listen((event) {
      debugPrint("SSE Raw Event: ${event.data}");
      if (event.data != null && event.data!.isNotEmpty) {
        try {
        // Parsing String -> Map
        final Map<String, dynamic> decodedData = jsonDecode(event.data!);
        final String eventType = decodedData['eventType'] ?? '';
        final dynamic message = decodedData['message'];

        // 2. Logika "Dystrybutora" zdarzeń
        if (eventType == 'ALERT_UPDATE' || eventType == 'ALERT_UPDATE_ONLY') {
          handleSingleAlertUpdate(message);
        } else {
          debugPrint("Otrzymano inny typ zdarzenia: $eventType");
        }
        
      } catch (e) {
        debugPrint("DEBUG: SSE prasing error: $e");
      }
    }
  });
}

      
   

//MOCK: getting alerts from local JSON
//FINAL: updating full list via REST when opening the app
Future<void> updateAllAlerts() async {


final response = await dio.get('http://10.0.2.2:10000/api/alerts/active');


      final List<dynamic> data = response.data;
      
      final List<Alert> parsedAlerts = data.map((item) {
        return Alert.fromJson(item as Map<String, dynamic>);
      }).toList();

    _processAlerts(parsedAlerts);
}


void handleSingleAlertUpdate(dynamic message) {
    debugPrint("SSE Message: $message");
    if (message is! Map<String, dynamic>) return;

    final int? id = message['alertId'];
    
    // Jeśli to pełny obiekt Alert (ma pole subject)
    if (message.containsKey('subject')) {
      final alert = Alert.fromJson(message);
      _processAlerts([alert]);
      return;
    }

    // Jeśli to częściowy update i mamy go w cache
    if (id != null && alertsCache.containsKey(id)) {
      final existing = alertsCache[id]!;
      Alert updated = existing;

      // Update Severity
      if (message.containsKey('newSeverity')) {
        updated = updated.copyWith(severity: _mapIntToSeverity(message['newSeverity']));
      }

      // Update ACK status
      if (message.containsKey('ack')) {
        updated = updated.copyWith(acknowledged: message['ack']);
      }

      // Update Komentarza (obsługa comment z endpointu ack)
      if (message.containsKey('comment') || message.containsKey('message')) {
        updated = updated.copyWith(
          message: message['comment'] ?? message['message'], 
        );
      }

      alertsCache[id] = updated;
      notifyListeners();
    } else if (id != null) {
      // Nie mamy w cache? Pobieramy listę, by być up-to-date
      updateAllAlerts();
    }
  }


void _processAlerts(List<Alert> incomingAlerts) {
    debugPrint("Repo: Procesuję ${incomingAlerts.length} alertów");

    bool alarmOverlayTrigger = false;


    for (var alert in incomingAlerts) {

      alertsCache[alert.id] = alert;

      debugPrint("DEBUG: Analysis of Alert ID: ${alert.id}");
      debugPrint(" DEBUG: Show severity: ${alert.severity}");
      debugPrint(" DEBUG: Is ID already in notifedAlertIds list>? ${notifiedAlertIds.contains(alert.id)}");

      if (alert.severity == AlertSeverity.extreme && !notifiedAlertIds.contains(alert.id)) {
        debugPrint("DEBUG: Alert is new");
        notifiedAlertIds.add(alert.id);
        alarmOverlayTrigger = true;
        
      } else {
        debugPrint("DEBUG: Alert is not new");
      }
   
    }

    if(alarmOverlayTrigger) {
      debugPrint(" DEBUG: Calling showEmergencyOverlay() method");
      navigationService.showEmergencyOverlay();
    } else {
      
      debugPrint(" DEBUG: No new extreme alerts.");
        
        }

  notifyListeners();
  debugPrint("--- [DEBUG END] ---");

}

Future<void> sendAcknowledge(int alertId, {String? comment, bool isAck = true}) async {
  


final String actionType = isAck ? "ACK" : "COMMENT";
final String commentText = comment ?? "";

final originalAlert = alertsCache[alertId];
    if (originalAlert != null) {
      alertsCache[alertId] = originalAlert.copyWith(
        acknowledged: isAck,
        message: commentText.isNotEmpty ? commentText : originalAlert.message,
      );
      notifyListeners();
    }

final Map<String, dynamic> requestBody = {
    "message": comment ?? "",
    "actionType": actionType,
    "author": "Mobile User",
  };


  try {
      final response = await dio.post(
        'http://10.0.2.2:10000/api/alerts/$alertId/ack',
        data: requestBody,
      );

      if (response.statusCode == 200) {
        debugPrint('ACK SUCCESS: Alert $alertId acknowledged.');
      }
    } on DioException catch (e) {
  debugPrint('--- [Detailed Error Log] ---');
  debugPrint('Status Code: ${e.response?.statusCode}');
  debugPrint('Response Data: ${e.response?.data}'); 
  debugPrint('Headers: ${e.response?.headers}');
    } catch (e) {
      debugPrint('ACK UNKNOWN ERROR: $e');
    }
  }
}