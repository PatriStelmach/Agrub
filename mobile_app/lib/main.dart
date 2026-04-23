import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:alert_app/firebase_options.dart';
import 'package:alert_app/services/navigation_service.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:alert_app/screens/general_layout_screen.dart';
import 'package:alert_app/themes/app_theme_default.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

Future<void> main() async {

//checking if flutter engine is ready
  WidgetsFlutterBinding.ensureInitialized();

  // To inicjalizuje Firebase
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );


  runApp(
    MultiProvider(
      providers: [
        //Single source of truth
        ChangeNotifierProvider(create: (_) => AlertRepository()),
        ChangeNotifierProvider(create: (_) => PluginsRepository()),
        ChangeNotifierProvider(create: (_) => UserRepository()),
        
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
      home: GeneralLayout());
    
    }
    
  }

  /*


T0D0:

ASAP
2. Mockup pushowania alertów pod FCM ( underway)
3. Logika alarmu przy nowym extreme
4. Mockup skryptów



App Theme:
- odpicowanie elementów stałych, żeby nie wyglądały tak szkolnie
- uporządkowanie obramowań containerów/usunięcie jak będzie lepiej


Login screen/authorization screen?
- Layout
- przygotowanie logiki pod autoryzację z serwerem



  */