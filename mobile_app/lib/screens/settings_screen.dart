import 'package:alert_app/logic/settings_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final themeViewModel = context.watch<SettingsViewModel>();
    final isDark = themeViewModel.isDarkMode;

    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        children: [
          Row(
            children: [
              const Expanded(
                child: Text(
                  'Dark Theme',
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.w500),
                ),
              ),
              Expanded(
                child: Align(
                  alignment: Alignment.centerRight,
                  child: IconButton(
                    iconSize: 40,
                    icon: Icon(
                      isDark ? Icons.light_mode : Icons.dark_mode,
                      color: isDark ? Colors.amber : Colors.blueGrey,
                    ),
                    onPressed: () {
                      themeViewModel.toggleTheme();
                    },
                  ),
                ),
              ),
            ],
          ),
          Container(
            height: 2.0,
            width: double.infinity,
            color: isDark ? Colors.grey[800] : Colors.black,
            margin: const EdgeInsets.symmetric(vertical: 16.0),
          ),
        ],
      ),
    );
  }
}
