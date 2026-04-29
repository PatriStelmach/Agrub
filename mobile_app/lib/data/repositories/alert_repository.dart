import 'dart:convert';

import 'package:alert_app/data/models/alert_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:alert_app/services/navigation_service.dart';
import 'package:http/http.dart' as http;





class AlertRepository extends ChangeNotifier {


final Map<int, Alert> alertsCache = {};
//remembering extreme alerts that triggered alert screen
final Set<int> notifiedAlertIds = {};

DateTime lastPing = DateTime.now();

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

  for (var alert in parsedAlerts) {
      alertsCache[alert.id] = alert;

      final newExtremeAlerts = parsedAlerts.where((alert) => 
      alert.severity == AlertSeverity.extreme && 
      !notifiedAlertIds.contains(alert.id)
    ).toList();

      if (newExtremeAlerts.isNotEmpty) {
      
      for (var alert in newExtremeAlerts) {
        notifiedAlertIds.add(alert.id);
      }
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
Future<void> sendAcknowledge(int id) async {
  

  
 print("ACK sent");
  

}



}


