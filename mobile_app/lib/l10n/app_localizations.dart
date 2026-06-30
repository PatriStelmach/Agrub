import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_en.dart';
import 'app_localizations_pl.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'l10n/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale) : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations? of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations);
  }

  static const LocalizationsDelegate<AppLocalizations> delegate = _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates = <LocalizationsDelegate<dynamic>>[
    delegate,
    GlobalMaterialLocalizations.delegate,
    GlobalCupertinoLocalizations.delegate,
    GlobalWidgetsLocalizations.delegate,
  ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('en'),
    Locale('pl')
  ];

  /// No description provided for @appTitle.
  ///
  /// In en, this message translates to:
  /// **'Agrub System'**
  String get appTitle;

  /// No description provided for @settings_language_label.
  ///
  /// In en, this message translates to:
  /// **'Language'**
  String get settings_language_label;

  /// No description provided for @settings_lang_pl.
  ///
  /// In en, this message translates to:
  /// **'Polish'**
  String get settings_lang_pl;

  /// No description provided for @settings_lang_en.
  ///
  /// In en, this message translates to:
  /// **'English'**
  String get settings_lang_en;

  /// No description provided for @yes.
  ///
  /// In en, this message translates to:
  /// **'Yes'**
  String get yes;

  /// No description provided for @no.
  ///
  /// In en, this message translates to:
  /// **'No'**
  String get no;

  /// No description provided for @sectionHomeScreen.
  ///
  /// In en, this message translates to:
  /// **'--- HOME SCREEN ------------------------------------------------------------------------------------------------------'**
  String get sectionHomeScreen;

  /// No description provided for @statLastPing.
  ///
  /// In en, this message translates to:
  /// **'Last Ping'**
  String get statLastPing;

  /// No description provided for @statActiveAlerts.
  ///
  /// In en, this message translates to:
  /// **'Active Alerts'**
  String get statActiveAlerts;

  /// No description provided for @latestCriticalSection.
  ///
  /// In en, this message translates to:
  /// **'Latest Critical Incident'**
  String get latestCriticalSection;

  /// No description provided for @alertStatusAcknowledged.
  ///
  /// In en, this message translates to:
  /// **'ACKNOWLEDGED'**
  String get alertStatusAcknowledged;

  /// No description provided for @alertStatusNotAcknowledged.
  ///
  /// In en, this message translates to:
  /// **'NOT ACKNOWLEDGED'**
  String get alertStatusNotAcknowledged;

  /// No description provided for @noCriticalAlerts.
  ///
  /// In en, this message translates to:
  /// **'No critical alerts'**
  String get noCriticalAlerts;

  /// No description provided for @quickNavigationSection.
  ///
  /// In en, this message translates to:
  /// **'Quick Navigation'**
  String get quickNavigationSection;

  /// No description provided for @navAllAlerts.
  ///
  /// In en, this message translates to:
  /// **'ALL ALERTS'**
  String get navAllAlerts;

  /// No description provided for @navPlugins.
  ///
  /// In en, this message translates to:
  /// **'PLUGINS'**
  String get navPlugins;

  /// No description provided for @sectionLoginScreen.
  ///
  /// In en, this message translates to:
  /// **'--- HOME SCREEN -----------------------------------------------------------------------------------------------------'**
  String get sectionLoginScreen;

  /// No description provided for @login_error_message.
  ///
  /// In en, this message translates to:
  /// **'Loggin error. Check connection and try again'**
  String get login_error_message;

  /// No description provided for @login_app_title.
  ///
  /// In en, this message translates to:
  /// **'AGRUB APP'**
  String get login_app_title;

  /// No description provided for @login_field_server.
  ///
  /// In en, this message translates to:
  /// **'Server'**
  String get login_field_server;

  /// No description provided for @login_field_email.
  ///
  /// In en, this message translates to:
  /// **'E-mail'**
  String get login_field_email;

  /// No description provided for @login_field_password.
  ///
  /// In en, this message translates to:
  /// **'Password'**
  String get login_field_password;

  /// No description provided for @login_button_submit.
  ///
  /// In en, this message translates to:
  /// **'LOG IN'**
  String get login_button_submit;

  /// No description provided for @ping_no_connection.
  ///
  /// In en, this message translates to:
  /// **'No connection'**
  String get ping_no_connection;

  /// No description provided for @ping_last_ping.
  ///
  /// In en, this message translates to:
  /// **'Last ping:'**
  String get ping_last_ping;

  /// No description provided for @sectionPluginsScreen.
  ///
  /// In en, this message translates to:
  /// **'--- PLUGINS SCREEN ------------------------------------------------------------------------------------------------'**
  String get sectionPluginsScreen;

  /// No description provided for @plugins_no_plugins_found.
  ///
  /// In en, this message translates to:
  /// **'No plugins found.'**
  String get plugins_no_plugins_found;

  /// No description provided for @plugins_sort_by.
  ///
  /// In en, this message translates to:
  /// **'Sort by - '**
  String get plugins_sort_by;

  /// No description provided for @plugins_sort_filename.
  ///
  /// In en, this message translates to:
  /// **'File Name'**
  String get plugins_sort_filename;

  /// No description provided for @plugins_sort_creator.
  ///
  /// In en, this message translates to:
  /// **'Creator'**
  String get plugins_sort_creator;

  /// No description provided for @plugins_sort_language.
  ///
  /// In en, this message translates to:
  /// **'Language'**
  String get plugins_sort_language;

  /// No description provided for @plugins_tile_no_name.
  ///
  /// In en, this message translates to:
  /// **'No name'**
  String get plugins_tile_no_name;

  /// No description provided for @plugins_tile_unknown_creator.
  ///
  /// In en, this message translates to:
  /// **'Unknown creator'**
  String get plugins_tile_unknown_creator;

  /// No description provided for @plugins_tile_unknown_status.
  ///
  /// In en, this message translates to:
  /// **'Unknown status'**
  String get plugins_tile_unknown_status;

  /// No description provided for @plugins_tile_unknown_time.
  ///
  /// In en, this message translates to:
  /// **'Unknown time'**
  String get plugins_tile_unknown_time;

  /// No description provided for @plugins_tile_unknown_severity.
  ///
  /// In en, this message translates to:
  /// **'Unknown severity'**
  String get plugins_tile_unknown_severity;

  /// No description provided for @plugins_button_edit.
  ///
  /// In en, this message translates to:
  /// **'Edit plugin'**
  String get plugins_button_edit;

  /// No description provided for @plugins_button_run.
  ///
  /// In en, this message translates to:
  /// **'Run plugin'**
  String get plugins_button_run;

  /// No description provided for @sectionPluginsCronDialog.
  ///
  /// In en, this message translates to:
  /// **'--- CRON DIALOG ---'**
  String get sectionPluginsCronDialog;

  /// No description provided for @plugins_cron_title.
  ///
  /// In en, this message translates to:
  /// **'Plugin schedule: {fileName}'**
  String plugins_cron_title(Object fileName);

  /// No description provided for @plugins_cron_field_cron.
  ///
  /// In en, this message translates to:
  /// **'CRON expression'**
  String get plugins_cron_field_cron;

  /// No description provided for @plugins_cron_field_args.
  ///
  /// In en, this message translates to:
  /// **'Default Arguments (Optional)'**
  String get plugins_cron_field_args;

  /// No description provided for @plugins_cron_switch_active.
  ///
  /// In en, this message translates to:
  /// **'Active?'**
  String get plugins_cron_switch_active;

  /// No description provided for @plugins_cron_button_cancel.
  ///
  /// In en, this message translates to:
  /// **'Cancel'**
  String get plugins_cron_button_cancel;

  /// No description provided for @plugins_cron_button_save.
  ///
  /// In en, this message translates to:
  /// **'Save'**
  String get plugins_cron_button_save;

  /// No description provided for @plugins_cron_toast_success.
  ///
  /// In en, this message translates to:
  /// **'Saved'**
  String get plugins_cron_toast_success;

  /// No description provided for @plugins_cron_toast_error.
  ///
  /// In en, this message translates to:
  /// **'Error'**
  String get plugins_cron_toast_error;

  /// No description provided for @sectionPluginsArgsDialog.
  ///
  /// In en, this message translates to:
  /// **'--- ARGS DIALOG ---'**
  String get sectionPluginsArgsDialog;

  /// No description provided for @plugins_args_title.
  ///
  /// In en, this message translates to:
  /// **'Run plugin: {fileName}'**
  String plugins_args_title(Object fileName);

  /// No description provided for @plugins_args_field_args.
  ///
  /// In en, this message translates to:
  /// **'Arguments (Optional)'**
  String get plugins_args_field_args;

  /// No description provided for @plugins_args_button_cancel.
  ///
  /// In en, this message translates to:
  /// **'Cancel'**
  String get plugins_args_button_cancel;

  /// No description provided for @plugins_args_button_run.
  ///
  /// In en, this message translates to:
  /// **'Run'**
  String get plugins_args_button_run;

  /// No description provided for @plugins_args_toast_triggered.
  ///
  /// In en, this message translates to:
  /// **'Plugin {fileName} triggered'**
  String plugins_args_toast_triggered(Object fileName);

  /// No description provided for @sectionAlertsScreen.
  ///
  /// In en, this message translates to:
  /// **'--- ALERTS SCREEN ----------------------------------------------------------------------------------------------------'**
  String get sectionAlertsScreen;

  /// No description provided for @alerts_no_alerts_found.
  ///
  /// In en, this message translates to:
  /// **'No alerts found. Consider checking the button in Debug Screen!'**
  String get alerts_no_alerts_found;

  /// No description provided for @alerts_sort_by.
  ///
  /// In en, this message translates to:
  /// **'Sort by - '**
  String get alerts_sort_by;

  /// No description provided for @alerts_sort_id.
  ///
  /// In en, this message translates to:
  /// **'ID'**
  String get alerts_sort_id;

  /// No description provided for @alerts_sort_title.
  ///
  /// In en, this message translates to:
  /// **'Title'**
  String get alerts_sort_title;

  /// No description provided for @alerts_sort_created_at.
  ///
  /// In en, this message translates to:
  /// **'CreatedAt'**
  String get alerts_sort_created_at;

  /// No description provided for @alerts_sort_closed_at.
  ///
  /// In en, this message translates to:
  /// **'ClosedAt'**
  String get alerts_sort_closed_at;

  /// No description provided for @alerts_sort_acknowledged.
  ///
  /// In en, this message translates to:
  /// **'Acknowledged'**
  String get alerts_sort_acknowledged;

  /// No description provided for @alerts_sort_severity.
  ///
  /// In en, this message translates to:
  /// **'Severity'**
  String get alerts_sort_severity;

  /// No description provided for @alerts_tile_no_title.
  ///
  /// In en, this message translates to:
  /// **'No Title'**
  String get alerts_tile_no_title;

  /// No description provided for @alerts_status_acknowledged.
  ///
  /// In en, this message translates to:
  /// **'ACKNOWLEDGED'**
  String get alerts_status_acknowledged;

  /// No description provided for @alerts_status_not_acknowledged.
  ///
  /// In en, this message translates to:
  /// **'NOT ACKNOWLEDGED'**
  String get alerts_status_not_acknowledged;

  /// No description provided for @alerts_tile_unknown_host.
  ///
  /// In en, this message translates to:
  /// **'Unknown Host'**
  String get alerts_tile_unknown_host;

  /// No description provided for @alerts_tile_unknown_time.
  ///
  /// In en, this message translates to:
  /// **'Unknown Time'**
  String get alerts_tile_unknown_time;

  /// No description provided for @alerts_tile_severity_prefix.
  ///
  /// In en, this message translates to:
  /// **'Severity: {severityLabel}'**
  String alerts_tile_severity_prefix(Object severityLabel);

  /// No description provided for @alerts_button_actions.
  ///
  /// In en, this message translates to:
  /// **'Actions'**
  String get alerts_button_actions;

  /// No description provided for @alerts_history_empty.
  ///
  /// In en, this message translates to:
  /// **'No alert history'**
  String get alerts_history_empty;

  /// No description provided for @alerts_history_no_comment.
  ///
  /// In en, this message translates to:
  /// **'No comment'**
  String get alerts_history_no_comment;

  /// No description provided for @alerts_history_subtitle.
  ///
  /// In en, this message translates to:
  /// **'Author: {author} | ACK Status: {ackStatus}'**
  String alerts_history_subtitle(Object ackStatus, Object author);

  /// No description provided for @sectionAlertsAckDialog.
  ///
  /// In en, this message translates to:
  /// **'--- ACK DIALOG ---'**
  String get sectionAlertsAckDialog;

  /// No description provided for @alerts_dialog_title.
  ///
  /// In en, this message translates to:
  /// **'Actions for Alert {alertId}'**
  String alerts_dialog_title(Object alertId);

  /// No description provided for @alerts_dialog_field_comment.
  ///
  /// In en, this message translates to:
  /// **'Your comment'**
  String get alerts_dialog_field_comment;

  /// No description provided for @alerts_dialog_section_severity.
  ///
  /// In en, this message translates to:
  /// **'Change Severity:'**
  String get alerts_dialog_section_severity;

  /// No description provided for @alerts_dialog_severity_unknown.
  ///
  /// In en, this message translates to:
  /// **'Unknown'**
  String get alerts_dialog_severity_unknown;

  /// No description provided for @alerts_dialog_severity_info.
  ///
  /// In en, this message translates to:
  /// **'Info'**
  String get alerts_dialog_severity_info;

  /// No description provided for @alerts_dialog_severity_low.
  ///
  /// In en, this message translates to:
  /// **'Low'**
  String get alerts_dialog_severity_low;

  /// No description provided for @alerts_dialog_severity_medium.
  ///
  /// In en, this message translates to:
  /// **'Medium'**
  String get alerts_dialog_severity_medium;

  /// No description provided for @alerts_dialog_severity_high.
  ///
  /// In en, this message translates to:
  /// **'High'**
  String get alerts_dialog_severity_high;

  /// No description provided for @alerts_dialog_severity_critical.
  ///
  /// In en, this message translates to:
  /// **'Critical'**
  String get alerts_dialog_severity_critical;

  /// No description provided for @alerts_dialog_check_ack.
  ///
  /// In en, this message translates to:
  /// **'Acknowledge alert'**
  String get alerts_dialog_check_ack;

  /// No description provided for @alerts_dialog_button_cancel.
  ///
  /// In en, this message translates to:
  /// **'Cancel'**
  String get alerts_dialog_button_cancel;

  /// No description provided for @alerts_dialog_button_update.
  ///
  /// In en, this message translates to:
  /// **'Update'**
  String get alerts_dialog_button_update;

  /// No description provided for @sectionUserScreen.
  ///
  /// In en, this message translates to:
  /// **'--- USER SCREEN ---------------------------------------------------------------------------------------------------------------------'**
  String get sectionUserScreen;

  /// No description provided for @user_loading_data.
  ///
  /// In en, this message translates to:
  /// **'Loading user data...'**
  String get user_loading_data;

  /// No description provided for @user_name.
  ///
  /// In en, this message translates to:
  /// **'Name'**
  String get user_name;

  /// No description provided for @user_surname.
  ///
  /// In en, this message translates to:
  /// **'Surname'**
  String get user_surname;

  /// No description provided for @user_label_username.
  ///
  /// In en, this message translates to:
  /// **'Username'**
  String get user_label_username;

  /// No description provided for @user_label_email.
  ///
  /// In en, this message translates to:
  /// **'E-mail'**
  String get user_label_email;

  /// No description provided for @user_label_role.
  ///
  /// In en, this message translates to:
  /// **'Role'**
  String get user_label_role;

  /// No description provided for @user_label_group.
  ///
  /// In en, this message translates to:
  /// **'Group'**
  String get user_label_group;

  /// No description provided for @user_button_logout.
  ///
  /// In en, this message translates to:
  /// **'Logout'**
  String get user_button_logout;

  /// No description provided for @sectionGeneralLayout.
  ///
  /// In en, this message translates to:
  /// **'--- GENERAL LAYOUT ------------------------------------------------------------------------------------------------------------------'**
  String get sectionGeneralLayout;

  /// No description provided for @layout_menu_header.
  ///
  /// In en, this message translates to:
  /// **'ALERT MENU'**
  String get layout_menu_header;

  /// No description provided for @layout_menu_home.
  ///
  /// In en, this message translates to:
  /// **'Home'**
  String get layout_menu_home;

  /// No description provided for @layout_menu_alerts.
  ///
  /// In en, this message translates to:
  /// **'Alerts'**
  String get layout_menu_alerts;

  /// No description provided for @layout_menu_plugins.
  ///
  /// In en, this message translates to:
  /// **'Plugins'**
  String get layout_menu_plugins;

  /// No description provided for @layout_menu_settings.
  ///
  /// In en, this message translates to:
  /// **'Settings'**
  String get layout_menu_settings;

  /// No description provided for @layout_menu_user.
  ///
  /// In en, this message translates to:
  /// **'User'**
  String get layout_menu_user;

  /// No description provided for @layout_menu_debug.
  ///
  /// In en, this message translates to:
  /// **'Debug'**
  String get layout_menu_debug;

  /// No description provided for @sectionAlarmOverlay.
  ///
  /// In en, this message translates to:
  /// **'--- ALARM OVERLAY ---------------------------------------------------------------------------------------------------------------------'**
  String get sectionAlarmOverlay;

  /// No description provided for @overlay_extreme_alert.
  ///
  /// In en, this message translates to:
  /// **'Extreme alert!'**
  String get overlay_extreme_alert;

  /// No description provided for @overlay_button_stop.
  ///
  /// In en, this message translates to:
  /// **'Stop'**
  String get overlay_button_stop;

  /// No description provided for @settings_switch_to_polish.
  ///
  /// In en, this message translates to:
  /// **'Switch to Polish'**
  String get settings_switch_to_polish;

  /// No description provided for @navigation_history.
  ///
  /// In en, this message translates to:
  /// **'History'**
  String get navigation_history;

  /// No description provided for @navigation_alerts.
  ///
  /// In en, this message translates to:
  /// **'Alerts'**
  String get navigation_alerts;

  /// No description provided for @navigation_plugins.
  ///
  /// In en, this message translates to:
  /// **'Plugins'**
  String get navigation_plugins;

  /// No description provided for @navigation_settings.
  ///
  /// In en, this message translates to:
  /// **'Settings'**
  String get navigation_settings;

  /// No description provided for @navigation_user_profile.
  ///
  /// In en, this message translates to:
  /// **'User Profile'**
  String get navigation_user_profile;

  /// No description provided for @navigation_home.
  ///
  /// In en, this message translates to:
  /// **'Home'**
  String get navigation_home;

  /// No description provided for @app_title.
  ///
  /// In en, this message translates to:
  /// **'Agrub'**
  String get app_title;

  /// No description provided for @theme.
  ///
  /// In en, this message translates to:
  /// **'Theme'**
  String get theme;
}

class _AppLocalizationsDelegate extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) => <String>['en', 'pl'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {


  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'en': return AppLocalizationsEn();
    case 'pl': return AppLocalizationsPl();
  }

  throw FlutterError(
    'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
    'an issue with the localizations generation tool. Please file an issue '
    'on GitHub with a reproducible sample app and the gen-l10n configuration '
    'that was used.'
  );
}
