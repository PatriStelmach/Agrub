import 'package:flutter/material.dart';


class AppTheme {

  static const Color _lightSeedColor = Color.fromARGB(255, 119, 111, 111);
  static const Color _darkSeedColor = Color(0xFF101828);


  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      colorScheme: ColorScheme.fromSeed(
        seedColor: _lightSeedColor,
        brightness: Brightness.light,
       
      ),
      
      // Tło ogólne aplikacji
      scaffoldBackgroundColor: const Color(0xFFFFFFFF),

      // App Bar
      appBarTheme: const AppBarTheme(
        backgroundColor: Color(0xFFf3f4f6),
        foregroundColor: null,
        elevation: null,
        centerTitle: null,
        titleTextStyle: TextStyle(), // Tu styl tekstu tytułu
      ),

      // Elevated Button
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: null,
          foregroundColor: null,
          elevation: null,
          padding: const EdgeInsets.all(0),
          shape: const RoundedRectangleBorder(), // Tu zaokrąglenia
        ),
      ),

      // Dropdown List (Material 3 używa DropdownMenuTheme)
      dropdownMenuTheme: DropdownMenuThemeData(
        textStyle: const TextStyle(),
        inputDecorationTheme: const InputDecorationTheme(
          filled: null,
          fillColor: null,
          border: OutlineInputBorder(), // Definicja ramki
        ),
        menuStyle: MenuStyle(
          backgroundColor: WidgetStateProperty.all(null),
          elevation: WidgetStateProperty.all(null),
          shape: WidgetStateProperty.all(const RoundedRectangleBorder()),
        ),
      ),
    );
  }

 
  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      colorScheme: ColorScheme.fromSeed(
        seedColor: _darkSeedColor,
        brightness: Brightness.dark,
        // primary: ,
        // surface: ,
      ),

      
      scaffoldBackgroundColor: const Color(0xFF121212),

      // App Bar
      appBarTheme: const AppBarTheme(
        backgroundColor: null,
        foregroundColor: null,
        elevation: null,
      ),

      // Elevated Button
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: null,
          foregroundColor: null,
          shape: const RoundedRectangleBorder(),
        ),
      ),

      // Dropdown List
      dropdownMenuTheme: DropdownMenuThemeData(
        menuStyle: MenuStyle(
          backgroundColor: WidgetStateProperty.all(null),
        ),
      ),
    );
  }
}