import 'package:alert_app/data/models/alert_model.dart';

import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
//mock imports
import 'dart:convert';
import 'package:flutter/services.dart';

class PushNotificationService extends ChangeNotifier{



  Future<void> incomingPush() async {
 
  
  }


  Future<void> registerDevice() async {
    //FCM token registration

  }
Future<void> pingBackend() async {
  // Użyj 10.0.2.2 jeśli korzystasz z emulatora Androida
  // Użyj localhost (lub 127.0.0.1) jeśli to apka Windows/macOS lub iOS Simulator
  final url = Uri.parse('http://10.0.2.2:10000/api/health'); // sprawdź ścieżkę w Swaggerze!

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






//final pushService = PushNotificationService();
}