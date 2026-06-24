import 'dart:async';

import 'package:alert_app/data/services/push_notification_service.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:flutter/material.dart';

enum AppScreen { home, alerts, plugins, user, settings }

class GeneralLayoutViewModel extends ChangeNotifier {
  final UserViewModel userViewModel;
  final AlertsViewModel alertsViewModel;
  final PushNotificationService pushNotificationService;

  GeneralLayoutViewModel({
    required this.userViewModel,
    required this.alertsViewModel,
    required this.pushNotificationService,
  });
  AppScreen _activeScreen = AppScreen.home;

  AppScreen get activeScreen => _activeScreen;

  int get currentIndex => _activeScreen.index;

  /// Change the page using enum and triggering rebuild
  void changePage(AppScreen newPage) {
    if (_activeScreen != newPage) {
      _activeScreen = newPage;
      notifyListeners();
    }
  }

  ///Initializing crucial methods and services as soon as General Layout is build
  Future<void> startBackgroundServices() async {
    final token = await userViewModel.userRepository.getToken();

    final user = userViewModel.user;

    if (token != null && user != null) {
      alertsViewModel.initializeSseConnection(
        userRole: user.role,
        token: token,
      );
      unawaited(alertsViewModel.fetchInitialAlerts());

      try {
        unawaited(pushNotificationService.registerDevice(token));
        debugPrint(
          "GENERAL LAYOUT VIEW MODEL - FCM Device registered successfully.",
        );
      } catch (e) {
        debugPrint(
          "GENERAL LAYOUT VIEW MODEL -  Error with FCM registration: $e",
        );
      }
    }
  }
}
