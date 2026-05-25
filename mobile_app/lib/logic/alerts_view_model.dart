import 'package:flutter/material.dart';

import '../data/models/alert_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';

class AlertsViewModel extends ChangeNotifier {
  final AlertRepository alertsRepository;
  String _currentSortProperty = 'id';
  bool _isAscending = true;

  AlertsViewModel({required this.alertsRepository}) {
    alertsRepository.addListener(handleRepoChange);
  }

  String get currentSortProperty => _currentSortProperty;
  bool get isAscending => _isAscending;

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
      'source': (alert) => alert.source,
    };

    final getter = getters[_currentSortProperty] ?? (alert) => alert.id;

    list.sort((a, b) {
      final aValue = getter(a);
      final bValue = getter(b);

      return _isAscending ? aValue.compareTo(bValue) : bValue.compareTo(aValue);
    });

    return list;
  }

  void sortAlertsBy(String property, {bool? ascending}) {
    _currentSortProperty = property;

    if (ascending != null) {
      _isAscending = ascending;
    }

    debugPrint("Sorting by $property, Ascending: $_isAscending");
    notifyListeners();
  }

  void handleRepoChange() {
    debugPrint("ViewModel: Repo wysłało sygnał, odświeżam widok.");
    notifyListeners();
  }

  Future<void> acknowledgeAlert(
    int alertId, {
    String? comment,
    bool isAck = true,
  }) async {
    try {
      await alertsRepository.sendAcknowledge(
        alertId,
        comment: comment,
        isAck: isAck,
      );
    } catch (e) {
      debugPrint('Placeholder error: $e');
    }
  }

  @override
  void dispose() {
    alertsRepository.removeListener(handleRepoChange);
    super.dispose();
  }
}
