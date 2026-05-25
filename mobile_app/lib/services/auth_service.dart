import 'dart:convert';
import 'package:alert_app/services/push_notification_service.dart';
import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:get_it/get_it.dart';

class AuthService {
  final Dio _dio = GetIt.instance<Dio>();
  final FlutterSecureStorage _storage = GetIt.instance<FlutterSecureStorage>();

  AuthService();

  Future<String?> login(String email, String password) async {
    try {
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
        } catch (e) {
          print("LOGIN DEBUG:Cannot aquire groups from /me/settings: $e");
        }

        return token;
      }
      return null;
    } catch (e) {
      print("Błąd sieci/API: $e");
      return null;
    }
  }

  Future<void> logout() async {
    try {
      await _storage.delete(key: 'jwt_token');
      await _storage.delete(key: 'user_groups');

      print("Użytkownik został pomyślnie wylogowany (wyczyszczono pamięć).");
    } catch (e) {
      print("Błąd podczas czyszczenia pamięci przy wylogowywaniu: $e");
    }
  }
}
