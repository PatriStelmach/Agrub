import 'dart:async';

import 'package:flutter/material.dart';
import 'package:alert_app/screens/alarm_overlay_screen.dart';
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

class NavigationService {
  final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();
  final AlarmService alarmService = AlarmService();
  bool _isOverlayVisible = false;
  final String _alertMessage = "Generic message";

  ///Showing the alarm overlay, containing safeguard against overlay spam
  Future<void> showEmergencyOverlay(String alarmType) async {
    // If there is an alarm overlay already, return
    if (_isOverlayVisible) return;

    _isOverlayVisible = true;
    unawaited(alarmService.triggerAlarm());

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      if (navigatorKey.currentState == null) return;

      await navigatorKey.currentState?.push(
        PageRouteBuilder(
          opaque: false,
          barrierColor: Colors.black.withValues(alpha: 0.7),
          pageBuilder: (context, _, _) =>  AlarmOverlayScreen(alertMessage:_alertMessage),
          transitionsBuilder: (context, animation, _, child) {
            return FadeTransition(opacity: animation, child: child);
          },
        ),
      );

      _isOverlayVisible = false;
    });
  }

  ///Stopping the sound and closing the overlay
  void stopAlarmAndDismiss() {
    //stop sound
    alarmService.stopAlarm();
    // close overlay
    navigatorKey.currentState?.pop();
    // End adroid alarm loop
    FlutterLocalNotificationsPlugin().cancelAll();
  }
}
