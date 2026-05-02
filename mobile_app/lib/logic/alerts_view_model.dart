import 'package:flutter/material.dart';

import '../data/models/alert_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';

class AlertsViewModel extends ChangeNotifier {

final AlertRepository alertsRepository;
String _currentSortProperty = 'id';
AlertsViewModel({required this.alertsRepository}) {
  alertsRepository.addListener(handleRepoChange);

}

List<Alert> get alertsList => alertsRepository.alertsCache.values.toList();


List<Alert> get sortedAlerts {
    final list = List<Alert>.from(alertsList);
    
    final Map<String, Comparable Function(Alert)> getters = {
      'id': (alert) => alert.id,
      'title': (alert) => alert.subject,
      'hostName': (alert) => alert.source,
      'severity': (alert) => alert.severity.index,
      'status': (alert) => alert.status.index,
      'createdAt': (alert) => alert.createdAt,
      'description': (alert) => alert.message,
      'source': (alert) => alert.source
    };

    final getter = getters[_currentSortProperty] ?? (alert) => alert.id;
    list.sort((a, b) => getter(a).compareTo(getter(b)));
    return list;
  }

void sortAlertsBy(String property) {
    _currentSortProperty = property;
    notifyListeners(); 
  }

void handleRepoChange() {
    debugPrint("ViewModel: Repo wysłało sygnał, odświeżam widok.");
    notifyListeners(); 
  }




Future<void> acknowledgeAlert(int alertId) async {

// VERY simple function for sending ack via repository function. rudimentary error hadling
  try {
    await alertsRepository.sendAcknowledge(alertId);
    
    notifyListeners();
  } catch (e) {
      print('Placeholder error: $e');

  }
}

@override
  void dispose() {
    alertsRepository.removeListener(handleRepoChange);
    super.dispose();
  }
}
