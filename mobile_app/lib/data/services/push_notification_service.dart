import 'dart:async';
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:alert_app/locator.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

/// Handling notifications from FCM and getting them inside the app
class PushNotificationService {
  final Dio dio = locator<Dio>();
  final FirebaseMessaging fcm = FirebaseMessaging.instance;
  final _messageController = StreamController<Map<String, dynamic>>.broadcast();
  Stream<Map<String, dynamic>> get fcmPayloadStream =>
      _messageController.stream;

  PushNotificationService();

  ///Function handling incoming notification depending on what is the case with app activity
  ///Three paths: app not running at all, app minimized, app open
  Future<void> initializeNotificationHandling() async {
    final RemoteMessage? initialMessage = await fcm.getInitialMessage();
    if (initialMessage != null) {
      final rawData = initialMessage.data;

      if (rawData.isNotEmpty) {
        _messageController.add(rawData);
      }
    }

    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      final rawData = message.data;

      if (rawData.isNotEmpty) {
        _messageController.add(rawData);
      }
    });

    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      final rawData = message.data;

      if (rawData.isNotEmpty) {
        _messageController.add(rawData);
      }
    });
  }

  ///Registering device in backend for Firebase messaging
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
            data: {'token': fcmToken},
          );

          if (response.statusCode == 200 || response.statusCode == 201) {
            debugPrint("FCM - Token registered succesfully.");
          }
        }
      } else {
        debugPrint("FCM - User declined notification approval.");
      }
    } catch (e) {
      debugPrint("FCM - Error while registering the token - $e");
      await locator<AlarmService>().showEmergencyOverlay('FCM');
    }
  }
}
