import 'dart:io';
import 'package:audioplayers/audioplayers.dart';
import 'package:vibration/vibration.dart';





class AlarmService {
  
  final player = AudioPlayer();

  void triggerAlarm() async {
    
    
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
    }
}



