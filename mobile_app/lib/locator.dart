import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

final locator = GetIt.instance;

void setupLocator() {
  locator.registerLazySingleton<FlutterSecureStorage>(
    () => const FlutterSecureStorage(),
  );
  locator.registerLazySingleton<AlertRepository>(() => AlertRepository());
  locator.registerLazySingleton<UserRepository>(() => UserRepository());

  locator.registerLazySingleton<Dio>(() {
    final dio = Dio(
      BaseOptions(
        baseUrl: 'http://141.95.41.41',
        connectTimeout: const Duration(seconds: 15),
        receiveTimeout: const Duration(seconds: 15),
        headers: {'Accept': '*/*', 'Content-Type': 'application/json'},
      ),
    );

    dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) async {
          final storage = locator<FlutterSecureStorage>();

          if (options.path != '/api/auth/login') {
            String? token = await storage.read(key: 'jwt_token');
            if (token != null) {
              options.headers['Authorization'] = 'Bearer $token';
            }
          }
          return handler.next(options);
        },
      ),
    );

    return dio;
  });
}
