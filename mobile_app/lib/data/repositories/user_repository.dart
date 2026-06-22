import 'package:alert_app/data/models/user_model.dart';
import 'package:alert_app/data/services/auth_service.dart';
import 'package:alert_app/screens/login_screen.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

///Login and getting user information for View Model to use
class UserRepository {
  final Dio dio;
  final AuthService authService;
  final FlutterSecureStorage storage;
  final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

  UserRepository({
    required this.dio,
    required this.authService,
    required this.storage,
  });

  Future<bool> login(String email, String password, String serverIp) async {
    final String? token = await authService.login(email, password, serverIp);

    if (token != null) {
      await storage.write(key: 'jwt_token', value: token);
      return true;
    }
    return false;
  }

  Future<String?> getToken() async {
    return await storage.read(key: 'jwt_token');
  }

  Future<String?> getLastIp() async {
    return await storage.read(key: 'lastServerIp');
  }

  void setBaseUrl(String base) {
    dio.options.baseUrl = base;
  }

  Future<void> logout() async {
    await storage.delete(key: 'jwt_token');
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await navigatorKey.currentState?.push(
        PageRouteBuilder(
          opaque: false,
          barrierColor: Colors.black.withValues(alpha: 0.7),
          pageBuilder: (context, _, _) => const LoginScreen(),
          transitionsBuilder: (context, animation, _, child) {
            return FadeTransition(opacity: animation, child: child);
          },
        ),
      );
    });
  }

  UserModel? getUserFromToken(String token) {
    if (token.isEmpty) return null;

    final Map<String, dynamic> decodedToken = JwtDecoder.decode(token);
    debugPrint("JWT DECODED: $decodedToken");

    return UserModel.fromJwt(decodedToken);
  }

  Future<String> getCurrentUserRole() async {
    final token = await getToken();
    if (token == null || token.isEmpty) return 'None';

    final user = getUserFromToken(token);
    return user?.role ?? 'USER';
  }

  Future<UserModel?> getCurrentUser() async {
    final token = await getToken();
    if (token == null || token.isEmpty) return null;
    return getUserFromToken(token);
  }
}
