import 'package:alert_app/data/models/user_model.dart';
import 'package:alert_app/data/services/auth_service.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

///Login and getting user information for View Model to use
class UserRepository {
  final Dio dio;
  final AuthService authService;
  final FlutterSecureStorage storage;

  UserRepository({
    required this.dio,
    required this.authService,
    required this.storage,
  });

  Future<bool> login(String email, String password) async {
    final String? token = await authService.login(email, password);

    if (token != null) {
      await storage.write(key: 'jwt_token', value: token);
      return true;
    }
    return false;
  }

  Future<String?> getToken() async => await storage.read(key: 'jwt_token');

  Future<void> logout() async => await storage.delete(key: 'jwt_token');

  UserModel? getUserFromToken(String token) {
    if (token.isEmpty) return null;

    final Map<String, dynamic> decodedToken = JwtDecoder.decode(token);
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
