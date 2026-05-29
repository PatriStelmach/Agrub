import 'package:alert_app/data/datasources/alert_local_data_source.dart';
import 'package:alert_app/data/datasources/alert_remote_data_source.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:alert_app/data/services/auth_service.dart';
import 'package:dio/dio.dart';
import 'package:get_it/get_it.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:alert_app/data/services/navigation_service.dart';
import 'package:shared_preferences/shared_preferences.dart';

final locator = GetIt.instance;

Future<void> setupLocator() async {
  final sharedPreferences = await SharedPreferences.getInstance();
  locator.registerSingleton<SharedPreferences>(sharedPreferences);

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

  locator.registerLazySingleton<AlertLocalDataSource>(
    () => AlertLocalDataSourceImpl(
      sharedPreferences: locator<SharedPreferences>(),
    ),
  );

  locator.registerLazySingleton<AlertRemoteDataSource>(
    () => AlertRemoteDataSourceImpl(dio: locator<Dio>()),
  );

  locator.registerLazySingleton<FlutterSecureStorage>(
    () => const FlutterSecureStorage(),
  );

  locator.registerLazySingleton<AuthService>(() => AuthService());
  locator.registerLazySingleton<AlertRepository>(
    () => AlertRepository(
      remoteDataSource: locator<AlertRemoteDataSource>(),
      localDataSource: locator<AlertLocalDataSource>(),
    ),
  );

  locator.registerLazySingleton<NavigationService>(() => NavigationService());

  locator.registerLazySingleton<UserRepository>(
    () => UserRepository(
      dio: locator<Dio>(),
      authService: locator<AuthService>(),
      storage: FlutterSecureStorage(),
    ),
  );
  locator.registerLazySingleton<PluginRepository>(
    () => PluginRepository(dio: locator<Dio>()),
  );
}
