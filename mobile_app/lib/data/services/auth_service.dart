import 'dart:convert';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:alert_app/screens/general_layout_screen.dart';
import 'package:alert_app/screens/login_screen.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:get_it/get_it.dart';
import 'package:provider/provider.dart';

class AuthService {
  final Dio _dio = GetIt.instance<Dio>();
  final FlutterSecureStorage _storage = GetIt.instance<FlutterSecureStorage>();

  AuthService();

  Future<String?> login(String email, String password, String serverIp) async {
    try {
      _dio.options.baseUrl = 'http://$serverIp';
      final response = await _dio.post(
        '/api/auth/login',
        data: {'email': email, 'password': password},
      );

      if (response.statusCode == 200) {
        final token = response.data['access_token'];
        await _storage.write(key: 'jwt_token', value: token);

        try {
          final settingsResponse = await _dio.get(
            '/api/users/me/settings',
            options: Options(headers: {'Authorization': 'Bearer $token'}),
          );

          if (settingsResponse.statusCode == 200) {
            final List<dynamic>? groups = settingsResponse.data['groups'];

            if (groups != null) {
              final List<String> stringGroups = List<String>.from(groups);
              await _storage.write(
                key: 'user_groups',
                value: json.encode(stringGroups),
              );
              debugPrint(
                "LOGIN DEBUG: Succesfully got the groups: $stringGroups",
              );
            }
          }
          await _storage.write(key: 'lastServerIp', value: serverIp);
        } catch (e) {
          debugPrint("LOGIN DEBUG:Cannot aquire groups from /me/settings: $e");
        }

        return token;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

///Checking log in status on app startup
class AuthGate extends StatefulWidget {
  const AuthGate({super.key});

  @override
  State<AuthGate> createState() => _AuthGateState();
}

class _AuthGateState extends State<AuthGate> {
  final FlutterSecureStorage _storage = GetIt.instance<FlutterSecureStorage>();
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<UserViewModel>(context, listen: false).checkAuthStatus();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<UserViewModel>(
      builder: (context, userViewModel, child) {
        if (userViewModel.isLoading) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        }
        if (userViewModel.isLoggedIn) {
          return const GeneralLayout();
        }

        return const LoginScreen();
      },
    );
  }

  Future<String?> getLastIp() async {
    return await _storage.read(key: 'lastServerIp');
  }
}
