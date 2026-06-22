import 'package:flutter/material.dart';
import 'package:alert_app/data/services/alarm_service.dart';

class DebugViewModel extends ChangeNotifier {
  final AlarmService navigationService;

  DebugViewModel({required this.navigationService});
  void alarmPressed() {
    navigationService.showEmergencyOverlay("Alert");
  }
}
