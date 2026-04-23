import 'dart:convert';

import 'package:alert_app/data/models/alert_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:alert_app/services/navigation_service.dart';





class AlertRepository extends ChangeNotifier {


final Map<String, Alert> alertsCache = {};

DateTime lastPing = DateTime.now();

//MOCK: getting alerts from local JSON
//FINAL: updating full list via REST when opening the app
Future<void> updateAllAlerts() async {

     final String response = await rootBundle.loadString('assets/mocks/alerts.json');
    final List<dynamic> decodedData = jsonDecode(response);
    final List<Alert> parsedAlerts = decodedData.map((item) {
      return Alert.fromJson(item as Map<String, dynamic>);
    }).toList();

  for (var alert in parsedAlerts) {
      alertsCache[alert.id] = alert;

      if(alert.severity == AlertSeverity.extreme) {
        navigationService.showEmergencyOverlay();
      }
    }

  notifyListeners();

}


void ping() {
  lastPing = DateTime.now();
  notifyListeners();
}


// very simple placehold for sending ack
Future<void> sendAcknowledge(String id) async {
  

  
 print("ACK sent");
  

}



}


