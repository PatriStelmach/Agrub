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
 
    List<Alert> allAlerts = repository.alertsCache.values.toList();
    allAlerts.sort((a, b) => b.createdAt.compareTo(a.createdAt));
    return allAlerts.take(3).toList();
}

List<Alert> latestCriticalAlerts() {
  return repository.alertsCache.values
      .where((alert) => alert.severity == AlertSeverity.extreme)
      .toList();
}

}