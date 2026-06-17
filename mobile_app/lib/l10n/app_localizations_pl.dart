// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for Polish (`pl`).
class AppLocalizationsPl extends AppLocalizations {
  AppLocalizationsPl([String locale = 'pl']) : super(locale);

  @override
  String get appTitle => 'Alert App';

  @override
  String get settings_language_label => 'Język';

  @override
  String get settings_lang_pl => 'Polski';

  @override
  String get settings_lang_en => 'Angielski';

  @override
  String get yes => 'Tak';

  @override
  String get no => 'Nie';

  @override
  String get sectionHomeScreen => '--- HOME SCREEN ------------------------------------------------------------------------------------------------------';

  @override
  String get statLastPing => 'Ostatni Ping';

  @override
  String get statActiveAlerts => 'Aktywne Alerty';

  @override
  String get latestCriticalSection => 'Najnowsze Incydenty Krytyczne';

  @override
  String get alertStatusAcknowledged => 'ZAAKCEPTOWANY';

  @override
  String get alertStatusNotAcknowledged => 'NIEZAAKCEPTOWANY';

  @override
  String get noCriticalAlerts => 'Brak krytycznych alertów';

  @override
  String get quickNavigationSection => 'Szybka Nawigacja';

  @override
  String get navAllAlerts => 'WSZYSTKIE ALERTY';

  @override
  String get navPlugins => 'WTYCZKI';

  @override
  String get sectionLoginScreen => '--- HOME SCREEN ------------------------------------------------------------------------------------------------';

  @override
  String get login_error_message => 'Błąd logowania. Sprawdź połączenie i spróbuj ponownie';

  @override
  String get login_app_title => 'ALERT APP';

  @override
  String get login_field_email => 'E-mail';

  @override
  String get login_field_password => 'Hasło';

  @override
  String get login_button_submit => 'ZALOGUJ SIĘ';

  @override
  String get sectionPluginsScreen => '--- PLUGINS SCREEN ------------------------------------------------------------------------------------------------';

  @override
  String get plugins_no_plugins_found => 'Nie znaleziono wtyczek. Rozważ sprawdzenie przycisku na ekranie debugowania!';

  @override
  String get plugins_sort_by => 'Sortuj po - ';

  @override
  String get plugins_sort_filename => 'Nazwa pliku';

  @override
  String get plugins_sort_creator => 'Twórca';

  @override
  String get plugins_sort_language => 'Język';

  @override
  String get plugins_tile_no_name => 'Brak nazwy';

  @override
  String get plugins_tile_unknown_creator => 'Nieznany twórca';

  @override
  String get plugins_tile_unknown_status => 'Nieznany status';

  @override
  String get plugins_tile_unknown_time => 'Unknown time';

  @override
  String get plugins_tile_unknown_severity => 'Nieznana ważność';

  @override
  String get plugins_button_edit => 'Edytuj wtyczkę';

  @override
  String get plugins_button_run => 'Uruchom wtyczkę';

  @override
  String get sectionPluginsCronDialog => '--- CRON DIALOG ---';

  @override
  String plugins_cron_title(Object fileName) {
    return 'Harmonogram wtyczki: $fileName';
  }

  @override
  String get plugins_cron_field_cron => 'Wyrażenie CRON';

  @override
  String get plugins_cron_field_args => 'Domyślne argumenty (Opcjonalnie)';

  @override
  String get plugins_cron_switch_active => 'Aktywna?';

  @override
  String get plugins_cron_button_cancel => 'Anuluj';

  @override
  String get plugins_cron_button_save => 'Zapisz';

  @override
  String get plugins_cron_toast_success => 'Zapisano';

  @override
  String get plugins_cron_toast_error => 'Błąd';

  @override
  String get sectionPluginsArgsDialog => '--- ARGS DIALOG ---';

  @override
  String plugins_args_title(Object fileName) {
    return 'Uruchom wtyczkę: $fileName';
  }

  @override
  String get plugins_args_field_args => 'Argumenty (Opcjonalnie)';

  @override
  String get plugins_args_button_cancel => 'Anuluj';

  @override
  String get plugins_args_button_run => 'Uruchom';

  @override
  String plugins_args_toast_triggered(Object fileName) {
    return 'Wtyczka $fileName została uruchomiona';
  }

  @override
  String get sectionAlertsScreen => '--- ALERTS SCREEN ---';

