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

  ///Login method with token storage
  Future<bool> login(String email, String password, String serverIp) async {
    final String? token = await authService.login(email, password, serverIp);

    if (token != null) {
      await storage.write(key: 'JWT_TOKEN', value: token);
      await storage.write(key: 'LAST_SERVER_IP', value: serverIp);
      return true;
    }
    return false;
  }

  ///Reading saved JWT token from storage
  Future<String?> getToken() async {
    return await storage.read(key: 'JWT_TOKEN');
  }

  ///Reading saved server address from storage and setting it as current ip if it exists
  Future<bool> checkLastIp() async {
    final readIp = await storage.read(key: 'LAST_SERVER_IP');
    if (readIp != null) {
      dio.options.baseUrl = ('http://$readIp');
      return true;
    } else {
      return false;
    }
  }

  ///Logout method
  Future<void> logout() async {
    await storage.delete(key: 'JWT_TOKEN');
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

  ///Getting user data from token and exposing method to view model
  UserModel getUserFromToken(String token) {
    final Map<String, dynamic> decodedToken = JwtDecoder.decode(token);
    debugPrint("USER REPOSITORY - JWT token decoded: $decodedToken");

    return UserModel.fromJwt(decodedToken);
  }
}
