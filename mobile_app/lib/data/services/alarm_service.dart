import 'dart:async';
import 'dart:io';
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter_volume_controller/flutter_volume_controller.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/screens/alarm_overlay_screen.dart';
import 'package:flutter/services.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:sound_mode/sound_mode.dart';
import 'package:vibration/vibration.dart';
import 'package:sound_mode/utils/ringer_mode_statuses.dart';

class AlarmService {
  final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();
  bool _isOverlayVisible = false;
  String _alertMessage = "Generic message";
  final player = AudioPlayer();

  ///Showing the alarm overlay, containing safeguard against overlay spam.
  ///Allows calling different messages to be shown
  Future<void> showEmergencyOverlay(String alarmType) async {
    // If there is an alarm overlay already, return
    if (_isOverlayVisible) return;

    switch (alarmType) {
      case 'Alert':
        _alertMessage = "Critical alert!";
        break;
      case 'Connection':
        _alertMessage =
            "No connection to server! Restore connection and ping in Home Screen";
        break;
      case 'FCM':
        _alertMessage =
            "FCM Issue! There won't be any updates in the background";
        break;
    }

    _isOverlayVisible = true;
    unawaited(triggerAlarm());

    WidgetsBinding.instance.addPostFrameCallback((_) async {
      if (navigatorKey.currentState == null) return;

      await navigatorKey.currentState?.push(
        PageRouteBuilder(
          opaque: false,
          barrierColor: Colors.black.withValues(alpha: 0.7),
          pageBuilder: (context, _, _) =>
              AlarmOverlayScreen(alertMessage: _alertMessage),
          transitionsBuilder: (context, animation, _, child) {
            return FadeTransition(opacity: animation, child: child);
          },
        ),
      );

      _isOverlayVisible = false;
    });
  }

  ///Triggering the sound and vibration effects themselves.
  ///Here should've been iOS alarm trigger, but for time being it is postponed due to constraints mentioned in
  ///our thesis. I've left conditional statement for potential future implementation
  Future<void> triggerAlarm() async {
    String ringerStatus = SoundMode.ringerModeStatus.toString();
    debugPrint(ringerStatus);

    final bool isGranted = await Permission.accessNotificationPolicy.isGranted;
    if (!isGranted) {
      await Permission.accessNotificationPolicy.request();
      return;
    }

    await SoundMode.setSoundMode(RingerModeStatus.normal);

    if (Platform.isAndroid) {
      await FlutterVolumeController.setVolume(1.0);
      await player.setReleaseMode(ReleaseMode.loop);
      await player.play(AssetSource('audio/alarm.mp3'));

      // Vibrations (if possible)
      if (await Vibration.hasVibrator()) {
        await Vibration.vibrate(duration: 2000, amplitude: 255);
      }
    }
  }

  ///Stopping the sound and closing the overlay
  Future<void> stopAlarmAndDismiss() async {
    await player.stop();
    if (await Vibration.hasVibrator()) {
      await Vibration.cancel();
    }
    navigatorKey.currentState?.pop();
    await FlutterLocalNotificationsPlugin().cancelAll();
  }
}
