import 'package:alert_app/logic/general_layout_view_model.dart';
import 'package:alert_app/screens/debug_screen.dart';
import 'package:alert_app/screens/plugins_screen.dart';
import 'package:alert_app/screens/home_screen.dart';
import 'package:alert_app/screens/alerts_screen.dart';
import 'package:alert_app/screens/settings_screen.dart';
import 'package:alert_app/screens/user_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';



class GeneralLayout extends StatelessWidget {
  const GeneralLayout({super.key});

  

@override
  Widget build(BuildContext context) {
    final layoutViewModel = context.watch<GeneralLayoutViewModel>();


    return Scaffold(
      appBar: AppBar(
        title: Text(layoutViewModel.activeScreenName.toUpperCase()), // Dynamiczny tytuł
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(4.0),
          child: Container(color: Colors.black, height: 4.0),
        ),

      ),

 
      drawer: Drawer(
        child: Column(
          children: [
            const DrawerHeader(child: Center(child: Text("ALERT MENU"))),
            ListTile(
              leading: const Icon(Icons.home),
              title: const Text("Home"),
              onTap: () {
                layoutViewModel.changePage('Home');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.warning),
              title: const Text("Alerts"),
              onTap: () {
                layoutViewModel.changePage('Alerts');
                Navigator.pop(context);
              },
            ),
               ListTile(
              leading: const Icon(Icons.computer_rounded),
              title: const Text("Plugins"),
              onTap: () {
                layoutViewModel.changePage('Plugins');
                Navigator.pop(context);
              },
            ),
               ListTile(
              leading: const Icon(Icons.settings),
              title: const Text("Settings"),
              onTap: () {
                layoutViewModel.changePage('Settings');
                Navigator.pop(context);
              },
            ),
                          ListTile(
              leading: const Icon(Icons.person_3_outlined),
              title: const Text("User"),
              onTap: () {
                layoutViewModel.changePage('User');
                Navigator.pop(context);
              },
            ),
               ListTile(
              leading: const Icon(Icons.warning),
              title: const Text("Debug"),
              onTap: () {
                layoutViewModel.changePage('Debug');
                Navigator.pop(context);
              },
            ),
          ],
        ),
      ),

      body: _buildBody(layoutViewModel.activeScreenName),
    );
  }

Widget _buildBody(String screenName) {
    switch (screenName) {
      case 'Alerts': return const AlertsScreen();
      case 'Plugins': return const PluginsScreen();
      case 'Settings': return const SettingsScreen();
      case 'Debug': return const DebugScreen();
      case 'User': return const UserScreen();
      default: return const HomeScreen();
    }
  }
}

