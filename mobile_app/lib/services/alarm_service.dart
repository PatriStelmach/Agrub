import 'dart:io';
import 'package:audioplayers/audioplayers.dart';
import 'package:vibration/vibration.dart';





class AlarmService {
  void triggerAlarm() async {
    print('Service triggerd');
    if (Platform.isAndroid) {
      final _player = AudioPlayer();

  
    // 1. Odpal dźwięk (plik musi być w pubspec.yaml)
    await _player.play(AssetSource('audio/alarm.mp3'));

    // 2. Odpal wibracje (jeśli urządzenie na to pozwala)
    if (await Vibration.hasVibrator() ?? false) {
      Vibration.vibrate(duration: 2000, amplitude: 255);
    }
  
    } else if (Platform.isIOS) {
      print("The platform is iOS");
      
    }
  }
}