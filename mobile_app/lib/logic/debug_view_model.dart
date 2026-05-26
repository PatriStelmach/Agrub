import 'package:alert_app/data/services/navigation_service.dart';
import 'package:flutter/material.dart';

class DebugViewModel extends ChangeNotifier {
  void alarmPressed() {
    navigationService.showEmergencyOverlay();
  }
}
