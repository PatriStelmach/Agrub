

import 'package:alert_app/data/models/user_model.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:flutter/material.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

class UserViewModel extends ChangeNotifier{

final UserRepository repository;

  UserModel? _user;
  UserModel? get user => _user;
  bool _isLoggedIn = false;
  bool _isLoading = false;

  bool get isLoggedIn => _isLoggedIn;
  bool get isLoading => _isLoading;

  UserViewModel({required this.repository}) {
    checkAuthStatus();
  }

Future<void> checkAuthStatus() async {
  _isLoading = true;
  notifyListeners();

  String? token = await repository.getToken();
  if (token != null && !JwtDecoder.isExpired(token)) {
    _user = repository.getUserFromToken(token);
    _isLoggedIn = (_user != null);
  } else {
    _isLoggedIn = false;
  }

  _isLoading = false;
  notifyListeners();
}

Future<bool> signIn(String email, String password) async {
  _isLoading = true;
  notifyListeners();

  bool success = await repository.login(email, password);
  if (success) {
    final token = await repository.getToken();
    if (token != null) {
      _user = repository.getUserFromToken(token);
      _isLoggedIn = true;
    }
  }

  _isLoading = false;
  notifyListeners();
  return success;
}

  void signOut() {}

  
}
