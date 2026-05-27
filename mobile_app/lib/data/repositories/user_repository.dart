import 'package:alert_app/data/models/user_model.dart';
import 'package:alert_app/data/services/auth_service.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

//connecting to server to check authorization of user

class UserRepository extends ChangeNotifier {
  final Dio dio;
  final AuthService _authService = AuthService();
  final _storage = const FlutterSecureStorage();

  UserRepository(this.dio);

  Future<bool> login(String email, String password) async {
    // 1. Pytamy API o token
    final String? token = await _authService.login(email, password);

    // 2. Jeśli token jest, zapisujemy go i zwracamy true
    if (token != null) {
      await _storage.write(key: 'jwt_token', value: token);
      return true;
    }

    // 3. Jeśli nie ma tokena, logowanie się nie udało
    return false;
  }

  Future<String?> getToken() async => await _storage.read(key: 'jwt_token');

  Future<void> logout() async => await _storage.delete(key: 'jwt_token');

  UserModel? getUserFromToken(String token) {
    if (token.isEmpty) return null;

    // Decoding token to get user data
    Map<String, dynamic> decodedToken = JwtDecoder.decode(token);
    //Debug do sprawdzenia czy mam info dobre
    debugPrint("JWT DECODED: $decodedToken");

    return UserModel.fromJwt(decodedToken);
  }

  Future<String> getCurrentUserGroup() async {
    final token = await getToken();
    if (token == null || token.isEmpty) return 'None';

    final user = getUserFromToken(token);
    return user?.group ?? 'None';
  }

  Future<UserModel?> getCurrentUser() async {
    final token = await getToken();
    if (token == null || token.isEmpty) return null;
    return getUserFromToken(token);
  }
}
