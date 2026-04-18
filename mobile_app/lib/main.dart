import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:alert_app/logic/admin_view_model.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:alert_app/screens/general_layout_screen.dart';
import 'package:alert_app/themes/app_theme_default.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        //Single source of truth
        ChangeNotifierProvider(create: (_) => AlertRepository()),
        ChangeNotifierProvider(create: (_) => UserRepository()),
        
        //Creation of view models
        ChangeNotifierProvider<AdminViewModel>(
          create: (context) => AdminViewModel(
            repository: context.read<AlertRepository>(),
          )),

        ChangeNotifierProvider<HomeViewModel>(
          create: (context) => HomeViewModel(
            repository: context.read<AlertRepository>(),
          )),


        ChangeNotifierProvider<AlertsViewModel>(
          create: (context) => AlertsViewModel(
            repository: context.read<AlertRepository>(),
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
      home: GeneralLayout());
    
    }
  }

  /*


T0D0:

App Theme:
- odpicowanie elementów stałych, żeby nie wyglądały tak szkolnie
- uporządkowanie obramowań containerów/usunięcie jak będzie lepiej

Funkcjonalności ogólne:
- przyjmowanie nowego alertu do appki -> jak i ile przechowywać? -> dodać jako test
- ekran alarmu
- alarm kiedy pojawia się alert o najwyższym poziomie, dla debuga podpiąć pod button
- działający alarm dźwiękowy i wibracje na androidzie (mimo wyciszenia), na iOS(bez wyciszenia)
- uporządkować funkcje pobierające info z serwera, przygotowanie pod podpięcie, firebase?

Login screen/authorization screen?
- Layout
- przygotowanie logiki pod autoryzację z serwerem

Alerts Screen:
- filtrowanie po severity itp. 

Propozycja finalnego układu folderów i plików w MVVM, wprwoadzać według potrzeb rozwoju
lib/data/models z modelami
lib data/services lub/oraz repositories w pierwszym HTTP(Rest?) w drugim pobieranie danych?
Jak faktycznie będzie coś tylko liczącego itd. to można zrobić "utility"




  */