import 'package:flutter/material.dart';
import 'package:alert_app/data/services/alarm_service.dart';

///View model for testing purposes, currently kept as backup for any future needs.
class DebugViewModel extends ChangeNotifier {
  final AlarmService alarmService;

  DebugViewModel({required this.alarmService});
  void alarmPressed() {
    alarmService.showEmergencyOverlay("Alert");
  }
}
