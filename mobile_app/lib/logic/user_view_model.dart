import 'dart:async';
import 'package:alert_app/data/models/user_model.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

class UserViewModel extends ChangeNotifier {
  final UserRepository userRepository;
  final Dio dio;

  UserModel? _user;
  bool _isLoggedIn = false;
  bool _isLoading = false;

  UserModel? get user => _user;
  bool get isLoggedIn => _isLoggedIn;
  bool get isLoading => _isLoading;

  UserViewModel({required this.userRepository, required this.dio});

  ///Logging in the user
  Future<bool> signIn(String email, String password, String serverIp) async {
    _isLoading = true;
    notifyListeners();
    bool success = false;

    try {
      success = await userRepository.login(email, password, serverIp);
      if (success) {
        final token = await userRepository.getToken();
        if (token != null) {
          _user = userRepository.getUserFromToken(token);
          _isLoggedIn = true;
        }
      }
    } catch (e) {
      debugPrint("USER VIEW MODEL - signIn issue, error message: $e");
      success = false;
      _isLoggedIn = false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
    return success;
  }

  /// Log out function exposed for user screen
  Future<void> signOut() async {
    _isLoading = true;
    notifyListeners();

    await userRepository.logout();

    _user = null;
    _isLoggedIn = false;
    _isLoading = false;
    notifyListeners();
  }

  ///Authorization method, returns true when logged in
  Future<bool> checkAuthorizationStatus() async {
    _isLoading = true;
    notifyListeners();

    final String? token = await userRepository.getToken();
    if (token != null && !JwtDecoder.isExpired(token)) {
      _user = userRepository.getUserFromToken(token);
      _isLoggedIn = true;

      final lastIp = await userRepository.checkLastIp();
      if (!lastIp) {
        _isLoggedIn = false;
      }
    } else {
      _isLoggedIn = false;
      _user = null;
    }

    _isLoading = false;

    notifyListeners();
    return _isLoggedIn;
  }
}
