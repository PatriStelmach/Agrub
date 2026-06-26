import 'dart:async';
import 'package:alert_app/data/models/alert_action_model.dart';
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:alert_app/data/services/push_notification_service.dart';
import 'package:alert_app/locator.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/repositories/alert_repository.dart';

class AlertsViewModel extends ChangeNotifier {
  final AlertRepository alertsRepository;
  final PushNotificationService pushNotificationService;
  final Map<int, Alert> _alertsCache = {};
  bool _isLoading = false;
  String _currentSortProperty = 'id';
  bool _isAscending = false;
  StreamSubscription? _sseSubscription;

  bool get isLoading => _isLoading;
  String get currentSortProperty => _currentSortProperty;
  bool get isAscending => _isAscending;

  List<Alert> get alertsList => _alertsCache.values.toList();

  AlertsViewModel({
    required this.alertsRepository,
    required this.pushNotificationService,
  }) {
    _loadOfflineAlerts();

    pushNotificationService.fcmPayloadStream.listen((rawData) {
      debugPrint("ALERTS VIEW MODEL - Caught live notitifaction");
      _handleIncomingSseUpdate(rawData);
    });
  }

  ///Private method for loading alerts from storage
  Future<void> _loadOfflineAlerts() async {
    final offlineAlerts = await alertsRepository.getOfflineAlerts();

    for (var alert in offlineAlerts) {
      _alertsCache[alert.id] = alert;
    }
    notifyListeners();
  }

  ///Method sorting alerts by certain fields, ascending and descending change using compareTo() result values
  List<Alert> sortAlerts(String sortProperty, bool ascending) {
    final List<Alert> sortedList = _alertsCache.values.toList();

    sortedList.sort((a, b) {
      int compareToValue;
      switch (sortProperty) {
        case 'title':
          compareToValue = a.subject.compareTo(b.subject);
        case 'severity':
          compareToValue = a.severity.index.compareTo(b.severity.index);
        case 'createdAt':
          compareToValue = a.createdAt.compareTo(b.createdAt);
        case 'acknowledged':
          final valueA = a.acknowledged ? 1 : 0;
          final valueB = b.acknowledged ? 1 : 0;
          compareToValue = valueA.compareTo(valueB);

        default:
          compareToValue = a.id.compareTo(b.id);
      }

      return ascending ? compareToValue : -compareToValue;
    });

    return sortedList;
  }

  /// Fetching first data on screen start
  Future<void> fetchInitialAlerts() async {
    _isLoading = true;
    notifyListeners();

    final alerts = await alertsRepository.fetchAllAlerts();

    _alertsCache.clear();
    for (var alert in alerts) {
      _alertsCache[alert.id] = alert;
      if (alert.severity == AlertSeverity.critical) {
        unawaited(_checkingForEmergency(alert));
      }
    }

    _isLoading = false;
    notifyListeners();
  }

  ///Initializing SSE
  void initializeSseConnection({
    required String userRole,
    required String token,
  }) {
    _sseSubscription?.cancel();

    _sseSubscription = alertsRepository
        .getAlertsUpdateStream(userRole: userRole, token: token)
        .listen((message) {
          _handleIncomingSseUpdate(message);
          notifyListeners();
        });
  }

  @override
  void dispose() {
    _sseSubscription?.cancel();
    super.dispose();
  }

  ///Processing update from SSE and updating the view.
  ///Method can handle full new object and partial update
  Future<void> _handleIncomingSseUpdate(dynamic message) async {
    if (message.containsKey('subject')) {
      final alert = Alert.fromJson(message);
      _alertsCache[alert.id] = alert;

      unawaited(_checkingForEmergency(alert));
    } else if (_alertsCache.containsKey(message['alertId'])) {
      final existing = _alertsCache[message['alertId']]!;
      Alert updated = existing;

      if (message.containsKey('newSeverity')) {
        updated = updated.copyWith(
          severity: AlertSeverity.values[message['newSeverity']],
        );
      }
      if (message.containsKey('ack')) {
        updated = updated.copyWith(acknowledged: message['ack']);
      }
      if (message.containsKey('comment') || message.containsKey('message')) {
        updated = updated.copyWith(
          message: message['comment'] ?? message['message'],
        );
      }

      _alertsCache[message['alertId']] = updated;
      unawaited(_checkingForEmergency(updated));
    }
    await alertsRepository.saveAlertsToOfflineCache(alertsList);
    await fetchInitialAlerts();

    notifyListeners();
  }

  ///Triggering emergency overlay if needed and saving notified status of incoming alert
  Future<void> _checkingForEmergency(Alert alert) async {
    final alreadyNotified = await alertsRepository.isAlertAlreadyNotified(
      alert.id,
    );

    if (alert.severity == AlertSeverity.critical && !alreadyNotified) {
      await locator<AlarmService>().showEmergencyOverlay("Alert");
      debugPrint("ALERTS VIEW MODEL - Emergency overlay triggered");

      await alertsRepository.markAlertAsNotified(alert.id);
    }
  }

  /// Updating private properties and calling sort method with them, then updating the view
  void sortAlertsBy(String property, bool ascending) {
    _currentSortProperty = property;
    _isAscending = ascending;
    notifyListeners();
  }

  ///Sending ack via repository and Optimistic UI change with reverting catch
  Future<void> acknowledgeAlert(
    int alertId, {
    required String author,
    String? comment,
    bool isAck = true,
    int? newSeverity,
  }) async {
    final originalAlert = _alertsCache[alertId];
    if (originalAlert == null) {
      return;
    }

    notifyListeners();

    try {
      await alertsRepository.acknowledgeAlert(
        alertId: alertId,
        author: author,
        message: comment ?? "",
        newSeverity: newSeverity ?? 1,
        isAck: isAck,
      );
    } catch (e) {
      _alertsCache[alertId] = originalAlert;

      notifyListeners();
      debugPrint("ALERT VIEW MODEL - Error during ACK, reverting changes $e");
    }
  }

  ///Getting latest action so Screen can display it in alert card
  Future<AlertAction?> getLatestActionForAlert(int alertId) async {
    return await alertsRepository.getLatestAction(alertId);
  }

  @visibleForTesting
  Future<void> handleIncomingSseUpdate(dynamic message) =>
      _handleIncomingSseUpdate(message);

  @visibleForTesting
  Future<void> checkingForEmergency(Alert alert) =>
      _checkingForEmergency(alert);
}
