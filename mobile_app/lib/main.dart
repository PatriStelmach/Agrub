import 'package:alert_app/data/services/auth_service.dart';
import 'package:alert_app/firebase_options.dart';
import 'package:alert_app/logic/debug_view_model.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

// Internal imports
import 'package:alert_app/locator.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/themes/app_theme_default.dart';

// Services & Repositories
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:alert_app/data/services/push_notification_service.dart';
import 'package:alert_app/data/services/fcm_background_handler.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';
import 'package:alert_app/data/repositories/user_repository.dart';

// ViewModels
import 'package:alert_app/logic/general_layout_view_model.dart';
import 'package:alert_app/logic/settings_view_model.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';

Future<void> main() async {
  //checking if flutter engine is ready
  WidgetsFlutterBinding.ensureInitialized();

  // Firebase initialization
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);

  await setupLocator();

  FirebaseMessaging.onBackgroundMessage(firebaseMessagingBackgroundHandler);

  runApp(const AppStateProvider());
}

class AppStateProvider extends StatelessWidget {
  const AppStateProvider({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        // Global services
        ChangeNotifierProvider(create: (_) => SettingsViewModel()),
        Provider<PushNotificationService>(
          create: (_) => PushNotificationService()..initNotificationHandling(),
        ),
      ],
      child: Builder(
        builder: (contextAfterServices) {
          return MultiProvider(
            providers: [
              ChangeNotifierProvider(
                create: (_) => UserViewModel(
                  userRepository: locator<UserRepository>(),
                  dio: locator<Dio>(),
                ),
              ),
              ChangeNotifierProvider(
                lazy: false,
                create: (_) => AlertsViewModel(
                  alertsRepository: locator<AlertRepository>(),
                  pushNotificationService: contextAfterServices
                      .read<PushNotificationService>(),
                ),
              ),

              ChangeNotifierProvider(
                create: (_) => PluginsViewModel(
                  pluginsRepository: locator<PluginRepository>(),
                ),
              ),
              ChangeNotifierProvider(
                create: (_) =>
                    DebugViewModel(alarmService: locator<AlarmService>()),
              ),
            ],
            child: Builder(
              builder: (contextAfterAlerts) {
                return MultiProvider(
                  providers: [
                    ChangeNotifierProvider(
                      create: (_) => GeneralLayoutViewModel(
                        userViewModel: contextAfterAlerts.read<UserViewModel>(),
                        alertsViewModel: contextAfterAlerts
                            .read<AlertsViewModel>(),
                        pushNotificationService: contextAfterAlerts
                            .read<PushNotificationService>(),
                      ),
                    ),
                    ChangeNotifierProvider(
                      lazy: false,
                      create: (_) => HomeViewModel(
                        alertsViewModel: contextAfterAlerts
                            .read<AlertsViewModel>(),
                        alertsRepository: locator<AlertRepository>(),
                      )..getMyToken(),
                    ),
                  ],
                  child: const MainApp(),
                );
              },
            ),
          );
        },
      ),
    );
  }
}

class MainApp extends StatelessWidget {
  const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    final settingsViewModel = context.watch<SettingsViewModel>();
    return MaterialApp(
      locale: settingsViewModel.currentLocale,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: settingsViewModel.themeMode,
      navigatorKey: locator<AlarmService>().navigatorKey,

      home: const AuthGate(),
    );
  }
}


  /*

T0D0:

- severity change
- komentarze porządne
- testy
- AlertRemoteDataSourceTest - There is no SSE test here - T0D0 as integration test for alert repository
- refactoring, (FCM, pozostałe drobiazgi)
- CONSIDER: refactoring, wywalenie wszystkich helper widget na rzecz class widget

  */