import 'package:alert_app/data/models/alert_action_model.dart';
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get_it/get_it.dart';
import 'package:mocktail/mocktail.dart';
import 'package:dio/dio.dart';
import 'package:alert_app/data/datasources/alert_remote_data_source.dart';
import 'package:alert_app/data/models/alert_model.dart';

///Mock classes for Dio
class MockDio extends Mock implements Dio {}

class MockAlarmService extends Mock implements AlarmService {}

//NOTE!!! There is no SSE test here - T0D0 as integration test for alert repository
void main() {
  late AlertRemoteDataSource dataSource;
  late MockDio mockDio;
  late MockAlarmService mockAlarmService;

  setUp(() {
    mockDio = MockDio();
    dataSource = AlertRemoteDataSource(dio: mockDio);
    mockAlarmService = MockAlarmService();
    when(
      () => mockAlarmService.showEmergencyOverlay(any()),
    ).thenAnswer((_) async {});
    GetIt.instance.registerSingleton<AlarmService>(mockAlarmService);
    registerFallbackValue(RequestOptions(path: ''));
  });

  tearDown(() async {
    await GetIt.instance.reset();
  });

  group('fetchActiveAlerts', () {
    test('should return List<Alert>, when getting 200 from server', () async {
      final mockResponseData = [
        {"id": 1, "subject": "test alert", "severity": 2, "status": "sent"},
      ];

      when(() => mockDio.get('/api/alerts/active')).thenAnswer(
        (_) async => Response(
          data: mockResponseData,
          statusCode: 200,
          requestOptions: RequestOptions(path: '/api/alerts/active'),
        ),
      );

      final result = await dataSource.fetchActiveAlerts();

      expect(result, isA<List<Alert>>());
      expect(result.length, 1);
      expect(result.first.id, 1);
      expect(result.first.subject, "test alert");
    });

    test('should return DioException in case of server error', () async {
      when(() => mockDio.get(any())).thenThrow(
        DioException(
          requestOptions: RequestOptions(path: ''),
          error: 'Not Found',
        ),
      );

      // without await to catch method throwing exeption
      final call = dataSource.fetchActiveAlerts;

      expect(() => call(), throwsA(isA<DioException>()));
    });
  });

  group('acknowledgeAlert', () {
    const testAlertId = 338;
    const testAuthor = 'admin@pjatk.pl';
    const testMessage = 'test1';
    const testNewSeverity = 1;
    const testIsAck = true;

    final tExpectedRequestBody = {
      "alertId": testAlertId,
      "author": testAuthor,
      "ack": testIsAck,
      "message": testMessage,
      "newSeverity": testNewSeverity,
    };

    test(
      'should send correct POST with correct data for correct endpoint',
      () async {
        when(() => mockDio.post(any(), data: any(named: 'data'))).thenAnswer(
          (_) async => Response(
            statusCode: 200,
            requestOptions: RequestOptions(path: ''),
          ),
        );

        await dataSource.acknowledgeAlert(
          alertId: testAlertId,
          author: testAuthor,
          message: testMessage,
          newSeverity: testNewSeverity,
          isAck: testIsAck,
        );

        verify(
          () => mockDio.post(
            '/api/alerts/$testAlertId/ack',
            data: tExpectedRequestBody,
          ),
        ).called(1);
      },
    );

    test('should throw exception, when POST fail', () async {
      when(
        () => mockDio.post(any(), data: any(named: 'data')),
      ).thenThrow(DioException(requestOptions: RequestOptions(path: '')));

      expect(
        () => dataSource.acknowledgeAlert(
          alertId: testAlertId,
          author: testAuthor,
          message: testMessage,
          newSeverity: testNewSeverity,
          isAck: testIsAck,
        ),
        throwsA(isA<DioException>()),
      );
    });
  });

  group('fetchLatestAction', () {
    final testAlertId = 123;

    test(
      'should return AlertAction object, when getting list with something inside',
      () async {
        final mockResponseData = [
          {"id": 1, "author": "System", "message": "Comment", "ack": false},
        ];

        when(() => mockDio.get(any())).thenAnswer(
          (_) async => Response(
            data: mockResponseData,
            statusCode: 200,
            requestOptions: RequestOptions(path: ''),
          ),
        );

        final result = await dataSource.fetchLatestAction(testAlertId);

        expect(result, isA<AlertAction>());
        expect(result?.message, "Comment");
      },
    );

    test('should return null, when list is empty', () async {
      when(() => mockDio.get(any())).thenAnswer(
        (_) async => Response(
          data: [],
          statusCode: 200,
          requestOptions: RequestOptions(path: ''),
        ),
      );

      final result = await dataSource.fetchLatestAction(testAlertId);

      expect(result, isNull);
    });

    test('should throw an exception when server fail', () async {
      when(
        () => mockDio.get(any()),
      ).thenThrow(DioException(requestOptions: RequestOptions(path: '')));

      expect(
        () => dataSource.fetchLatestAction(testAlertId),
        throwsA(isA<DioException>()),
      );
    });
  });
  group('isBackendConnected()', () {
    test('should succeed', () async {
      final mockResponseData = [
        {"id": 1, "subject": "test alert", "severity": 2, "status": "sent"},
      ];

      when(() => mockDio.get('/api/alerts/active')).thenAnswer(
        (_) async => Response(
          data: mockResponseData,
          statusCode: 200,
          requestOptions: RequestOptions(path: '/api/alerts/active'),
        ),
      );

      final result = await dataSource.isBackendConnected();
      expect(result, true);
    });
    test('should throw Dio Exception on any issue with connection', () async {
      when(() => mockDio.get(any())).thenThrow(
        DioException(
          requestOptions: RequestOptions(path: ''),
          error: 'Not Found',
        ),
      );
      final result = await dataSource.isBackendConnected();
      expect(result, false);
    });
  });
}
