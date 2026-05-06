import 'dart:convert';

import 'package:alert_app/data/models/alert_model.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/services/navigation_service.dart';
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_client_sse/flutter_client_sse.dart';

class AlertRepository extends ChangeNotifier {

final Map<int, Alert> alertsCache = {};
//remembering extreme alerts that triggered alert screen
final Set<int> notifiedAlertIds = {};

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




void initSseConnection() {
  const String sseUrl = 'http://10.0.2.2:10000/api/alerts/stream?groups=ADMIN';

SSEClient.subscribeToSSE(
      method: SSERequestType.GET,
      url: sseUrl,
      header: {
        "Accept": "text/event-stream",
        "Cache-Control": "no-cache",
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
        if (eventType == 'ALERT_UPDATE') {
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

  final url = Uri.parse('http://10.0.2.2:10000/api/alerts/active');

  final response = await http.get(url);


  //Used for mock:
  //final String response = await rootBundle.loadString('assets/mocks/alerts.json');
    final List<dynamic> decodedData = jsonDecode(response.body);
    final List<Alert> parsedAlerts = decodedData.map((item) {
      return Alert.fromJson(item as Map<String, dynamic>);
      }).toList();

    _processAlerts(parsedAlerts);
}


void handleSingleAlertUpdate(dynamic message) {
  debugPrint("SSE Message content: $message");

    // Severity update
    if (message.containsKey('alertId') && message.containsKey('newSeverity')) {
      int id = message['alertId'];
      int newSeverityValue = message['newSeverity'];

      if (alertsCache.containsKey(id)) {
        debugPrint("SSE: Aktualizuję tylko severity dla alertu $id");
        
        // Update of the object in cache
        final existingAlert = alertsCache[id]!;
        alertsCache[id] = existingAlert.copyWith(
          severity: _mapIntToSeverity(newSeverityValue), 
        );
        notifyListeners();
      } else {
        // Jeśli nie mamy go w cache, pobierzmy wszystko od nowa
        updateAllAlerts();
      }
    }

    // ACK update
    if (message.containsKey('alertId') && message.containsKey('ack')) {
      final int id = message['alertId'];
      final bool isAck = message['ack'];

      if (alertsCache.containsKey(id)) {
        debugPrint("SSE: ACK status change for $id to $isAck");
        
        alertsCache[id] = alertsCache[id]!.copyWith(
          acknowledged: isAck,
        );
        notifyListeners();
      }
    } 


    // Full alert update
    else if (message.containsKey('subject')) { 
      final alert = Alert.fromJson(message);
      _processAlerts([alert]);
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
  
final String baseUrl = 'http://10.0.2.2:10000';
  final url = Uri.parse('$baseUrl/api/alerts/$alertId/ack');


final String actionType = isAck ? "ACK" : "COMMENT";

final Map<String, dynamic> requestBody = {
    "message": comment ?? "",
    "actionType": actionType,
    "author": "Mobile User", // Możesz tu przekazać zmienną z loginem
  };


  try {
    final response = await http.post(
      url,
headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },

      body: jsonEncode(requestBody),
    );
    
    if (response.statusCode == 200) {
      debugPrint('ACK DEBUG: Alert $alertId acknowledged.');
    } else {
      debugPrint('ACK DEBUG: Server error ${response.statusCode}');
      debugPrint('ACK DEBUG: ${response.body}');
    }
  } catch (e) {
    debugPrint('ACK DEBUG: $e');
  }
}
}