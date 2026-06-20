import 'package:flutter/material.dart';

enum AppScreen { home, alerts, plugins, user, settings}

class GeneralLayoutViewModel extends ChangeNotifier {
  AppScreen _activeScreen = AppScreen.home;

  AppScreen get activeScreen => _activeScreen;

  int get currentIndex => _activeScreen.index;

  /// Change the page using enum
  void changePage(AppScreen newPage) {
    if (_activeScreen != newPage) {
      _activeScreen = newPage;
      notifyListeners();
    }
  }
}
