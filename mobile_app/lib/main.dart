import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:alert_app/firebase_options.dart';
import 'package:alert_app/screens/login_screen.dart';
import 'package:alert_app/services/auth_service.dart';
import 'package:alert_app/services/navigation_service.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:alert_app/screens/general_layout_screen.dart';
import 'package:alert_app/services/push_notification_service.dart';
import 'package:alert_app/themes/app_theme_default.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';


//This process is responsible for handling notifications coming when app is not running.
//Must be top-level, out of main 
@pragma('vm:entry-point')
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
  
  debugPrint("Handling a background message: ${message.messageId}");

}

Future<void> main() async {

//checking if flutter engine is ready
  WidgetsFlutterBinding.ensureInitialized();

  // Firebase initialization
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );


  final authService = AuthService();
  final dioInstance = authService.dio;
  //Creation of repositories instances
  final alertRepository = AlertRepository(dio: dioInstance);
  final pluginRepository = PluginsRepository();
  final userRepository = UserRepository();

  

  final notificationService = PushNotificationService(alertRepository);

  await notificationService.initNotificationHandling();


  runApp(
    MultiProvider(
      providers: [

        ChangeNotifierProvider.value(value: alertRepository),
        ChangeNotifierProvider.value(value: pluginRepository),
        ChangeNotifierProvider.value(value: notificationService),
        ChangeNotifierProvider.value(value: userRepository),



        ChangeNotifierProvider<UserViewModel>(
          create: (context) => UserViewModel(
            repository: context.read<UserRepository>(),
          ),
        ),

        ChangeNotifierProxyProvider<UserViewModel, AlertRepository>(
          create: (context) => alertRepository,
          update: (context, userVM, alertRepo) {
            if (userVM.isLoggedIn && alertRepo!.alertsCache.isEmpty) {
              
              alertRepo.updateAllAlerts();
              alertRepo.initSseConnection();
            }
            return alertRepo!;
          },
        ),
        
        //Creation of view models
        ChangeNotifierProvider<PluginsViewModel>(
          create: (context) => PluginsViewModel(
            pluginsRepository: context.read<PluginsRepository>(),
          )),

        ChangeNotifierProvider<HomeViewModel>(
          create: (context) => HomeViewModel(
            repository: context.read<AlertRepository>(),
          )..getMyToken(),
          ),


        ChangeNotifierProvider<AlertsViewModel>(
          create: (context) => AlertsViewModel(
            alertsRepository: context.read<AlertRepository>(),
          )),
          
        ChangeNotifierProvider<UserViewModel>(
          create: (context) => UserViewModel(
            repository: context.read<UserRepository>(),
          )),
        
      ],
      child: MainApp(),
    ),
  );
}

class MainApp extends StatelessWidget {
const MainApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.light,
      navigatorKey: navigationService.navigatorKey,
      
      // Home staje się dynamiczny
      home: Consumer<UserViewModel>(
        builder: (context, userVM, child) {
          
          // 1. Ekran ładowania (podczas sprawdzania SecureStorage przy starcie)
          if (userVM.isLoading) {
            return const Scaffold(
              body: Center(child: CircularProgressIndicator()),
            );
          }

          // 2. Jeśli zalogowany - pokazujemy główny layout
          if (userVM.isLoggedIn) {
            return const GeneralLayout();
          }

          // 3. Jeśli niezalogowany - pokazujemy ekran logowania
          return const LoginScreen();
        },
      ),
    );
  }
    
  }

  /*


T0D0:


5. Co z tymi pluginami? Gdzie biblioteka pluginów? ( Debug pokazuje że przychodzi pusta lista, investigate)
6. Uruchamianie pluginów/logi itd w tle
7. ekran logowanie na potem
8. Czy notifications z zamockowanymi alertami działają dobrze?
9. Jak dodać takie z updatem, żeby robiły się w tle?
10. FCM - co jest potrzebne na backendzie żeby wysyłać notifications?
???. Ogarnięcie logowania do backendu
??? Zmiana mapowania kolorów
??? Mam dwa sposoby na handling enumów ze stringów - wybrać jeden, żeby kod był spójny.

App Theme:
- odpicowanie elementów stałych, żeby nie wyglądały tak szkolnie
- uporządkowanie obramowań containerów/usunięcie jak będzie lepiej

  */