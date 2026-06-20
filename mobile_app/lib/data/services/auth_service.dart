import 'dart:convert';
import 'package:alert_app/logic/general_layout_view_model.dart';
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
              print("LOGIN DEBUG: Succesfully got the groups: $stringGroups");
            }

          }
                  await _storage.write(key: 'lastServerIp', value: serverIp);

        } catch (e) {
          print("LOGIN DEBUG:Cannot aquire groups from /me/settings: $e");
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
class AuthGate extends StatelessWidget {
  final FlutterSecureStorage _storage = GetIt.instance<FlutterSecureStorage>();

  //T0D0 should be universald "guard" listening to login, that way no navigation manipulation would be needed in user screen
  
 AuthGate({super.key});
   
  @override
  Widget build(BuildContext context) {
    return FutureBuilder<bool>(
      future: context.read<UserViewModel>().checkAuthStatus(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        }

        final isLoggedIn = snapshot.data ?? false;
        if (isLoggedIn) {
          WidgetsBinding.instance.addPostFrameCallback((_) {
          
            context.read<GeneralLayoutViewModel>().changePage(AppScreen.home);
          });

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
