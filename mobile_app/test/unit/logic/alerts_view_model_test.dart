import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:alert_app/data/services/push_notification_service.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get_it/get_it.dart';
import 'package:mocktail/mocktail.dart';

class MockAlertRepository extends Mock implements AlertRepository {}

class MockPushNotificationService extends Mock
    implements PushNotificationService {}

class MockAlarmService extends Mock implements AlarmService {}

void main() {
  late MockAlertRepository mockAlertRepository;
  late MockPushNotificationService mockPushNotificationService;
  late MockAlarmService mockAlarmService;
  setUp(() {
    mockAlertRepository = MockAlertRepository();

    when(() => mockAlertRepository.getOfflineAlerts()).thenAnswer(
      (_) async => [
        Alert(
          id: 10,
          subject: "Mock Alert",
          source: "Mock Server",
          severity: AlertSeverity.medium,
          status: AlertStatus.sent,
          createdAt: DateTime.parse("2026-06-14 19:43:19.634"),
          message: "Test alert for unit testing",
          author: " Tester",
          acknowledged: false,
        ),
        Alert(
          id: 11,
          subject: "Mock Alert",
          source: "Mock Server",
          severity: AlertSeverity.medium,
          status: AlertStatus.sent,
          createdAt: DateTime.parse("2026-06-14 19:43:19.634"),
          message: "Test alert for unit testing",
          author: " Tester",
          acknowledged: false,
        ),
        Alert(
          id: 13,
          subject: "Mock Alert",
          source: "Mock Server",
          severity: AlertSeverity.medium,
          status: AlertStatus.sent,
          createdAt: DateTime.parse("2026-06-14 19:43:19.634"),
          message: "Test alert for unit testing",
          author: " Tester",
          acknowledged: false,
        ),
      ],
    );

    when(
      () => mockAlertRepository.markAlertAsNotified(any()),
    ).thenAnswer((_) async {});
    mockAlarmService = MockAlarmService();
    GetIt.instance.registerSingleton<AlarmService>(mockAlarmService);

    when(
      () => mockAlarmService.showEmergencyOverlay('Alert'),
    ).thenAnswer((_) async {});

    mockPushNotificationService = MockPushNotificationService();
    when(
      () => mockPushNotificationService.fcmPayloadStream,
    ).thenAnswer((_) => const Stream.empty());
  });
  tearDown(() async {
    await GetIt.instance.reset();
  });

  group('_loadOfflineAlerts tests', () {
    test('Succesfully loading alerts', () async {
      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );
      await Future.microtask(() {});

      expect(viewModel.alertsList.isNotEmpty, true);
    });
  });

  group('sortAlerts tests', () {
    test('Sort by id', () async {
      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );
      await Future.microtask(() {});

      final sortedList = viewModel.sortAlerts('id', false);
      final sortedAlertsIds = sortedList.map((alert) => alert.id).toList();
      expect(sortedAlertsIds, orderedEquals([13, 11, 10]));
    });
  });

  group('fetchInitialAlerts', () {
    test('succesfully fetch all alerts', () async {
      when(() => mockAlertRepository.fetchAllAlerts()).thenAnswer(
        (_) async => [
          Alert(
            id: 24,
            subject: "Mock Alert",
            source: "Mock Server",
            severity: AlertSeverity.medium,
            status: AlertStatus.sent,
            createdAt: DateTime.parse("2026-06-14 19:43:19.634"),
            message: "Test alert for unit testing",
            author: " Tester",
            acknowledged: false,
          ),
        ],
      );
      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );

      await viewModel.fetchInitialAlerts();
      expect(viewModel.alertsList.first.id, 24);
    });
  });
  group('_handleIncomingSseUpdate()', () {
    test('adding whole new alert', () {
      final mockMessage = {
        "id": 1,
        "subject": "test alert",
        "severity": 2,
        "status": "sent",
      };

      when(
        () => mockAlertRepository.isAlertAlreadyNotified(any()),
      ).thenAnswer((_) async => true);
      when(
        () => mockAlertRepository.saveAlertsToOfflineCache(any()),
      ).thenAnswer((_) async {});

      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );

      viewModel.handleIncomingSseUpdate(mockMessage);

      expect(viewModel.alertsList.length, 1);
    });

    test('partial update', () async {
      final mockMessage = {"alertId": 10, "newSeverity": 1, "severity": 1};

      when(
        () => mockAlertRepository.isAlertAlreadyNotified(any()),
      ).thenAnswer((_) async => true);
      when(
        () => mockAlertRepository.saveAlertsToOfflineCache(any()),
      ).thenAnswer((_) async {});

      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );

      await Future.microtask(() {});

      await viewModel.handleIncomingSseUpdate(mockMessage);
      final modifiedAlert = viewModel.alertsList.firstWhere(
        (alert) => alert.id == mockMessage['alertId'],
      );

      expect(modifiedAlert.severity, AlertSeverity.info);
    });
  });

  group('_checkingforEmergency', () {
    test('Alert already notified, no emergency', () async {
      final testAlert = Alert(
        id: 11,
        subject: "Mock Alert",
        source: "Mock Server",
        severity: AlertSeverity.medium,
        status: AlertStatus.sent,
        createdAt: DateTime.parse("2026-06-14 19:43:19.634"),
        message: "Test alert for unit testing",
        author: " Tester",
        acknowledged: false,
      );
      when(
        () => mockAlertRepository.isAlertAlreadyNotified(any()),
      ).thenAnswer((_) async => true);

      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );
      await viewModel.checkingForEmergency(testAlert);
      verifyNever(() => mockAlarmService.showEmergencyOverlay('Alert'));
      verifyNever(() => mockAlertRepository.markAlertAsNotified(testAlert.id));
    });

    test('Alert not notified,  emergency', () async {
      final testAlert = Alert(
        id: 11,
        subject: "Mock Alert",
        source: "Mock Server",
        severity: AlertSeverity.critical,
        status: AlertStatus.sent,
        createdAt: DateTime.parse("2026-06-14 19:43:19.634"),
        message: "Test alert for unit testing",
        author: " Tester",
        acknowledged: false,
      );
      when(
        () => mockAlertRepository.isAlertAlreadyNotified(any()),
      ).thenAnswer((_) async => false);

      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );
      await viewModel.checkingForEmergency(testAlert);
      verify(() => mockAlarmService.showEmergencyOverlay('Alert')).called(1);
      verify(
        () => mockAlertRepository.markAlertAsNotified(testAlert.id),
      ).called(1);
    });
  });

  group('acknowledgeAlert', () {
    test('Send ack to repository succesfully', () async {
      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );

      await viewModel.acknowledgeAlert(10, author: 'Tester');

      when(
        () => mockAlertRepository.acknowledgeAlert(
          alertId: 10,
          author: 'Tester',
          message: '',
          newSeverity: 3,
          isAck: true,
        ),
      ).thenAnswer((_) async => {});
    });

    test('Send ack to repository unsuccesfully', () async {
      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );
      await Future.microtask(() {});

      when(
        () => mockAlertRepository.acknowledgeAlert(
          alertId: 10,
          author: 'Tester',
          message: '',
          newSeverity: 3,
          isAck: true,
        ),
      ).thenThrow(Exception());

      await viewModel.acknowledgeAlert(10, author: 'Tester');

      final modifiedAlert = viewModel.alertsList.firstWhere(
        (alert) => alert.id == 10,
      );
      expect(modifiedAlert.acknowledged, false);
    });
  });

  group('getLatestActionForAlert', () {
    test('Get action succesfully, but it is null', () async {
      final viewModel = AlertsViewModel(
        alertsRepository: mockAlertRepository,
        pushNotificationService: mockPushNotificationService,
      );

      when(
        () => mockAlertRepository.getLatestAction(any()),
      ).thenAnswer((_) async => null);
      final lastAction = await viewModel.getLatestActionForAlert(10);
      expect(lastAction, null);
    });
  });
}
