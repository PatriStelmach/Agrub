import 'package:alert_app/data/datasources/alert_local_data_source.dart';
import 'package:alert_app/data/datasources/alert_remote_data_source.dart';
import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/models/alert_action_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';
import 'package:dio/dio.dart';
import 'package:mocktail/mocktail.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_client_sse/flutter_client_sse.dart';

class MockAlertRemoteDataSource extends Mock implements AlertRemoteDataSource {}

class MockAlertLocalDataSource extends Mock implements AlertLocalDataSource {}

void main() {
  late AlertRepository repository;
  late MockAlertRemoteDataSource mockRemoteDataSource;
  late MockAlertLocalDataSource mockLocalDataSource;

  setUp(() {
    mockRemoteDataSource = MockAlertRemoteDataSource();
    mockLocalDataSource = MockAlertLocalDataSource();

    repository = AlertRepository(
      remoteDataSource: mockRemoteDataSource,
      localDataSource: mockLocalDataSource,
    );

    registerFallbackValue(<Alert>[]);
  });
  group('fetchAllAlerts', () {
    test('Should let Alert object through', () async {
      final testAlert = Alert(
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

      when(
        () => mockRemoteDataSource.fetchActiveAlerts(),
      ).thenAnswer((_) async => [testAlert]);
      when(
        () => mockLocalDataSource.saveAlerts(any()),
      ).thenAnswer((_) async {});

      final List<Alert> testResult = await repository.fetchAllAlerts();
      expect(testResult, isA<List<Alert>>());
      expect(testResult.length, 1);
      expect(testResult.first, testAlert);
    });

    //T0D0 - need to finish this test and add another for 35-36 to get 100% coverage
    test(
      'Should return alerts stored locally when remote data source throws exception',
      () async {
        final testException = Exception('General exception');

        final localAlerts = [
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
        ];
        // when(
        //   () => mockRemoteDataSource.fetchActiveAlerts(),
        // ).thenAnswer((_) async => throw testException);
        // when(
        //   () => mockLocalDataSource.fetch(),
        // ).thenAnswer((_) async => throw testException);
      },
    );
  });

  group('acknowledgeAlert', () {
    test(
      'acknowledgedAlert take in an acknowledged Alert and succesfully send it through remote data source',
      () async {
        when(
          () => mockRemoteDataSource.acknowledgeAlert(
            alertId: 10,
            author: "Tester",
            message: "Working on it",
            newSeverity: 3,
            isAck: true,
          ),
        ).thenAnswer((_) async {});

        await repository.acknowledgeAlert(
          alertId: 10,
          author: "Tester",
          message: "Working on it",
          newSeverity: 3,
          isAck: true,
        );

        verify(
          () => mockRemoteDataSource.acknowledgeAlert(
            alertId: 10,
            author: "Tester",
            message: "Working on it",
            newSeverity: 3,
            isAck: true,
          ),
        ).called(1);
      },
    );
    test('DioException is thrown and passed through to ViewModel', () async {
      final testDioException = DioException(
        requestOptions: RequestOptions(path: '/api/alerts/10/ack'),
        type: DioExceptionType.connectionError,
        message: 'No server connection',
      );

      when(
        () => mockRemoteDataSource.acknowledgeAlert(
          alertId: 10,
          author: "Tester",
          message: "Working on it",
          newSeverity: 3,
          isAck: true,
        ),
      ).thenAnswer((_) => throw (testDioException));

      expect(
        () => repository.acknowledgeAlert(
          alertId: 10,
          author: "Tester",
          message: "Working on it",
          newSeverity: 3,
          isAck: true,
        ),
        throwsA(testDioException),
      );
    });

    test(
      'General exception is thrown and passed through to ViewModel',
      () async {
        final testException = Exception("General exception");

        when(
          () => mockRemoteDataSource.acknowledgeAlert(
            alertId: 10,
            author: "Tester",
            message: "Working on it",
            newSeverity: 3,
            isAck: true,
          ),
        ).thenAnswer((_) => throw (testException));

        expect(
          () => repository.acknowledgeAlert(
            alertId: 10,
            author: "Tester",
            message: "Working on it",
            newSeverity: 3,
            isAck: true,
          ),
          throwsA(testException),
        );
      },
    );
  });

  group('getLatestActionForAlert', () {
    test('Fetch latest comment or action for an alert', () async {
      final testLatestAction = AlertAction(
        id: 11,
        author: "Admin",
        message: "Taking over",
        ack: true,
        newSeverity: 3,
        createdAt: DateTime.parse('2026-06-14 19:43:19.634'),
      );
      when(
        () => mockRemoteDataSource.fetchLatestAction(testLatestAction.id),
      ).thenAnswer((_) async => testLatestAction);

      final problemActionResponse = await repository.getLatestActionForAlert(
        testLatestAction.id,
      );

      expect(problemActionResponse, testLatestAction);
    });
    test(
      'Fetch latest comment and pass DioException through to ViewModel',
      () async {
        final testLatestAction = AlertAction(
          id: 11,
          author: "Admin",
          message: "Taking over",
          ack: true,
          newSeverity: 3,
          createdAt: DateTime.parse('2026-06-14 19:43:19.634'),
        );
        final testDioException = DioException(
          requestOptions: RequestOptions(path: '/api/alerts/11/actions'),
          type: DioExceptionType.connectionError,
          message: 'No server connection',
        );

        when(
          () => mockRemoteDataSource.fetchLatestAction(testLatestAction.id),
        ).thenAnswer((_) async => throw testDioException);

        expect(
          () => repository.getLatestActionForAlert(testLatestAction.id),
          throwsA(testDioException),
        );
      },
    );
    test(
      'Fetch latest comment and pass DioException through to ViewModel',
      () async {
        final testLatestAction = AlertAction(
          id: 11,
          author: "Admin",
          message: "Taking over",
          ack: true,
          newSeverity: 3,
          createdAt: DateTime.parse('2026-06-14 19:43:19.634'),
        );

        final testException = Exception("General exception");

        when(
          () => mockRemoteDataSource.fetchLatestAction(testLatestAction.id),
        ).thenAnswer((_) async => throw testException);

        expect(
          () => repository.getLatestActionForAlert(testLatestAction.id),
          throwsA(testException),
        );
      },
    );
  });
  group('getLatestActionForAlert', () {
    final mockEventsStream = [
      SSEModel(
        data:
            '{"eventType": "ALERT_UPDATE", "message": "ALERT_UPDATE Critical alert"}',
      ),
      SSEModel(
        data:
            '{"eventType": "ALERT_UPDATE_ONLY", "message": "ALERT_UPDATE_ONLY Critical alert"}',
      ),
      SSEModel(data: 'Spam not matching desired data'),
      SSEModel(
        data:
            '{"eventType": "STRANGE_ACTION", "message": "Spamming the system"}',
      ),
    ];
    test(
      'Recognizing legit action types and passing only them to ViewModel',
      () async {
        when(
          () => mockRemoteDataSource.getAlertsStream(
            userGroup: any(named: 'userGroup'),
            token: any(named: 'token'),
          ),
        ).thenAnswer((_) => Stream.fromIterable(mockEventsStream));

        final stream = repository.getAlertsUpdateStream(
          userGroup: 'Admin',
          token: '123',
        );
        await expectLater(
          stream,
          emitsInOrder([
            "ALERT_UPDATE Critical alert",
            "ALERT_UPDATE_ONLY Critical alert",
            emitsDone,
          ]),
        );
      },
    );
    test('Recognizing legit action types but catching broken JSON', () async {
      final mockEventsWithBrokenJson = [
        SSEModel(
          data:
              '{"eventType": "ALERT_UPDATE", "message": "ALERT_UPDATE Critical alert"}',
        ),
        SSEModel(
          data: '{"eventType": "ALERT_UPDATE_ONLY", "message": "ALERT_UPDATE_',
        ),
        SSEModel(
          data:
              '{"eventType": "ALERT_UPDATE_ONLY", "message": "ALERT_UPDATE_ONLY Critical alert"}',
        ),
      ];
      when(
        () => mockRemoteDataSource.getAlertsStream(
          userGroup: any(named: 'userGroup'),
          token: any(named: 'token'),
        ),
      ).thenAnswer((_) => Stream.fromIterable(mockEventsWithBrokenJson));

      final stream = repository.getAlertsUpdateStream(
        userGroup: 'Admin',
        token: '123',
      );
      await expectLater(
        stream,
        emitsInOrder([
          "ALERT_UPDATE Critical alert",
          //Broken JSON should show up in terminal with error message
          "ALERT_UPDATE_ONLY Critical alert",
          emitsDone,
        ]),
      );
    });
  });
  group('saveAlertsToOfflineCache', () {
    final mockAlerts = [
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
    ];
    test('Saving alert to local cache', () async {
      when(
        () => mockLocalDataSource.saveAlerts(any()),
      ).thenAnswer((_) async {});

      await repository.saveAlertsToOfflineCache(mockAlerts);

      verify(() => mockLocalDataSource.saveAlerts(mockAlerts)).called(1);
    });

    test('Saving alert to local cache with error', () async {
      final testException = Exception("General exception");
      when(
        () => mockLocalDataSource.saveAlerts(any()),
      ).thenAnswer((_) async => throw testException);
      await repository.saveAlertsToOfflineCache(mockAlerts);

      verify(() => mockLocalDataSource.saveAlerts(mockAlerts)).called(1);
    });
  });
  group('markAlertsAsNotified', () {
    test('marking alerts as notified with sound alarm', () async {
      when(
        () => mockLocalDataSource.markAlertAsNotified(any()),
      ).thenAnswer((_) async => {});

      await repository.markAlertAsNotified(11);

      verify(() => mockLocalDataSource.markAlertAsNotified(11)).called(1);
    });
  });
  group('isAlertAlreadyNotified', () {
    test('Checking if alert is notified', () async {
      when(
        () => mockLocalDataSource.wasAlertAlreadyNotified(any()),
      ).thenAnswer((_) async => true);

      final alertNotified = await repository.isAlertAlreadyNotified(10);

      expect(alertNotified, true);
    });
  });
}
