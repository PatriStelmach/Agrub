import 'package:flutter/material.dart';
import 'package:alert_app/data/services/navigation_service.dart';
import 'package:alert_app/locator.dart';

class DebugViewModel extends ChangeNotifier {
  final NavigationService navigationService;

  DebugViewModel({ required this.navigationService});
  void alarmPressed() {
    navigationService.showEmergencyOverlay("TEST");
  }
}
