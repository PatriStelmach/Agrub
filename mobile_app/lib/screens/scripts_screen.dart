import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ScriptsScreen extends StatelessWidget {
  const ScriptsScreen({super.key});



  @override
  Widget build(BuildContext context) {

    final adminViewModel = context.watch<PluginsViewModel>();
    
    return Column(
      children: [
        Row(children: [
          Expanded(
          child: Text('Ping server', textAlign: TextAlign.center),
          ),
          Expanded(
          child: ElevatedButton(onPressed: () {adminViewModel.repository.ping();} , child: Text('Ping',) ),
          ),
        ],),
        Row(children: [
          Expanded(
          child: Text('Opis opcji', textAlign: TextAlign.center),
          ),
          Expanded(
          child: ElevatedButton(onPressed: () {}, child: Text('button opcji')),
          ),
        ],),
        Row(children: [
          Expanded(
          child: Text('Opis opcji',textAlign: TextAlign.center),
          ),
          Expanded(
          child: ElevatedButton(onPressed: () {}, child: Text('button opcji')),
          ),
        ],),

        ]
        );
      
    
  }
}