import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';

import '../data/models/alert_model.dart';

class HomeViewModel extends ChangeNotifier {
  final AlertsViewModel alertsViewModel;
  final AlertRepository alertsRepository;
  int get activeAlertsCount => alertsViewModel.alertsList.length;

  bool? _lastPing;
  bool? get lastPing => _lastPing;
  HomeViewModel({
    required this.alertsViewModel,
    required this.alertsRepository,
  }) {
    alertsViewModel.addListener(notifyListeners);
  }

  /// Getting FCM token
  Future<void> getMyToken() async {
    final String? token = await FirebaseMessaging.instance.getToken();
    debugPrint("My FCM Token: $token");
  }

  ///Method to ping backend via repository method
  Future<bool> pingBackend() async {
    final bool ping = await alertsRepository.checkBackendConnection();
    if (ping) {
      return true;
    } else {
      return false;
    }
  }

  ///Method to call update variable and update screens
  Future<void> triggerPingAndNotify() async {
    _lastPing = await pingBackend();
    notifyListeners();
  }

  ///Getting latest critical alerts for display
  List<Alert> latestCriticalAlerts() {
    return alertsViewModel.alertsList
        .where((alert) => alert.severity == AlertSeverity.critical)
        .toList();
  }
}
