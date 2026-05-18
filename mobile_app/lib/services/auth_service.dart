import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthService {
  late final Dio _dio;
  final _storage = const FlutterSecureStorage();

  AuthService() {
    _dio = Dio(
      BaseOptions(
        baseUrl: 'http://141.95.41.41',
        connectTimeout: const Duration(seconds: 15),
        receiveTimeout: const Duration(seconds: 15),
        headers: {'Accept': '*/*', 'Content-Type': 'application/json'},
      ),
    );

    _dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          if (options.path != '/api/auth/login') {
            String? token = await _storage.read(key: 'jwt_token');
            if (token != null) {
              options.headers['Authorization'] = 'Bearer $token';
            }
          }
          return handler.next(options);
        },
      ),
    );
  }

  Dio get dio => _dio;

  Future<String?> login(String email, String password) async {
    try {
      final response = await _dio.post(
        '/api/auth/login',
        data: {'email': email, 'password': password},
      );

      if (response.statusCode == 200) {
        final token = response.data['access_token'];
        await _storage.write(key: 'jwt_token', value: token);
        return token;
      }
      return null;
    } catch (e) {
      print("Błąd sieci/API: $e");
      return null;
    }
  }
}
