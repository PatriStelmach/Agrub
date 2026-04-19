import 'package:alert_app/services/alarm_service.dart';
import 'package:flutter/material.dart';

class DebugViewModel extends ChangeNotifier {

final alarmService = AlarmService();

void AlarmPressed() {
  print('View model triggerd');
  alarmService.triggerAlarm();
}

}