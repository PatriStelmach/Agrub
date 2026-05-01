import 'dart:convert';

import 'package:alert_app/data/models/alert_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:alert_app/services/navigation_service.dart';
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_client_sse/flutter_client_sse.dart';

class AlertRepository extends ChangeNotifier {

final Map<int, Alert> alertsCache = {};
//remembering extreme alerts that triggered alert screen
final Set<int> notifiedAlertIds = {};

DateTime lastPing = DateTime.now();

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
      if (event.data != null && event.data!.isNotEmpty) {
        debugPrint("SSE Otrzymano dane: ${event.data}");

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


void handleSingleAlertUpdate(Map<String, dynamic> rawData) {
    try {
      // Mapujemy pojedynczy obiekt
      final alert = Alert.fromJson(rawData);
      
      _processAlerts([alert]);
      
      debugPrint("DEBUG: FCM/SSE: Alert processed: ${alert.id}");
    } catch (e) {
      debugPrint("DEBUG: FCM Mapping Error $e");
    }
  }

void _processAlerts(List<Alert> incomingAlerts) {
    debugPrint("Repo: Procesuję ${incomingAlerts.length} alertów");

    bool alarmOverlayTrigger = false;


    for (var alert in incomingAlerts) {
      //cache synchronization ( multiple alarm overlays prevention)
      if (notifiedAlertIds.contains(alert.id)) continue;
      alertsCache[alert.id] = alert;

    debugPrint("DEBUG: Analysis of Alert ID: ${alert.id}");
    debugPrint(" DEBUG: Show severity: ${alert.severity}");
    debugPrint(" DEBUG: Is ID already in notifedAlertsIds list>? ${notifiedAlertIds.contains(alert.id)}");

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

// very simple placehold for sending ack
Future<void> sendAcknowledge(int id) async {
  
 print("ACK sent");
  }

}