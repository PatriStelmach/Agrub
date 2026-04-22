import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/logic/debug_view_model.dart';
import 'package:provider/provider.dart';

class DebugScreen extends StatelessWidget {
  const DebugScreen({super.key});

  @override
  Widget build(BuildContext context) {
    
    final debugViewModel = DebugViewModel();
    return Column(
      children: [ 
        Text("DEBUG FUNCTIONS", style: TextStyle(fontSize: 30)),
        Row(children: [
          Expanded(
          
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: ElevatedButton(onPressed: () {debugViewModel.alarmPressed();}, child: Text('Trigger extreme alert alarm') ),
          ),
          )
        ],),

        Row(children: [


          Expanded(
          child: 
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: ElevatedButton(onPressed: () {}, child: Text(' Push - new alerts(no extreme)')),
          ),
          ),
        ],),
        Row(children: [

          Expanded(
          child: 
          
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: ElevatedButton(onPressed: () {Provider.of<AlertRepository>(context, listen:false).updateAllAlerts();}, child: Text('Push - new alerts(with extreme)')),
          ),
          ),
        ],),],
    );
  }
}