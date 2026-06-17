import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';

import '../data/models/alert_model.dart';

class HomeViewModel extends ChangeNotifier {
  final AlertsViewModel alertsViewModel;
  final AlertRepository alertsRepository;
  Future<void> getMyToken() async {
    final String? token = await FirebaseMessaging.instance.getToken();

    debugPrint("My FCM Token: $token");
  }

  HomeViewModel({
    required this.alertsViewModel,
    required this.alertsRepository,
  }) {
    alertsViewModel.addListener(notifyListeners);
  }

  int get activeAlertsCount => alertsViewModel.alertsList.length;
  //T0D0: To be handled
  DateTime? get lastPing => null;

  Future<DateTime?> pingBackend() async {
    final ping = await alertsRepository.checkBackendConnection();
    if (ping) {
      return DateTime.now();
    } else {
      return null;
    }
  }

  List<Alert> latestAlerts() {
    final List<Alert> allAlerts = List<Alert>.from(alertsViewModel.alertsList);
    allAlerts.sort((a, b) => b.createdAt.compareTo(a.createdAt));
    return allAlerts.take(3).toList();
  }

  List<Alert> latestCriticalAlerts() {
    return alertsViewModel.alertsList
        .where((alert) => alert.severity == AlertSeverity.extreme)
        .toList();
  }

  void refresh() {
    debugPrint("HomeViewModel: Manual refresh");
    notifyListeners();
  }
}
