import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

///Central theme styling file
class AppTheme {
  static const Color _lightSeedColor = Color.fromARGB(255, 119, 111, 111);
  static const Color _darkSeedColor = Color(0xFF101828);

  ///Setting style for light theme
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      colorScheme: ColorScheme.fromSeed(
        seedColor: _lightSeedColor,
        brightness: Brightness.light,
      ),

      scaffoldBackgroundColor: const Color(0xFFFFFFFF),
      textTheme: GoogleFonts.jetBrainsMonoTextTheme(
        ThemeData.light().textTheme.copyWith(
          titleLarge: const TextStyle(
            color: Colors.black87,
            fontWeight: FontWeight.bold,
          ),
          bodyLarge: const TextStyle(color: Colors.black),
        ),
      ),

      appBarTheme: const AppBarTheme(
        backgroundColor: Color(0xFFf3f4f6),
        foregroundColor: Colors.black87,
        elevation: null,
        centerTitle: null,
        titleTextStyle: TextStyle(color: Colors.black),
      ),

      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: null,
          foregroundColor: null,
          elevation: null,
          padding: const EdgeInsets.all(0),
          shape: const RoundedRectangleBorder(),
        ),
      ),

      dropdownMenuTheme: DropdownMenuThemeData(
        textStyle: const TextStyle(),
        inputDecorationTheme: const InputDecorationTheme(
          filled: null,
          fillColor: null,
          border: OutlineInputBorder(),
        ),
        menuStyle: MenuStyle(
          backgroundColor: WidgetStateProperty.all(null),
          elevation: WidgetStateProperty.all(null),
          shape: WidgetStateProperty.all(const RoundedRectangleBorder()),
        ),
      ),
    );
  }

  ///Setting style for dark theme
  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      brightness: Brightness.dark,
      colorScheme: ColorScheme.fromSeed(
        seedColor: _darkSeedColor,
        brightness: Brightness.dark,
      ),

      scaffoldBackgroundColor: const Color(0xFF121212),

      textTheme: GoogleFonts.jetBrainsMonoTextTheme(
        ThemeData.dark().textTheme.copyWith(
          titleLarge: const TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
          bodyLarge: const TextStyle(color: Colors.white70),
        ),
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: null,
        foregroundColor: null,
        elevation: null,
      ),

      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: null,
          foregroundColor: null,
          shape: const RoundedRectangleBorder(),
        ),
      ),

      dropdownMenuTheme: DropdownMenuThemeData(
        menuStyle: MenuStyle(backgroundColor: WidgetStateProperty.all(null)),
      ),
    );
  }
}
