import 'package:flutter/material.dart';

class SettingsViewModel extends ChangeNotifier {
  Locale _currentLocale = const Locale('en');

  ThemeMode _themeMode = ThemeMode.dark;

  Locale get currentLocale => _currentLocale;

  ThemeMode get themeMode => _themeMode;

  ///Toggling between themes
  void toggleTheme() {
    _themeMode == ThemeMode.light
        ? _themeMode = ThemeMode.dark
        : _themeMode = ThemeMode.light;

    notifyListeners();
  }

  ///Changing global language
  void changeLanguage(String languageCode) {
    _currentLocale = Locale(languageCode);
    notifyListeners();
  }
}
