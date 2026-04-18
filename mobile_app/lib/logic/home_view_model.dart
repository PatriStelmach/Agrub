import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:flutter/material.dart';

import '../data/models/alert_model.dart';

class HomeViewModel extends ChangeNotifier {

final AlertRepository repository;
HomeViewModel({required this.repository}) {
  repository.addListener(notifyListeners);
}

DateTime? get lastPing => repository.lastPing;


List<Alert> latestAlerts() {
  List<Alert> sortedAlerts = List.from(testAlerts);
   sortedAlerts.sort((a, b) => b.createdAt.compareTo(a.createdAt));
   return sortedAlerts.take(3).toList();
}

List<Alert> latestCriticalAlerts() {
  List<Alert> criticalAlerts = List.from(testAlerts);
  criticalAlerts.removeWhere((alert) => alert.severity != AlertSeverity.extreme);
  return criticalAlerts;
}

}