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
  //UI state variables
  final Map<int, Alert> _alertsCache = {};
  bool _isLoading = false;
  String _currentSortProperty = 'id';
  bool _isAscending = true;
  StreamSubscription? _sseSubscription;

  AlertsViewModel({
    required this.alertsRepository,
    required this.pushNotificationService,
  }) {
    _loadOfflineAlertsIntoCache();

    pushNotificationService.fcmPayloadStream.listen((rawData) {
      debugPrint("AlertsViewModel: Przechwycono powiadomienie FCM na żywo!");
      _handleIncomingSseUpdate(rawData);
    });
  }
  Future<void> _loadOfflineAlertsIntoCache() async {
    try {
      final offlineAlerts = await alertsRepository.getOfflineAlerts();
      for (var alert in offlineAlerts) {
        _alertsCache[alert.id] = alert;
      }
      notifyListeners();
    } catch (e) {
      debugPrint("AlertsViewModel Error: Błąd ładowania cache offline: $e");
    }
  }

  //Getters
  bool get isLoading => _isLoading;
  String get currentSortProperty => _currentSortProperty;
  bool get isAscending => _isAscending;

  List<Alert> get alertsList => _alertsCache.values.toList();
  List<Alert> get sortedAlerts {
    final list = List<Alert>.from(alertsList);

    final Map<String, Comparable Function(Alert)> getters = {
      'id': (alert) => alert.id,
      'title': (alert) => alert.subject,
      'hostName': (alert) => alert.source,
      'severity': (alert) => alert.severity.index,
      'status': (alert) => alert.status.index,
      'createdAt': (alert) => alert.createdAt,
      'description': (alert) => alert.message,
      'source': (alert) => alert.source,
    };

    final getter = getters[_currentSortProperty] ?? (alert) => alert.id;

    list.sort((a, b) {
      final aValue = getter(a);
      final bValue = getter(b);

      return _isAscending ? aValue.compareTo(bValue) : bValue.compareTo(aValue);
    });

    return list;
  }

  /// Fetching first data on screen start
  Future<void> fetchInitialAlerts() async {
    _isLoading = true;
    notifyListeners();

    try {
      final alerts = await alertsRepository.fetchAllAlerts();

      _alertsCache.clear();
      for (var alert in alerts) {
        _alertsCache[alert.id] = alert;
        if (alert.severity == AlertSeverity.critical) {
          unawaited(_triggerEmergencyOverlayForNewAlert(alert));
        }
      }
    } catch (e) {
      debugPrint("ALERTS VIEW MODEL ERROR: Couldn't fetch the data: $e");
    } finally {
      _isLoading = false;
      notifyListeners();
    }
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
        });
  }

  ///Processing update from SSE and updating the view
  Future<void> _handleIncomingSseUpdate(dynamic message) async {
    if (message is! Map<String, dynamic>) return;

    final int? id = _extractId(message['alertId']);
    if (id == null) return;

    // If there is a full object
    if (message.containsKey('subject')) {
      final alert = Alert.fromJson(message);
      _alertsCache[alert.id] = alert;

      unawaited(_triggerEmergencyOverlayForNewAlert(alert));
    }
    // If the update is partial and alert exist in cache
    else if (_alertsCache.containsKey(id)) {
      final existing = _alertsCache[id]!;
      Alert updated = existing;

      if (message.containsKey('newSeverity')) {
        final int newSevInt = message['newSeverity'] as int;
        updated = updated.copyWith(severity: AlertSeverity.values[newSevInt]);
      }
      if (message.containsKey('ack')) {
        updated = updated.copyWith(acknowledged: message['ack']);
      }
      if (message.containsKey('comment') || message.containsKey('message')) {
        updated = updated.copyWith(
          message: message['comment'] ?? message['message'],
        );
      }

      _alertsCache[id] = updated;
      unawaited(_triggerEmergencyOverlayForNewAlert(updated));
    } else {
      await fetchInitialAlerts();
      return;
    }
    await alertsRepository.saveAlertsToOfflineCache(alertsList);

    notifyListeners();
  }

  Future<void> _triggerEmergencyOverlayForNewAlert(Alert alert) async {
    final alreadyNotified = await alertsRepository.isAlertAlreadyNotified(
      alert.id,
    );

    if (alert.severity == AlertSeverity.critical && !alreadyNotified) {
      await locator<AlarmService>().showEmergencyOverlay("Alert");
      debugPrint("OVERLAY TRIGGERED");

      await alertsRepository.markAlertAsNotified(alert.id);
    }
  }

  void sortAlertsBy(String property, {bool? ascending}) {
    _currentSortProperty = property;
    if (ascending != null) _isAscending = ascending;
    notifyListeners();
  }

  ///Sending ack via repository and Optimistic UI change
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
      debugPrint(
        "ALERT VIEW MODEL ERROR: Error during ACK, reverting changes $e",
      );
    }
  }

  Future<AlertAction?> getLatestActionForAlert(int alertId) async {
    return await alertsRepository.getLatestActionForAlert(alertId);
  }

  int? _extractId(dynamic rawId) {
    if (rawId == null) return null;
    if (rawId is int) return rawId;
    return int.tryParse(rawId.toString());
  }

  @override
  void dispose() {
    _sseSubscription?.cancel();
    super.dispose();
  }
}