  @override
  String get alerts_no_alerts_found => 'Nie znaleziono alertów. Rozważ sprawdzenie przycisku na ekranie debugowania!';

  @override
  String get alerts_sort_by => 'Sortuj według - ';

  @override
  String get alerts_sort_id => 'ID';

  @override
  String get alerts_sort_title => 'Tytuł';

  @override
  String get alerts_sort_created_at => 'Data utworzenia';

  @override
  String get alerts_sort_closed_at => 'Data zamknięcia';

  @override
  String get alerts_sort_acknowledged => 'Status akceptacji';

  @override
  String get alerts_sort_severity => 'Ważność';

  @override
  String get alerts_tile_no_title => 'Brak tytułu';

  @override
  String get alerts_status_acknowledged => 'ZAAKCEPTOWANY';

  @override
  String get alerts_status_not_acknowledged => 'NIEZAAKCEPTOWANY';

  @override
  String get alerts_tile_unknown_host => 'Nieznany host';

  @override
  String get alerts_tile_unknown_time => 'Nieznany czas';

  @override
  String alerts_tile_severity_prefix(Object severityLabel) {
    return 'Ważność: $severityLabel';
  }

  @override
  String get alerts_button_actions => 'Akcje';

  @override
  String get alerts_history_empty => 'Brak historii alertu';

  @override
  String get alerts_history_no_comment => 'Brak komentarza';

  @override
  String alerts_history_subtitle(Object ackStatus, Object author) {
    return 'Autor: $author | Status ACK: $ackStatus';
  }

  @override
  String get sectionAlertsAckDialog => '--- ACK DIALOG ---';

  @override
  String alerts_dialog_title(Object alertId) {
    return 'Akcje dla alertu $alertId';
  }

  @override
  String get alerts_dialog_field_comment => 'Twój komentarz';

  @override
  String get alerts_dialog_section_severity => 'Zmień ważność:';

  @override
  String get alerts_dialog_severity_unknown => 'Unknown';

  @override
  String get alerts_dialog_severity_info => 'Info';

  @override
  String get alerts_dialog_severity_low => 'Niski';

  @override
  String get alerts_dialog_severity_medium => 'Średni';

  @override
  String get alerts_dialog_severity_high => 'Wysoki';

  @override
  String get alerts_dialog_severity_critical => 'Critical';

  @override
  String get alerts_dialog_check_ack => 'Zaakceptuj alert';

  @override
  String get alerts_dialog_button_cancel => 'Anuluj';

  @override
  String get alerts_dialog_button_update => 'Aktualizuj';

  @override
  String get sectionUserScreen => '--- USER SCREEN --------------------------------------------------------------------------------------------------------------------------------';

  @override
  String get user_loading_data => 'Ładowanie danych użytkownika...';

  @override
  String get user_label_username => 'Nazwa użytkownika';

  @override
  String get user_label_email => 'E-mail';

  @override
  String get user_label_role => 'Rola';

  @override
  String get user_label_group => 'Grupa';

  @override
  String get user_button_logout => 'Wyloguj się';

  @override
  String get sectionGeneralLayout => '--- GENERAL LAYOUT ---------------------------------------------------------------------------------------------------------------------';

  @override
  String get layout_menu_header => 'ALERT MENU';

  @override
  String get layout_menu_home => 'Główna';

  @override
  String get layout_menu_alerts => 'Alerty';

  @override
  String get layout_menu_plugins => 'Wtyczki';

  @override
  String get layout_menu_settings => 'Ustawienia';

  @override
  String get layout_menu_user => 'Użytkownik';

  @override
  String get layout_menu_debug => 'Debugowanie';

  @override
  String get sectionAlarmOverlay => '--- ALARM OVERLAY -------------------------------------------------------------------------------------------------------------------------';

  @override
  String get overlay_extreme_alert => 'Alert krytyczny!';

  @override
  String get overlay_button_stop => 'Zatrzymaj';

  @override
  String get settings_switch_to_polish => 'Switch to Polish';

  @override
  String get navigation_history => 'Historia';

  @override
  String get navigation_alerts => 'Alerty';

  @override
  String get navigation_plugins => 'Pluginy';

  @override
  String get navigation_settings => 'Ustawienia';

  @override
  String get navigation_user_profile => 'Profil użytkownika';

  @override
  String get navigation_home => 'Ekran główny';

  @override
  String get app_title => 'AlertVIP';

  @override
  String get theme => 'Motyw';
}
