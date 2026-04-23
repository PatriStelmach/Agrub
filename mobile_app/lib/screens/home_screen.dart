import 'package:flutter/material.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
//View Models
import 'package:alert_app/logic/home_view_model.dart';
import 'package:provider/provider.dart';





class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {

    final homeViewModel = context.watch<HomeViewModel>();
    final DateTime lastPingTime = homeViewModel.repository.lastPing;
    final String shortPingTime = "${lastPingTime.hour}:${lastPingTime.minute.toString()}:${lastPingTime.second.toString()}";
  

    return Scaffold(
      body:Padding(
        padding: const EdgeInsets.all(6), 
      child: Column(
        
        children: [
          Row(
            spacing: 50,
            children: [
            Expanded(
              child: 
              
                Row(
                  children: [
                    Text('Latest ping recorded at: ', style: TextStyle(fontSize: 20)),
                     Spacer(),
                Text(shortPingTime, style: TextStyle(fontSize: 20))
                  ],
                ),
                
              
            ),
                      ],),
        Text('Latest Alerts', style: TextStyle(fontSize: 30)),
        
            Expanded(
              child: Container(
                padding: EdgeInsets.all(16),
                decoration: BoxDecoration(
                  border: Border.all(color: Colors.black),
                  borderRadius: BorderRadius.circular(12),
                
              ),
              
                child: ListView.builder(
                itemCount: homeViewModel.latestAlerts().length,
                itemBuilder: (context, index) {
                  final alert = homeViewModel.latestAlerts()[index];

                  return Card (
                    color: alert.severityColor,
                     child: ListTile(
                     title: Text(alert.title),
                    subtitle: Text(alert.hostName),
                    leading: Icon(Icons.warning, color: Colors.black),
                    )
              );
              },
                padding: const EdgeInsets.all(10),
                scrollDirection: Axis.vertical,
                )
              ),
              ),
              
            
            Text('Latest Critical Alerts', style: TextStyle(fontSize: 30)),

              Expanded(
              child: Container(
              decoration: BoxDecoration(
                border: Border.all(color: Colors.black),
                borderRadius: BorderRadius.circular(12),
              ),
              
                child: ListView.builder(
                itemCount: homeViewModel.latestCriticalAlerts().length,
                itemBuilder: (context, index) {
                  final alert = homeViewModel.latestCriticalAlerts()[index];

                  return Card (
                    color: alert.severityColor,
                     child: ListTile(
                     title: Text(alert.title),
                    subtitle: Text(alert.hostName),
                    leading: Icon(Icons.warning, color: Colors.black),
                    )
              );
              },
                padding: const EdgeInsets.all(10),
                scrollDirection: Axis.vertical,
                )
              ),
              ),

            ]

            
      )
      
  ),
  );
    
  }
}