import 'package:alert_app/firebase_options.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

// Internal imports
import 'package:alert_app/locator.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/themes/app_theme_default.dart';
import 'package:alert_app/screens/login_screen.dart';
import 'package:alert_app/screens/general_layout_screen.dart';

// Services & Repositories
import 'package:alert_app/data/services/language_service.dart';
import 'package:alert_app/data/services/navigation_service.dart';
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
        // Serwisy globalne
        ChangeNotifierProvider(create: (_) => LanguageService()),
        ChangeNotifierProvider(create: (_) => SettingsViewModel()),
        ChangeNotifierProvider(create: (_) => GeneralLayoutViewModel()),
        Provider<PushNotificationService>(
          create: (_) => PushNotificationService()..initNotificationHandling(),
        ),
      ],
      child: Builder(
        builder: (contextAfterServices) {
          return MultiProvider(
            providers: [
              ChangeNotifierProvider(
                create: (_) =>
                    UserViewModel(repository: locator<UserRepository>()),
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
            ],
            child: Builder(
              builder: (contextAfterAlerts) {
                return MultiProvider(
                  providers: [
                    ChangeNotifierProvider(
                      lazy: false,
                      create: (_) => HomeViewModel(
                        alertsViewModel: contextAfterAlerts
                            .read<AlertsViewModel>(),
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
    final languageService = context.watch<LanguageService>();

    final settingsViewModel = context.watch<SettingsViewModel>();
    return MaterialApp(
      locale: languageService.currentLocale,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: settingsViewModel.themeMode,
      navigatorKey: locator<NavigationService>().navigatorKey,

      home: Consumer<UserViewModel>(
        builder: (context, userViewModel, child) {
          // Sprawdzanie secure storage
          if (userViewModel.isLoading) {
            return const Scaffold(
              body: Center(child: CircularProgressIndicator()),
            );
          }

          // For logged in user, show main layout
          if (userViewModel.isLoggedIn) {
            return const GeneralLayout();
          }

          // For not logged in user - show login screen
          return const LoginScreen();
        },
      ),
    );
  }
}

  /*

T0D0:

- severity change
- komentarze porządne
- testy
- AlertRemoteDataSourceTest - There is no SSE test here - T0D0 as integration test for alert repository
- refactoring, (pluginy, FCM, pozostałe drobiazgi)

  */