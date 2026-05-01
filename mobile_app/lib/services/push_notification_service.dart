import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;


import 'package:firebase_messaging/firebase_messaging.dart';

//mock imports
//import 'dart:convert';
//import 'package:flutter/services.dart';

class PushNotificationService extends ChangeNotifier{

final AlertRepository alertRepository;

PushNotificationService(this.alertRepository);

final FirebaseMessaging fcm = FirebaseMessaging.instance;

 Future<void> initNotificationHandling() async {
    // 1. SCENARIUSZ: Aplikacja była CAŁKOWICIE WYŁĄCZONA (Terminated)
    // Sprawdzamy, czy aplikacja "wstała" dzięki kliknięciu w powiadomienie
    RemoteMessage? initialMessage = await fcm.getInitialMessage();
    if (initialMessage != null) {
      _handleMessage(initialMessage);
    }

    // 2. SCENARIUSZ: Aplikacja była ZMINIMALIZOWANA (Background)
    // Stream nasłuchujący na kliknięcie w powiadomienie w zasobniku
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      _handleMessage(message);
    });

    // 3. SCENARIUSZ: Aplikacja jest OTWARTA (Foreground) - Krytyczne dla Emergency!
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      debugPrint("FCM: Otrzymano powiadomienie w trybie Foreground");
      _handleMessage(message);
    });


    
  }


void _handleMessage(RemoteMessage message) {
  final rawData = message.data;

  if (rawData.isNotEmpty) {
    debugPrint("FCM: Przekazywanie surowych danych do AlertRepository...");
    

    alertRepository.handleSingleAlertUpdate(rawData);
  }
}



Future<void> registerDevice() async {
    //FCM token registration

  }

Future<void> pingBackend() async {
  // 10.0.2.2 emulator Androida
  // localhost/127.0.0.1 Windows(Chrome)
  final url = Uri.parse('http://10.0.2.2:10000/api/health');

  try {
    final response = await http.get(url);

    if (response.statusCode == 200) {
      print('Backend working: ${response.body}');
    } else {
      print('Backend working, but error: ${response.statusCode}');
    }
  } catch (e) {
    print('No connection: $e');
  }
}


}