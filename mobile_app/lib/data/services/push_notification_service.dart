import 'dart:async';

import 'package:alert_app/locator.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

import 'package:firebase_messaging/firebase_messaging.dart';

class PushNotificationService {
  final Dio dio = locator<Dio>();
  final FirebaseMessaging fcm = FirebaseMessaging.instance;
  final _messageController = StreamController<Map<String, dynamic>>.broadcast();
  Stream<Map<String, dynamic>> get fcmPayloadStream =>
      _messageController.stream;

  PushNotificationService();

  ///Function handling incoming notification depending on what is the case with app activity
  Future<void> initNotificationHandling() async {
    //Aplication not running at all
    final RemoteMessage? initialMessage = await fcm.getInitialMessage();
    if (initialMessage != null) {
      _handleMessage(initialMessage);
    }

    //Application minimized
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      _handleMessage(message);
    });

    // Application open
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      debugPrint("FCM: Otrzymano powiadomienie w trybie Foreground");
      _handleMessage(message);
    });
  }

  void _handleMessage(RemoteMessage message) {
    final rawData = message.data;

    if (rawData.isNotEmpty) {
      debugPrint("FCM: Przekazywanie surowych danych do AlertRepository...");

      _messageController.add(rawData);
    }
  }

  Future<void> registerDevice(String jwtToken) async {
    try {
      final NotificationSettings settings = await fcm.requestPermission(
        alert: true,
        badge: true,
        sound: true,
      );

      if (settings.authorizationStatus == AuthorizationStatus.authorized) {
        final String? fcmToken = await fcm.getToken();
        debugPrint("FCM Token: $fcmToken");

        if (fcmToken != null) {
          final response = await dio.post(
            '/api/devices/token',
            options: Options(headers: {'Authorization': 'Bearer $jwtToken'}),
          );

          if (response.statusCode == 200 || response.statusCode == 201) {
            debugPrint("FCM: Token pomyślnie zarejestrowany na backendzie.");
          }
        }
      } else {
        debugPrint("FCM: Użytkownik odmówił uprawnień do powiadomień.");
      }
    } catch (e) {
      debugPrint("FCM ERROR: Błąd podczas rejestracji tokenu: $e");
    }
  }

}
