import 'package:flutter/material.dart';
import 'package:alert_app/screens/alarm_overlay_screen.dart';
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

class NavigationService {
  final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();
  final AlarmService alarmService = AlarmService();
  bool _isOverlayVisible = false;

  void showEmergencyOverlay() {
    // If there is an alarm overlay already, return
    if (_isOverlayVisible) return;

    _isOverlayVisible = true;
    alarmService.triggerAlarm();

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      if (navigatorKey.currentState == null) return;

      _isOverlayVisible = true;

      await navigatorKey.currentState?.push(
        PageRouteBuilder(
          opaque: false,
          barrierColor: Colors.black.withOpacity(0.7),
          pageBuilder: (context, _, __) => const AlarmOverlayScreen(),
          transitionsBuilder: (context, animation, _, child) {
            return FadeTransition(opacity: animation, child: child);
          },
        ),
      );

      _isOverlayVisible = false;
    });
  }

  void stopAlarmAndDismiss() {
    // Wyłącza dźwięk
    alarmService.stopAlarm();
    // Zamyka overlay
    navigatorKey.currentState?.pop();
    // Kończy pętlę alarmową androida
    FlutterLocalNotificationsPlugin().cancelAll();
  }
}

final navigationService = NavigationService();
