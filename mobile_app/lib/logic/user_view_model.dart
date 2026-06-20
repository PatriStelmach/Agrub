import 'dart:async';

import 'package:alert_app/data/models/user_model.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:get_it/get_it.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

class UserViewModel extends ChangeNotifier {
  final UserRepository repository;
    final FlutterSecureStorage _storage = GetIt.instance<FlutterSecureStorage>();
final Dio dio;

  UserModel? _user;
  bool _isLoggedIn = false;
  bool _isLoading = false;

  UserModel? get user => _user;
  bool get isLoggedIn => _isLoggedIn;
  bool get isLoading => _isLoading;

  UserViewModel({required this.repository, required this.dio});

  ///Authorization method, returns true when logged in
  Future<bool> checkAuthStatus() async {
    _setLoading(true);
    try {
      final String? token = await repository.getToken();
      if (token != null && !JwtDecoder.isExpired(token)) {
        _user = repository.getUserFromToken(token);
        _isLoggedIn = (_user != null);

        //T0D0 - breaking architecture, place in repo in The Great Rewriting
        final lastIp =  await repository.getLastIp();
        if( lastIp != "") {
       dio.options.baseUrl = ('http://$lastIp');
        }
        else {
          _isLoggedIn = false;
        }
        

        

      
        
      } else {
        _isLoggedIn = false;
      }
    } catch (e) {
      debugPrint("USER VIEW MODEL - checkAuthStatus ERROR: $e");
      _isLoggedIn = false;
      _user = null;
    } finally {
      _setLoading(false);
    }
    return _isLoggedIn;
  }

  ///Log in the user
  Future<bool> signIn(String email, String password, String serverIp) async {
    _setLoading(true);
    bool success = false;

    try {
      success = await repository.login(email, password, serverIp);
      if (success) {
        final token = await repository.getToken();
        if (token != null) {
          _user = repository.getUserFromToken(token);
          _isLoggedIn = true;
        }
      }
    } catch (e) {
      debugPrint("USER VIEW MODEL - signIn ERROR: $e");
      success = false;
      _isLoggedIn = false;
    } finally {
      _setLoading(false);
    }
    return success;
  }

  Future<void> signOut() async {
    _setLoading(true);

    try {
      await repository.logout();
    } catch (e) {
      debugPrint("USER VIEW MODEL ERROR: $e");
    } finally {
      _user = null;
      _isLoggedIn = false;
      _setLoading(false);
    notifyListeners();
    }
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }
}
