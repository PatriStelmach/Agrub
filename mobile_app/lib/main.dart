import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:alert_app/data/services/language_service.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/locator.dart';
import 'package:alert_app/logic/general_layout_view_model.dart';
import 'package:alert_app/logic/settings_view_model.dart';
import 'package:alert_app/screens/login_screen.dart';
import 'package:alert_app/data/services/navigation_service.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:alert_app/screens/general_layout_screen.dart';
import 'package:alert_app/data/services/push_notification_service.dart';
import 'package:alert_app/themes/app_theme_default.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:alert_app/data/services/fcm_background_handler.dart';

Future<void> main() async {
  //checking if flutter engine is ready
  WidgetsFlutterBinding.ensureInitialized();

  // Firebase initialization
  await Firebase.initializeApp();

  setupLocator();

  FirebaseMessaging.onBackgroundMessage(firebaseMessagingBackgroundHandler);
  //Creation of repositories instances
  final alertRepository = locator<AlertRepository>();
  final pluginRepository = PluginsRepository();
  final userRepository = UserRepository();

  final notificationService = PushNotificationService(alertRepository);

  await notificationService.initNotificationHandling();

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => LanguageService()),
        ChangeNotifierProvider(create: (_) => GeneralLayoutViewModel()),
        ChangeNotifierProvider(create: (_) => SettingsViewModel()),
        ChangeNotifierProvider.value(value: notificationService),
        ChangeNotifierProvider.value(value: userRepository),

        ChangeNotifierProvider<UserViewModel>(
          create: (context) =>
              UserViewModel(repository: context.read<UserRepository>()),
        ),

        ChangeNotifierProxyProvider<UserViewModel, AlertRepository>(
          create: (context) => alertRepository,
          lazy: false,
          update: (context, userViewModel, alertRepo) {
            if (userViewModel.isLoggedIn && alertRepo!.alertsCache.isEmpty) {
              alertRepo.updateAllAlerts();
              alertRepo.initSseConnection();
            }
            return alertRepo!;
          },
        ),

        //Creation of view models
        ChangeNotifierProxyProvider<UserViewModel, PluginsRepository>(
          create: (context) => pluginRepository,
          update: (context, userViewModel, pluginsRepo) {
            if (userViewModel.isLoggedIn && pluginsRepo!.pluginsCache.isEmpty) {
              pluginsRepo.updateAllPlugins();
            }
            return pluginsRepo!;
          },
        ),

        ChangeNotifierProvider<HomeViewModel>(
          lazy: false,
          create: (context) =>
              HomeViewModel(repository: context.read<AlertRepository>())
                ..getMyToken(),
        ),

        ChangeNotifierProvider<AlertsViewModel>(
          create: (context) => AlertsViewModel(
            alertsRepository: context.read<AlertRepository>(),
          ),
        ),

        ChangeNotifierProvider<PluginsViewModel>(
          create: (context) => PluginsViewModel(
            pluginsRepository: context.read<PluginsRepository>(),
          ),
        ),
      ],
      child: MainApp(),
    ),
  );
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
      navigatorKey: navigationService.navigatorKey,

      home: Consumer<UserViewModel>(
        builder: (context, userViewModel, child) {
          // Sprawdzanie secure storage
          if (userViewModel.isLoading) {
            return const Scaffold(
              body: Center(child: CircularProgressIndicator()),
            );
          }

          // \zalogowany -  główny layout
          if (userViewModel.isLoggedIn) {
            return const GeneralLayout();
          }

          // niezalogowany - ekran logowania
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
- refactoring, uporządkowanie tego co robią servicy/repo/view models zgodnie z MVVM, uporządkowanie rzeczy zgodnie z DRY itd.



  */