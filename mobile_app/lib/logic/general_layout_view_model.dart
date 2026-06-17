import 'package:flutter/material.dart';

enum AppScreen { home, alerts, plugins, user, settings, history }

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

  /// Bottom navigation
  void setPageByIndex(int index) {
    if (index >= 0 && index < AppScreen.values.length) {
      final newScreen = AppScreen.values[index];

      if (_activeScreen != newScreen) {
        _activeScreen = newScreen;
        notifyListeners();
      }
    }
  }
}
