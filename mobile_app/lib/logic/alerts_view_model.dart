import 'package:flutter/material.dart';

import '../data/models/alert_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';

class AlertsViewModel extends ChangeNotifier {

final AlertRepository repository;
AlertsViewModel({required this.repository}) {
  repository.addListener(notifyListeners);

}



List<Alert> sortedAlerts = [];

List<Alert> get alertsList => repository.alertsCache.values.toList();


void sortAlertsBy(String property) {

final Map<String, Comparable Function(Alert)> getters = {
  'id': (alert) => alert.id,
  'title': (alert) => alert.title,
  'hostName': (alert) => alert.hostName,
  'ipAddress': (alert) => alert.ipAddress,
  'severity': (alert) => alert.severity as Comparable,
  'status': (alert) => alert.status as Comparable,
  'createdAt': (alert) => alert.createdAt,
  'description': (alert) => alert.description,
  'source': (alert) => alert.source
};


final getter = getters[property]!;

final newList = List<Alert>.from(alertsList);
newList.sort((a,b) => getter(a).compareTo(getter(b)));
sortedAlerts = newList;

notifyListeners();
}





Future<void> acknowledgeAlert(String alertId) async {

// VERY simple function for sending ack via repository function. rudimentary error hadling
  try {
    await repository.sendAcknowledge(alertId);
    
    notifyListeners();
  } catch (e) {
      print('Placeholder error: $e');

  }
}

}