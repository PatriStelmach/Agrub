
import 'package:alert_app/services/navigation_service.dart';
import 'package:alert_app/services/push_notification_service.dart';
import 'package:flutter/material.dart';

class DebugViewModel extends ChangeNotifier {



void mockPush() {
  
}

void alarmPressed() {
 
  navigationService.showEmergencyOverlay();
}

}