// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get appTitle => 'Alert System';

  @override
  String get settings_language_label => 'Language';

  @override
  String get settings_lang_pl => 'Polish';

  @override
  String get settings_lang_en => 'English';

  @override
  String get yes => 'Yes';

  @override
  String get no => 'No';

  @override
  String get sectionHomeScreen => '--- HOME SCREEN ------------------------------------------------------------------------------------------------------';

  @override
  String get statLastPing => 'Last Ping';

  @override
  String get statActiveAlerts => 'Active Alerts';

  @override
  String get latestCriticalSection => 'Latest Critical Incident';

  @override
  String get alertStatusAcknowledged => 'ACKNOWLEDGED';

  @override
  String get alertStatusNotAcknowledged => 'NOT ACKNOWLEDGED';

  @override
  String get noCriticalAlerts => 'No critical alerts';

  @override
  String get quickNavigationSection => 'Quick Navigation';

  @override
  String get navAllAlerts => 'ALL ALERTS';

  @override
  String get navPlugins => 'PLUGINS';

  @override
  String get sectionLoginScreen => '--- HOME SCREEN -----------------------------------------------------------------------------------------------------';

  @override
  String get login_error_message => 'Loggin error. Check connection and try again';

  @override
  String get login_app_title => 'ALERT APP';

  @override
  String get login_field_email => 'E-mail';

  @override
  String get login_field_password => 'Password';

  @override
  String get login_button_submit => 'LOG IN';

  @override
  String get sectionPluginsScreen => '--- PLUGINS SCREEN ------------------------------------------------------------------------------------------------';

  @override
  String get plugins_no_plugins_found => 'No plugins found. Consider checking the button in Debug Screen!';

  @override
  String get plugins_sort_by => 'Sort by - ';

  @override
  String get plugins_sort_filename => 'File Name';

  @override
  String get plugins_sort_creator => 'Creator';

  @override
  String get plugins_sort_language => 'Language';

  @override
  String get plugins_tile_no_name => 'No name';

  @override
  String get plugins_tile_unknown_creator => 'Unknown creator';

  @override
  String get plugins_tile_unknown_status => 'Unknown status';

  @override
  String get plugins_tile_unknown_time => 'Unknown time';

  @override
  String get plugins_tile_unknown_severity => 'Unknown severity';

  @override
  String get plugins_button_edit => 'Edit plugin';

  @override
  String get plugins_button_run => 'Run plugin';

  @override
  String get sectionPluginsCronDialog => '--- CRON DIALOG ---';

  @override
  String plugins_cron_title(Object fileName) {
    return 'Plugin schedule: $fileName';
  }

  @override
  String get plugins_cron_field_cron => 'CRON expression';

  @override
  String get plugins_cron_field_args => 'Default Arguments (Optional)';

  @override
  String get plugins_cron_switch_active => 'Active?';

  @override
  String get plugins_cron_button_cancel => 'Cancel';

  @override
  String get plugins_cron_button_save => 'Save';

  @override
  String get plugins_cron_toast_success => 'Saved';

  @override
  String get plugins_cron_toast_error => 'Error';

  @override
  String get sectionPluginsArgsDialog => '--- ARGS DIALOG ---';

  @override
  String plugins_args_title(Object fileName) {
    return 'Run plugin: $fileName';
  }

  @override
  String get plugins_args_field_args => 'Arguments (Optional)';

  @override
  String get plugins_args_button_cancel => 'Cancel';

  @override
  String get plugins_args_button_run => 'Run';

  @override
  String plugins_args_toast_triggered(Object fileName) {
    return 'Plugin $fileName triggered';
  }

  @override
  String get sectionAlertsScreen => '--- ALERTS SCREEN ----------------------------------------------------------------------------------------------------';

  @override
  String get alerts_no_alerts_found => 'No alerts found. Consider checking the button in Debug Screen!';

  @override
  String get alerts_sort_by => 'Sort by - ';

  @override
  String get alerts_sort_id => 'ID';

  @override
  String get alerts_sort_title => 'Title';

  @override
  String get alerts_sort_created_at => 'CreatedAt';

  @override
  String get alerts_sort_closed_at => 'ClosedAt';

  @override
  String get alerts_sort_acknowledged => 'Acknowledged';

  @override
  String get alerts_sort_severity => 'Severity';

  @override
  String get alerts_tile_no_title => 'No Title';

  @override
  String get alerts_status_acknowledged => 'ACKNOWLEDGED';

  @override
  String get alerts_status_not_acknowledged => 'NOT ACKNOWLEDGED';

  @override
  String get alerts_tile_unknown_host => 'Unknown Host';

  @override
  String get alerts_tile_unknown_time => 'Unknown Time';

  @override
  String alerts_tile_severity_prefix(Object severityLabel) {
    return 'Severity: $severityLabel';
  }

  @override
  String get alerts_button_actions => 'Actions';

  @override
  String get alerts_history_empty => 'No alert history';

  @override
  String get alerts_history_no_comment => 'No comment';

  @override
  String alerts_history_subtitle(Object ackStatus, Object author) {
    return 'Author: $author | ACK Status: $ackStatus';
  }

  @override
  String get sectionAlertsAckDialog => '--- ACK DIALOG ---';

  @override
  String alerts_dialog_title(Object alertId) {
    return 'Actions for Alert $alertId';
  }

  @override
  String get alerts_dialog_field_comment => 'Your comment';

  @override
  String get alerts_dialog_section_severity => 'Change Severity:';

  @override
  String get alerts_dialog_severity_unknown => 'Unknown';

  @override
  String get alerts_dialog_severity_info => 'Info';

  @override
  String get alerts_dialog_severity_low => 'Low';

  @override
  String get alerts_dialog_severity_medium => 'Medium';

  @override
  String get alerts_dialog_severity_high => 'High';

  @override
  String get alerts_dialog_severity_critical => 'Critical';

  @override
  String get alerts_dialog_check_ack => 'Acknowledge alert';

  @override
  String get alerts_dialog_button_cancel => 'Cancel';

  @override
  String get alerts_dialog_button_update => 'Update';

  @override
  String get sectionUserScreen => '--- USER SCREEN ---------------------------------------------------------------------------------------------------------------------';

  @override
  String get user_loading_data => 'Loading user data...';

  @override
  String get user_label_username => 'Username';

  @override
  String get user_label_email => 'E-mail';

  @override
  String get user_label_role => 'Role';

  @override
  String get user_label_group => 'Group';

  @override
  String get user_button_logout => 'Logout';

  @override
  String get sectionGeneralLayout => '--- GENERAL LAYOUT ------------------------------------------------------------------------------------------------------------------';

  @override
  String get layout_menu_header => 'ALERT MENU';

  @override
  String get layout_menu_home => 'Home';

  @override
  String get layout_menu_alerts => 'Alerts';

  @override
  String get layout_menu_plugins => 'Plugins';

  @override
  String get layout_menu_settings => 'Settings';

  @override
  String get layout_menu_user => 'User';

  @override
  String get layout_menu_debug => 'Debug';

  @override
  String get sectionAlarmOverlay => '--- ALARM OVERLAY ---------------------------------------------------------------------------------------------------------------------';

  @override
  String get overlay_extreme_alert => 'Extreme alert!';

  @override
  String get overlay_button_stop => 'Stop';

  @override
  String get settings_switch_to_polish => 'Switch to Polish';

  @override
  String get navigation_history => 'History';

  @override
  String get navigation_alerts => 'Alerts';

  @override
  String get navigation_plugins => 'Plugins';

  @override
  String get navigation_settings => 'Settings';

  @override
  String get navigation_user_profile => 'User Profile';

  @override
  String get navigation_home => 'Home';

  @override
  String get app_title => 'AlertVIP';

  @override
  String get theme => 'Theme';
}
