import 'dart:async';

import 'package:alert_app/locator.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

import 'package:firebase_messaging/firebase_messaging.dart';

//mock imports
//import 'dart:convert';
//import 'package:flutter/services.dart';

class PushNotificationService {
  final Dio dio = locator<Dio>();
  final FirebaseMessaging fcm = FirebaseMessaging.instance;
  final _messageController = StreamController<Map<String, dynamic>>.broadcast();
  Stream<Map<String, dynamic>> get fcmPayloadStream =>
      _messageController.stream;

  PushNotificationService();

  Future<void> initNotificationHandling() async {
    //Aplication not running at all
    RemoteMessage? initialMessage = await fcm.getInitialMessage();
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
      NotificationSettings settings = await fcm.requestPermission(
        alert: true,
        badge: true,
        sound: true,
      );

      if (settings.authorizationStatus == AuthorizationStatus.authorized) {
        String? fcmToken = await fcm.getToken();
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

  Future<void> pingBackend() async {
    // 10.0.2.2 emulator Androida
    // localhost/127.0.0.1 Windows(Chrome)

    try {
      final response = await dio.post('');

      if (response.statusCode == 200) {
        print('Backend working:');
      } else {
        print('Backend working, but error: ${response.statusCode}');
      }
    } catch (e) {
      print('No connection: $e');
    }
  }

  // Dodatkowa metoda wywoływana z poziomu main.dart w tle
  void handleBackgroundMessage(RemoteMessage message) {
    debugPrint("FCM [BACKGROUND]: Serwis przechwycił wiadomość wybudzającą.");
    _handleMessage(message);
  }
}
