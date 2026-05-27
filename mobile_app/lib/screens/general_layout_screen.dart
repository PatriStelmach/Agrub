import 'package:alert_app/logic/general_layout_view_model.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:alert_app/screens/debug_screen.dart';
import 'package:alert_app/screens/plugins_screen.dart';
import 'package:alert_app/screens/home_screen.dart';
import 'package:alert_app/screens/alerts_screen.dart';
import 'package:alert_app/screens/settings_screen.dart';
import 'package:alert_app/screens/user_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:alert_app/l10n/app_localizations.dart';

class GeneralLayout extends StatefulWidget {
  const GeneralLayout({super.key});

  @override
  State<GeneralLayout> createState() => _GeneralLayoutState();
}

class _GeneralLayoutState extends State<GeneralLayout>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    // Rejestrujemy nasłuchiwanie powrotu z tła dla całej aplikacji
    WidgetsBinding.instance.addObserver(this);

    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        context.read<HomeViewModel>().refresh();
      }
    });
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    debugPrint("GeneralLayout: Zmiana stanu aplikacji na: $state");

    if (state == AppLifecycleState.resumed) {
      debugPrint(
        "GeneralLayout: Aplikacja wybudzona! Synchronizuję cache z dyskiem i API...",
      );
      if (mounted) {
        context.read<HomeViewModel>().refresh();
      }
    }
  }

  String _getScreenTitle(String screenName, AppLocalizations t) {
    switch (screenName) {
      case 'Alerts':
        return t.layout_menu_alerts;
      case 'Plugins':
        return t.layout_menu_plugins;
      case 'Settings':
        return t.layout_menu_settings;
      case 'Debug':
        return t.layout_menu_debug;
      case 'User':
        return t.layout_menu_user;
      default:
        return t.layout_menu_home;
    }
  }

  @override
  Widget build(BuildContext context) {
    final layoutViewModel = context.watch<GeneralLayoutViewModel>();
    final t = AppLocalizations.of(context)!;

    return Scaffold(
      appBar: AppBar(
        title: Text(
          _getScreenTitle(layoutViewModel.activeScreenName, t).toUpperCase(),
        ),
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(4.0),
          child: Container(color: Colors.black, height: 4.0),
        ),
      ),

      drawer: Drawer(
        child: Column(
          children: [
            DrawerHeader(child: Center(child: Text(t.layout_menu_header))),
            ListTile(
              leading: const Icon(Icons.home),
              title: Text(t.layout_menu_home),
              onTap: () {
                layoutViewModel.changePage('Home');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.warning),
              title: Text(t.layout_menu_alerts),
              onTap: () {
                layoutViewModel.changePage('Alerts');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.computer_rounded),
              title: Text(t.layout_menu_plugins),
              onTap: () {
                layoutViewModel.changePage('Plugins');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.settings),
              title: Text(t.layout_menu_settings),
              onTap: () {
                layoutViewModel.changePage('Settings');
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: const Icon(Icons.person_3_outlined),
              title: Text(t.layout_menu_user),
              onTap: () {
                layoutViewModel.changePage('User');
                Navigator.pop(context);
              },
            ),
            // Commenting way to debug screen for now
            // ListTile(
            //   leading: const Icon(Icons.warning),
            //   title: const Text(t.layout_menu_debug),
            //   onTap: () {
            //     layoutViewModel.changePage('Debug');
            //     Navigator.pop(context);
            //   },
            // ),
          ],
        ),
      ),

      body: _buildBody(layoutViewModel.activeScreenName),
    );
  }

  Widget _buildBody(String screenName) {
    switch (screenName) {
      case 'Alerts':
        return const AlertsScreen();
      case 'Plugins':
        return const PluginsScreen();
      case 'Settings':
        return const SettingsScreen();
      case 'Debug':
        return const DebugScreen();
      case 'User':
        return const UserScreen();
      default:
        return const HomeScreen();
    }
  }
}
