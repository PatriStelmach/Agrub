import 'package:alert_app/data/services/language_service.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/settings_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;
    final languageService = context.watch<LanguageService>();
    final currentLanguageCode = languageService.currentLocale.languageCode;
    final themeViewModel = context.watch<SettingsViewModel>();
    final isDark = themeViewModel.isDarkMode;

    final isPolish = currentLanguageCode == 'pl';
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
          Row(
            children: [
              Expanded(
                child: Text(
                  t.settings_language_label,
                  style: const TextStyle(
                    fontSize: 22,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
              Expanded(
                child: Align(
                  alignment: Alignment.centerRight,
                  child: DropdownButton<String>(
                    value: currentLanguageCode,
                    icon: const Icon(Icons.language, color: Colors.blueGrey),
                    elevation: 16,
                    style: TextStyle(
                      fontSize: 18,
                      color: Theme.of(context).textTheme.bodyLarge?.color,
                    ),
                    underline: Container(
                      height: 2,
                      color: Theme.of(context).colorScheme.primary,
                    ),

                    onChanged: (String? newLanguageCode) {
                      if (newLanguageCode != null) {
                        context.read<LanguageService>().changeLanguage(
                          newLanguageCode,
                        );
                      }
                    },

                    items: [
                      DropdownMenuItem<String>(
                        value: 'pl',
                        child: Text(t.settings_lang_pl),
                      ),
                      DropdownMenuItem<String>(
                        value: 'en',
                        child: Text(t.settings_lang_en),
                      ),
                    ],
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
