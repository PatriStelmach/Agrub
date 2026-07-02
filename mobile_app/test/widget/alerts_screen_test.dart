import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/locator.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/screens/alert/alert_card.dart';
import 'package:alert_app/screens/alert/alerts_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get_it/get_it.dart';
import 'package:mocktail/mocktail.dart';
import 'package:provider/provider.dart';

class MockFlutterSecureStorage extends Mock implements FlutterSecureStorage {}

class MockAlertsViewModel extends Mock
    with ChangeNotifier
    implements AlertsViewModel {}

void main() {
  late MockFlutterSecureStorage mockFlutterSecureStorage;
  late MockAlertsViewModel mockAlertsViewModel;

  final Alert testAlert = Alert(
    id: 10,
    subject: "Mock Alert",
    source: "Mock Server",
    severity: AlertSeverity.medium,
    status: AlertStatus.sent,
    createdAt: DateTime.parse("2026-06-14 19:43:19.634"),
    message: "Test alert for unit testing",
    author: " Tester",
    acknowledged: false,
  );
  setUpAll(() {
    registerFallbackValue(0);

    mockFlutterSecureStorage = MockFlutterSecureStorage();
    mockAlertsViewModel = MockAlertsViewModel();
    locator.allowReassignment = true;
    GetIt.instance.registerSingleton<FlutterSecureStorage>(
      mockFlutterSecureStorage,
    );

    when(() => mockAlertsViewModel.alertsList).thenReturn(<Alert>[]);

    when(() => mockAlertsViewModel.isLoading).thenReturn(false);
    when(
      () => mockAlertsViewModel.fetchInitialAlerts(),
    ).thenAnswer((_) async {});
    when(
      () => mockAlertsViewModel.getLatestActionForAlert(any()),
    ).thenAnswer((_) async => null);
  });
  testWidgets('Building alerts screen', (WidgetTester tester) async {
    await tester.pumpWidget(
      ChangeNotifierProvider<AlertsViewModel>.value(
        value: mockAlertsViewModel,
        child: const MaterialApp(
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,
          home: Scaffold(body: AlertsScreen()),
        ),
      ),
    );
    await tester.pumpAndSettle();

    expect(find.byType(AlertsScreen), findsOneWidget);
  });

  testWidgets('Build alert card', (WidgetTester tester) async {
    final t = lookupAppLocalizations(const Locale('en'));

    await tester.pumpWidget(
      ChangeNotifierProvider<AlertsViewModel>.value(
        value: mockAlertsViewModel,
        child: MaterialApp(
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,
          home: Scaffold(
            body: SingleChildScrollView(
              child: AlertCard(
                alert: testAlert,
                viewModel: mockAlertsViewModel,
                t: t,
              ),
            ),
          ),
        ),
      ),
    );
    await tester.pumpAndSettle();
    final subjectFinder = find.text('Mock Alert');
    final sourceFinder = find.text('NOT ACKNOWLEDGED');

    expect(subjectFinder, findsOneWidget);
    expect(sourceFinder, findsOneWidget);
  });

  testWidgets('Tap the alert card', (WidgetTester tester) async {
    final t = lookupAppLocalizations(const Locale('en'));
    await tester.pumpWidget(
      ChangeNotifierProvider<AlertsViewModel>.value(
        value: mockAlertsViewModel,
        child: MaterialApp(
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,

          home: Scaffold(
            body: SingleChildScrollView(
              child: AlertCard(
                alert: testAlert,
                viewModel: mockAlertsViewModel,
                t: t,
              ),
            ),
          ),
        ),
      ),
    );
    await tester.pumpAndSettle();

    final cardFinder = find.byType(ExpansionTile);
    await tester.tap(cardFinder);
    await tester.pumpAndSettle();

    final sourceFinder = find.text('Mock Server');
    final messageFinder = find.text('No alert history');

    expect(sourceFinder, findsOneWidget);
    expect(messageFinder, findsOneWidget);
  });

  testWidgets('Open actions dialogue window', (WidgetTester tester) async {
    final t = lookupAppLocalizations(const Locale('en'));
    await tester.pumpWidget(
      ChangeNotifierProvider<AlertsViewModel>.value(
        value: mockAlertsViewModel,
        child: MaterialApp(
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,

          home: Scaffold(
            body: SingleChildScrollView(
              child: AlertCard(
                alert: testAlert,
                viewModel: mockAlertsViewModel,
                t: t,
              ),
            ),
          ),
        ),
      ),
    );
    await tester.pumpAndSettle();

    final cardFinder = find.byType(ExpansionTile);
    await tester.tap(cardFinder);
    await tester.pumpAndSettle();
    await tester.tap(find.text('Actions'));
    await tester.pumpAndSettle();

    final sourceFinder = find.text('Acknowledge alert');

    expect(sourceFinder, findsOneWidget);
  });

  testWidgets('Tap acknowledge box', (WidgetTester tester) async {
    final t = lookupAppLocalizations(const Locale('en'));
    await tester.pumpWidget(
      ChangeNotifierProvider<AlertsViewModel>.value(
        value: mockAlertsViewModel,
        child: MaterialApp(
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,

          home: Scaffold(
            body: SingleChildScrollView(
              child: AlertCard(
                alert: testAlert,
                viewModel: mockAlertsViewModel,
                t: t,
              ),
            ),
          ),
        ),
      ),
    );
    await tester.pumpAndSettle();

    final cardFinder = find.byType(ExpansionTile);
    await tester.tap(cardFinder);
    await tester.pumpAndSettle();
    await tester.tap(find.text('Actions'));
    await tester.pumpAndSettle();
    await tester.tap(find.byType(CheckboxListTile));
    await tester.pumpAndSettle();

    final checkboxFinder = find.byType(CheckboxListTile);
    final checkbox = tester.widget<CheckboxListTile>(checkboxFinder);

    expect(checkbox.value, isTrue);
  });
}
