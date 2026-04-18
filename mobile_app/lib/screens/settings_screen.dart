import 'package:flutter/material.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Row(children: [
          Expanded(
          child: Text('Toggle theme', textAlign: TextAlign.center),
          ),
          Expanded(
          child: ElevatedButton(onPressed: () {}, child: Text('button opcji',) ),
          ),
        ],),
        Container(
          height: 3.0,
          width: double.infinity,
          color: Colors.black,
          margin: const EdgeInsets.symmetric(vertical: 10.0),
          ),

        Text("DEBUG FUNCTIONS"),
        Row(children: [
          Expanded(
          child: Text('Trigger extreme alert alarm', textAlign: TextAlign.center),
          ),
          Expanded(
          child: ElevatedButton(onPressed: () {}, child: Text('Alarm',) ),
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