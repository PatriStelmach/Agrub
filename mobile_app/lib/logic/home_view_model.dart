import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';

import '../data/models/alert_model.dart';

class HomeViewModel extends ChangeNotifier {
  final AlertsViewModel alertsViewModel;
  void getMyToken() async {
    String? token = await FirebaseMessaging.instance.getToken();
    //
    debugPrint("Mój FCM Token: $token");
  }

  HomeViewModel({required this.alertsViewModel}) {
    alertsViewModel.addListener(notifyListeners);
  }

  int get activeAlertsCount => alertsViewModel.alertsList.length;
  //T0D0: Null tymczasowo, co zrobić z tym last ping>?
  DateTime? get lastPing => null;

  List<Alert> latestAlerts() {
    List<Alert> allAlerts = List<Alert>.from(alertsViewModel.alertsList);
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
