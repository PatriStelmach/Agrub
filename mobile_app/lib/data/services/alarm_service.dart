//dart:io is needed for Plaftorm class in below code
import 'dart:io';
import 'package:audioplayers/audioplayers.dart';
import 'package:vibration/vibration.dart';

///Service class starting and stopping sound and vibration on physical devices
class AlarmService {
  final player = AudioPlayer();

  Future<void> triggerAlarm() async {
    if (Platform.isAndroid) {
      await player.setReleaseMode(ReleaseMode.loop);
      await player.play(AssetSource('audio/alarm.mp3'));

      // Vibrations (if possible)
      if (await Vibration.hasVibrator()) {
        Vibration.vibrate(duration: 2000, amplitude: 255);
      }
    } else if (Platform.isIOS) {
      print("The platform is iOS");
    }
  }

  Future<void> stopAlarm() async {
    await player.stop();
    if (await Vibration.hasVibrator()) {
      Vibration.cancel();
    }
  }
}
