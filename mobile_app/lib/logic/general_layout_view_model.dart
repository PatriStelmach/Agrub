import 'package:flutter/material.dart';

class GeneralLayoutViewModel extends ChangeNotifier {
  String _activeScreenName = 'Home';

  String get activeScreenName => _activeScreenName;

  void changePage(String newPage) {
    _activeScreenName = newPage;
    notifyListeners();
  }
}
