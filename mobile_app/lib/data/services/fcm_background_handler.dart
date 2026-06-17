import 'package:alert_app/locator.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:get_it/get_it.dart';

import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';

//Global function, so it can be called in Isolate
@pragma('vm:entry-point')
Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();

  // Inicjalizacja DI (GetIt) dla procesu w tle
  if (!GetIt.instance.isRegistered<AlertRepository>()) {
    setupLocator();
  }

  debugPrint("Handling a background message: ${message.messageId}");

  try {
    final rawData = message.data;
    if (rawData.isNotEmpty) {
      debugPrint("FCM [BACKGROUND]: Przetwarzanie surowych danych...");

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
      await _showNotificationBasedOnSeverity(alert);

      debugPrint("!!! BACKGROUND SYSTEM ALARM PROCESSED SUCCESSFULLY !!!");
    }
  } catch (e) {
    debugPrint("FCM [BACKGROUND] ERROR: Błąd przetwarzania w tle: $e");
  }
}

/// Prywatna funkcja pomocnicza do rysowania natywnych powiadomień Androida
Future<void> _showNotificationBasedOnSeverity(Alert alert) async {
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
      'Krytyczne Alerty',
      channelDescription:
          'Powiadomienia o awariach krytycznych infrastruktury.',
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
      //W natywnym Androidzie flaga zapętlenia to stała numeryczna FLAG_INSISTENT o wartości 4.
      additionalFlags: Int32List.fromList([4]),
      category: AndroidNotificationCategory.call,
      fullScreenIntent: true,
    );
  } else {
    androidDetails = const AndroidNotificationDetails(
      'standard_alerts_channel_v1',
      'Zwykłe Alerty (Ciche)',
      channelDescription: 'Mniejsze alerty informacyjne, niekrytyczne.',
      importance: Importance.low,
      priority: Priority.defaultPriority,
      playSound: true,
    );
  }

  await localNotifications.show(
    id: alert.id,
    title: alert.severity == AlertSeverity.critical
        ? "EXTREME ALERT!"
        : "New alert from: ${alert.source}",
    body: alert.subject,
    notificationDetails: NotificationDetails(android: androidDetails),
  );
}
