import 'package:alert_app/screens/plugins_screen.dart';
import 'package:alert_app/screens/home_screen.dart';
import 'package:alert_app/screens/alerts_screen.dart';
import 'package:alert_app/screens/settings_screen.dart';
import 'package:alert_app/screens/user_screen.dart';
import 'package:alert_app/screens/debug_screen.dart';
import 'package:flutter/material.dart';



class GeneralLayout extends StatefulWidget {
  const GeneralLayout({super.key});

  @override
  State<GeneralLayout> createState() => _GeneralLayoutState();
}

class _GeneralLayoutState extends State<GeneralLayout> {

String activeScreenName = 'Home';
String dropDownValue = 'Home';


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('ALERT APP'),
        bottom: PreferredSize(preferredSize: const Size.fromHeight(4.0), child: Container(color: Colors.black, height: 4.0,)),
        
        actions: <Widget>[

          ElevatedButton(onPressed: () {  setState(() {
                activeScreenName = 'Debug';
              });
              }, child: Icon(Icons.settings_applications_rounded, size: 45, color: Colors.red)),

          DropdownButton<String>(
            padding: EdgeInsets.all(6),
            borderRadius: BorderRadius.all(Radius.circular(16)),
            value: dropDownValue, 
            icon: const Icon(Icons.menu),
            style: const TextStyle(color: Colors.black),
            onChanged: (String? newValue) {
              print("Zmieniam na: $newValue");
              setState(() {
                activeScreenName = newValue!;
              });
            },
            items:const[
            DropdownMenuItem<String>(value: 'Home', child: Text('Home')),
            DropdownMenuItem<String>(value: 'Alerts', child: Text('Alerts')),
            DropdownMenuItem<String>(value: 'Plugins', child: Text('Plugins')),
            DropdownMenuItem<String>(value: 'Settings', child: Text('Settings')),
            
            ]
            ),
            IconButton(onPressed: () {

              setState(() {
                activeScreenName = 'User';
              });
              
            
            }, 
            icon: Icon(
              Icons.account_circle_rounded, 
              size: 45
              )
              ),
        ],
      ),

      body: () {
        switch (activeScreenName) {
          case 'Home': return HomeScreen();
          case 'Alerts': return AlertsScreen();
          case 'Settings': return SettingsScreen();
          case 'Plugins': return PluginsScreen();
          case 'User': return UserScreen();
          case 'Debug': return DebugScreen();
          default: return HomeScreen();
      }
      }(),
    );
    
    
  }
}






