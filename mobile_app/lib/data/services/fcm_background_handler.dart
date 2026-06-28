import 'dart:async';
import 'package:alert_app/locator.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:get_it/get_it.dart';
import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';

///Service handling FCM in the background, working in it's own Isolate
@pragma('vm:entry-point')
Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();

  if (!GetIt.instance.isRegistered<AlertRepository>()) {
    await setupLocator();
  }

  debugPrint(
    "FCM BACKGROUND HANDLER - Handling a background message: ${message.messageId}",
  );

  try {
    final rawData = message.data;
    if (rawData.isNotEmpty) {
      final Alert alert = Alert.fromJson(rawData);

      final alertRepo = GetIt.instance<AlertRepository>();
      final offlineAlerts = await alertRepo.getOfflineAlerts();

      final existingIndex = offlineAlerts.indexWhere((a) => a.id == alert.id);
      if (existingIndex != -1) {
        offlineAlerts[existingIndex] = alert;
      } else {
        offlineAlerts.add(alert);
      }

      await alertRepo.saveAlertsToOfflineCache(offlineAlerts);
      await _notificationHandling(alert);

      debugPrint(
        "FCM BACKGROUND HANDLER - Background message handled succesfully",
      );
    }
  } catch (e) {
    debugPrint("FCM BACKGROUND HANDLER - Encountered error $e");
  }
}

/// Helper function to render Android native notification
Future<void> _notificationHandling(Alert alert) async {
  final FlutterLocalNotificationsPlugin localNotifications =
      FlutterLocalNotificationsPlugin();

  const AndroidInitializationSettings initializationSettingsAndroid =
      AndroidInitializationSettings('@mipmap/ic_launcher');

  const InitializationSettings initializationSettings = InitializationSettings(
    android: initializationSettingsAndroid,
  );

  await localNotifications.initialize(settings: initializationSettings);

  AndroidNotificationDetails androidDetails;

  if (alert.severity == AlertSeverity.critical) {
    androidDetails = AndroidNotificationDetails(
      'extreme_alerts_channel_v3',
      'Critical',
      channelDescription: 'Critial alerts notification',
      importance: Importance.max,
      priority: Priority.high,
      playSound: true,
      sound: const RawResourceAndroidNotificationSound('alarm'),
      enableVibration: true,
      vibrationPattern: Int64List.fromList(const [
        0,
        1000,
        500,
        1000,
        500,
        1000,
      ]),
      //Why 4 in addtionalFlags: in native android loop is FLAG_INSISTENT with value 4.
      additionalFlags: Int32List.fromList([4]),
      category: AndroidNotificationCategory.call,
      fullScreenIntent: true,
    );
  } else {
    androidDetails = const AndroidNotificationDetails(
      'standard_alerts_channel_v1',
      'Standard alert (Quitet)',
      channelDescription: 'Less important alerts',
      importance: Importance.low,
      priority: Priority.defaultPriority,
      playSound: true,
    );
  }

  await localNotifications.show(
    id: alert.id,
    title: alert.severity == AlertSeverity.critical
        ? "CRITICAL ALERT!"
        : "New alert from: ${alert.source}",
    body: alert.subject,
    notificationDetails: NotificationDetails(android: androidDetails),
  );
}
