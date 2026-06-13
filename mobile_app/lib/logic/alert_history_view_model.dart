import 'dart:async';
import 'package:alert_app/data/models/problem_action_model.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';
import '../data/models/history_alert_model.dart';

class AlertsHistoryViewModel extends ChangeNotifier {
  final AlertRepository alertsRepository;
  //UI state variables
  final Map<int, HistoryAlert> _historyAlertsCache = {};
  bool _isLoading = false;
  String _currentSortProperty = 'id';
  bool _isAscending = true;
  StreamSubscription? _sseSubscription;

  AlertsHistoryViewModel({
    required this.alertsRepository,
  });

  //Getters

  bool get isLoading => _isLoading;
  String get currentSortProperty => _currentSortProperty;
  bool get isAscending => _isAscending;

  List<HistoryAlert> get alertsList => _historyAlertsCache.values.toList();
  List<HistoryAlert> get sortedHistory {
    final list = List<HistoryAlert>.from(alertsList);

    final Map<String, Comparable Function(HistoryAlert)> getters = {
      'id': (alert) => alert.id,
      'title': (alert) => alert.subject,
      'hostName': (alert) => alert.source,
      'severity': (alert) => alert.severity.index,
      'status': (alert) => alert.status.index,
      'createdAt': (alert) => alert.createdAt,
      'closedAt': (alert) => alert.closedAt,
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

  /// Fetching first data on screen start
  Future<void> fetchInitialHistoryAlerts() async {
    _isLoading = true;
    notifyListeners();

    try {
      final alerts = await alertsRepository.fetchHistoryAlerts();

      _historyAlertsCache.clear();
      for (var alert in alerts) {
        _historyAlertsCache[alert.id] = alert;
      }
    } catch (e) {
      debugPrint("HISTORY ALERTS VIEW MODEL ERROR: Couldn't fetch the data: $e");
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }


  void sortAlertsBy(String property, {bool? ascending}) {
    _currentSortProperty = property;
    if (ascending != null) _isAscending = ascending;
    notifyListeners();
  }

  Future<ProblemAction?> getLatestActionForAlert(int alertId) async {
    return await alertsRepository.getLatestActionForAlert(alertId);
  }


  @override
  void dispose() {
    _sseSubscription?.cancel();
    super.dispose();
  }
}
