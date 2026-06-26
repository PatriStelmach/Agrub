import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:alert_app/data/services/auth_service.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/locator.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/general_layout_view_model.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:alert_app/logic/settings_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:alert_app/main.dart';
import 'package:alert_app/screens/home_screen.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:get_it/get_it.dart';
import 'package:integration_test/integration_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';

class MockDio extends Mock implements Dio {}

class MockFlutterSecureStorage extends Mock implements FlutterSecureStorage {}

class MockHomeViewModel extends Mock
    with ChangeNotifier
    implements HomeViewModel {}

class MockUserViewModel extends Mock
    with ChangeNotifier
    implements UserViewModel {}

class MockGeneralLayoutViewModel extends Mock
    with ChangeNotifier
    implements GeneralLayoutViewModel {}

class MockAlertsViewModel extends Mock
    with ChangeNotifier
    implements AlertsViewModel {}

class MockPluginsViewModel extends Mock
    with ChangeNotifier
    implements PluginsViewModel {}

class MockSettingsViewModel extends Mock
    with ChangeNotifier
    implements SettingsViewModel {}

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  late MockDio mockDio;
  late MockUserViewModel mockUserViewModel;
  late MockFlutterSecureStorage mockFlutterSecureStorage;
  late MockGeneralLayoutViewModel mockGeneralLayoutViewModel;
  late MockHomeViewModel mockHomeViewModel;
  late MockAlertsViewModel mockAlertsViewModel;
  late MockPluginsViewModel mockPluginsViewModel;
  late MockSettingsViewModel mockSettingsViewModel;
  setUpAll(() {
    mockDio = MockDio();
    mockUserViewModel = MockUserViewModel();
    mockFlutterSecureStorage = MockFlutterSecureStorage();
    mockGeneralLayoutViewModel = MockGeneralLayoutViewModel();
    mockHomeViewModel = MockHomeViewModel();
    mockAlertsViewModel = MockAlertsViewModel();
    mockPluginsViewModel = MockPluginsViewModel();
    mockSettingsViewModel = MockSettingsViewModel();
    locator.allowReassignment = true;
    GetIt.instance.registerSingleton<FlutterSecureStorage>(
      mockFlutterSecureStorage,
    );
    when(() => mockHomeViewModel.latestCriticalAlerts()).thenReturn([]);
    when(() => mockAlertsViewModel.alertsList).thenReturn(<Alert>[]);
    when(() => mockHomeViewModel.activeAlertsCount).thenReturn(0);
    when(
      () => mockHomeViewModel.triggerPingAndNotify(),
    ).thenAnswer((_) async {});
    when(() => mockAlertsViewModel.isLoading).thenReturn(false);
    when(
      () => mockAlertsViewModel.fetchInitialAlerts(),
    ).thenAnswer((_) async {});
    when(() => mockAlertsViewModel.alertsList).thenReturn([]);

    when(() => mockPluginsViewModel.isLoading).thenReturn(false);
    when(() => mockPluginsViewModel.fetchPlugins()).thenAnswer((_) async {});
    when(() => mockPluginsViewModel.pluginsList).thenReturn([]);
    when(
      () => mockSettingsViewModel.currentLocale,
    ).thenReturn(const Locale('pl'));
    when(() => mockSettingsViewModel.themeMode).thenReturn(ThemeMode.dark);
    locator.registerSingleton<Dio>(mockDio);
    locator.registerSingleton<UserViewModel>(mockUserViewModel);
  });

  group('end-to-end ping test', () {
    testWidgets('ping is succesfull', (tester) async {
      when(
        () => mockGeneralLayoutViewModel.activeScreen,
      ).thenReturn(AppScreen.home);
      when(
        () => mockGeneralLayoutViewModel.currentIndex,
      ).thenReturn(mockGeneralLayoutViewModel.activeScreen.index);
      when(
        () => mockGeneralLayoutViewModel.startBackgroundServices(),
      ).thenAnswer((_) async {});
      when(() => mockUserViewModel.isLoggedIn).thenReturn(true);
      when(() => mockUserViewModel.isLoading).thenReturn(false);
      when(
        () => mockUserViewModel.checkAuthorizationStatus(),
      ).thenAnswer((_) async => true);
      when(() => mockDio.get('/api/alerts/active')).thenAnswer(
        (_) async => Response(
          requestOptions: RequestOptions(path: '/api/alerts/active'),
          data: {
            "id": 1,
            "subject": "test alert",
            "severity": 2,
            "status": "sent",
          },
          statusCode: 200,
        ),
      );
      await tester.pumpWidget(
        MultiProvider(
          providers: [
            ChangeNotifierProvider<UserViewModel>.value(
              value: mockUserViewModel,
            ),
            ChangeNotifierProvider<GeneralLayoutViewModel>.value(
              value: mockGeneralLayoutViewModel,
            ),
            ChangeNotifierProvider<HomeViewModel>.value(
              value: mockHomeViewModel,
            ),
            ChangeNotifierProvider<AlertsViewModel>.value(
              value: mockAlertsViewModel,
            ),
            ChangeNotifierProvider<PluginsViewModel>.value(
              value: mockPluginsViewModel,
            ),
            ChangeNotifierProvider<SettingsViewModel>.value(
              value: mockSettingsViewModel,
            ),
          ],
          child: const MaterialApp(
            localizationsDelegates: AppLocalizations.localizationsDelegates,
            supportedLocales: AppLocalizations.supportedLocales,
            home: AuthGate(),
          ),
        ),
      );
      //await tester.pumpAndSettle();

      final pingButton = find.text('Ping!');

      await tester.tap(pingButton);

      await tester.pumpAndSettle();

      expect(find.text('Last ping:'), findsOneWidget);
    });

    testWidgets('ping is unsuccesful due to lack of connection', (
      tester,
    ) async {
      when(
        () => mockGeneralLayoutViewModel.activeScreen,
      ).thenReturn(AppScreen.home);
      when(
        () => mockGeneralLayoutViewModel.currentIndex,
      ).thenReturn(mockGeneralLayoutViewModel.activeScreen.index);
      when(
        () => mockGeneralLayoutViewModel.startBackgroundServices(),
      ).thenAnswer((_) async {});
      when(() => mockUserViewModel.isLoggedIn).thenReturn(true);
      when(() => mockUserViewModel.isLoading).thenReturn(false);

      when(
        () => mockUserViewModel.checkAuthorizationStatus(),
      ).thenAnswer((_) async => true);
      when(() => mockHomeViewModel.triggerPingAndNotify()).thenAnswer((
        _,
      ) async {
        when(() => mockHomeViewModel.lastPing).thenReturn(false);
        mockHomeViewModel.notifyListeners();
      });
      when(() => mockHomeViewModel.lastPing).thenReturn(false);

      await tester.pumpWidget(
        MultiProvider(
          providers: [
            ChangeNotifierProvider<UserViewModel>.value(
              value: mockUserViewModel,
            ),
            ChangeNotifierProvider<GeneralLayoutViewModel>.value(
              value: mockGeneralLayoutViewModel,
            ),
            ChangeNotifierProvider<HomeViewModel>.value(
              value: mockHomeViewModel,
            ),
            ChangeNotifierProvider<AlertsViewModel>.value(
              value: mockAlertsViewModel,
            ),
            ChangeNotifierProvider<PluginsViewModel>.value(
              value: mockPluginsViewModel,
            ),
            ChangeNotifierProvider<SettingsViewModel>.value(
              value: mockSettingsViewModel,
            ),
          ],
          child: const MaterialApp(
            localizationsDelegates: AppLocalizations.localizationsDelegates,
            supportedLocales: AppLocalizations.supportedLocales,
            home: AuthGate(),
          ),
        ),
      );

      final pingButton = find.text('Ping!');

      await tester.tap(pingButton);

      expect(find.text('No connection'), findsWidgets);
    });
  });
}
