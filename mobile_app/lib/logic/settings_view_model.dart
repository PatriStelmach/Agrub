import 'package:flutter/material.dart';

class SettingsViewModel extends ChangeNotifier {
  ThemeMode themeMode = ThemeMode.dark;


  void toggleTheme() {
    if (themeMode == ThemeMode.light) {
      themeMode = ThemeMode.dark;
    } else {
      themeMode = ThemeMode.light;
    }
    notifyListeners();
  }
}
