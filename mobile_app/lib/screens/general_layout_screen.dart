import 'dart:async';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/general_layout_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/data/services/push_notification_service.dart';

import 'package:alert_app/screens/home_screen.dart';
import 'package:alert_app/screens/alerts_screen.dart';
import 'package:alert_app/screens/plugins_screen.dart';
import 'package:alert_app/screens/user_screen.dart';
import 'package:alert_app/screens/settings_screen.dart';

class GeneralLayout extends StatefulWidget {
  const GeneralLayout({super.key});

  @override
  State<GeneralLayout> createState() => _GeneralLayoutState();
}

class _GeneralLayoutState extends State<GeneralLayout> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _startBackgroundServices();
    });
  }

  Future<void> _startBackgroundServices() async {
    if (!mounted) return;

    final userViewModel = context.read<UserViewModel>();
    final alertsViewModel = context.read<AlertsViewModel>();
    final pushService = context.read<PushNotificationService>();

    final token = await userViewModel.repository.getToken();
    final user = userViewModel.user;

    if (token != null && user != null) {
      alertsViewModel.initSseConnection(userGroup: user.group, token: token);
      unawaited(alertsViewModel.fetchInitialAlerts());

      try {
        unawaited(pushService.registerDevice(token));
        debugPrint("LAYOUT DEBUG: FCM Device registered successfully.");
      } catch (e) {
        debugPrint("LAYOUT DEBUG: Error with FCM registration: $e");
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final layoutViewModel = context.watch<GeneralLayoutViewModel>();
    final t = AppLocalizations.of(context)!;

    const List<Widget> screens = [
      HomeScreen(),
      AlertsScreen(),
      PluginsScreen(),
      UserScreen(),
      SettingsScreen(),
    ];

    return Scaffold(
      appBar: AppBar(
        title: Text(_getAppBarTitle(layoutViewModel.activeScreen, t)),
        elevation: 2,
      ),

      drawer: const NavDrawer(),

      body: IndexedStack(
        index: layoutViewModel.currentIndex,
        children: screens,
      ),
    );
  }

  /// Showing correct title in app bar
  String _getAppBarTitle(AppScreen screen, AppLocalizations t) {
    switch (screen) {
      case AppScreen.home:
        return 'Home';
      case AppScreen.alerts:
        return 'Alerts';
      case AppScreen.plugins:
        return 'Plugins';
      case AppScreen.user:
        return 'User Profile';
      case AppScreen.settings:
        return 'Settings';
    }
  }
}

/// Main class for nav side drawer
class NavDrawer extends StatelessWidget {
  const NavDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    final layoutViewModel = context.watch<GeneralLayoutViewModel>();

    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          DrawerHeader(
            decoration: BoxDecoration(color: Theme.of(context).primaryColor),
            child: const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Text(
                  'Alert App',
                  style: TextStyle(color: Colors.white, fontSize: 24),
                ),
              ],
            ),
          ),
          _DrawerItemWidget(
            icon: Icons.home,
            title: 'Home',
            targetScreen: AppScreen.home,
            currentScreen: layoutViewModel.activeScreen,
            onTap: () => _navigate(context, layoutViewModel, AppScreen.home),
          ),
          _DrawerItemWidget(
            icon: Icons.warning_amber_rounded,
            title: 'Alerts',
            targetScreen: AppScreen.alerts,
            currentScreen: layoutViewModel.activeScreen,
            onTap: () => _navigate(context, layoutViewModel, AppScreen.alerts),
          ),
          _DrawerItemWidget(
            icon: Icons.extension,
            title: 'Plugins',
            targetScreen: AppScreen.plugins,
            currentScreen: layoutViewModel.activeScreen,
            onTap: () => _navigate(context, layoutViewModel, AppScreen.plugins),
          ),
          _DrawerItemWidget(
            icon: Icons.person,
            title: 'User Profile',
            targetScreen: AppScreen.user,
            currentScreen: layoutViewModel.activeScreen,
            onTap: () => _navigate(context, layoutViewModel, AppScreen.user),
          ),
          _DrawerItemWidget(
            icon: Icons.settings,
            title: 'Settings',
            targetScreen: AppScreen.settings,
            currentScreen: layoutViewModel.activeScreen,
            onTap: () =>
                _navigate(context, layoutViewModel, AppScreen.settings),
          ),
        ],
      ),
    );
  }

  /// Drawer navigation
  void _navigate(
    BuildContext context,
    GeneralLayoutViewModel layoutViewModel,
    AppScreen screen,
  ) {
    layoutViewModel.changePage(screen);
    Navigator.pop(context);
  }
}

/// Single responsive nav element
class _DrawerItemWidget extends StatelessWidget {
  final IconData icon;
  final String title;
  final AppScreen targetScreen;
  final AppScreen currentScreen;
  final VoidCallback onTap;

  const _DrawerItemWidget({
    required this.icon,
    required this.title,
    required this.targetScreen,
    required this.currentScreen,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final isSelected = targetScreen == currentScreen;
    final pressedColor = Colors.yellow;

    return ListTile(
      leading: Icon(icon, color: isSelected ? pressedColor : Colors.grey[600]),
      title: Text(
        title,
        style: TextStyle(
          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
          color: isSelected ? pressedColor : null,
        ),
      ),
      selected: isSelected,
      onTap: onTap,
    );
  }
}
