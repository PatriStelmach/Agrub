import 'dart:async';
import 'dart:convert';
import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/data/models/problem_action_model.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:alert_app/locator.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/data/services/navigation_service.dart';
import 'package:flutter_client_sse/constants/sse_request_type_enum.dart';
import 'package:flutter_client_sse/flutter_client_sse.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AlertRepository extends ChangeNotifier {
  final Dio dio = locator<Dio>();
  final Map<int, Alert> alertsCache = {};
  //remembering extreme alerts that triggered alert screen
  final Set<int> notifiedAlertIds = {};
  final _storage = const FlutterSecureStorage();

  StreamSubscription? _sseSubscription;

  DateTime lastPing = DateTime.now();

  int _mapSeverityToInt(AlertSeverity severity) {
    switch (severity) {
      case AlertSeverity.extreme:
        return 5;
      case AlertSeverity.high:
        return 4;
      case AlertSeverity.medium:
        return 3;
      case AlertSeverity.low:
        return 2;
      case AlertSeverity.lowest:
        return 1;
      case AlertSeverity.info:
        return 0;
    }
  }

  AlertSeverity _mapIntToSeverity(int value) {
    switch (value) {
      case 5:
        return AlertSeverity.extreme;
      case 4:
        return AlertSeverity.high;
      case 3:
        return AlertSeverity.medium;
      case 2:
        return AlertSeverity.low;
      case 1:
        return AlertSeverity.lowest;
      case 0:
        return AlertSeverity.info;
      default:
        return AlertSeverity.info;
    }
  }

  Future<void> initSseConnection() async {
    final userRepo = locator<UserRepository>();
    final String userGroup = await userRepo.getCurrentUserGroup();
    final String sseUrl =
        '${dio.options.baseUrl}/api/alerts/stream?groups=$userGroup';
    _sseSubscription?.cancel();

    String? token = await _storage.read(key: 'jwt_token');

    if (token == null) {
      debugPrint("SSE ERROR: No JWT Token.");
      return;
    }

    // KK:pobranie tokenu

    _sseSubscription =
        SSEClient.subscribeToSSE(
          method: SSERequestType.GET,
          url: sseUrl,
          header: {
            "Accept": "text/event-stream",
            "Cache-Control": "no-cache",
            "Authorization": "Bearer $token",
          },
        ).listen((event) {
          debugPrint("SSE Raw Event: ${event.data}");
          if (event.data == null || event.data!.isEmpty) return;

          final String rawData = event.data!.trim();
          debugPrint("SSE Raw Event: $rawData");

          if (!rawData.startsWith('{') && !rawData.startsWith('[')) {
            debugPrint("Text message: $rawData");
            return;
          }

          try {
            // Parsing String -> Map
            final Map<String, dynamic> decodedData = jsonDecode(event.data!);
            final String eventType = decodedData['eventType'] ?? '';
            final dynamic message = decodedData['message'];

            // 2. Logika "Dystrybutora" zdarzeń
            if (eventType == 'ALERT_UPDATE' ||
                eventType == 'ALERT_UPDATE_ONLY') {
              handleSingleAlertUpdate(message);
            } else {
              debugPrint("Otrzymano inny typ zdarzenia: $eventType");
            }
          } catch (e) {
            debugPrint("DEBUG: SSE prasing error: $e");
          }
        });
  }

  //MOCK: getting alerts from local JSON
  //FINAL: updating full list via REST when opening the app
  Future<void> updateAllAlerts() async {
    final response = await dio.get('/api/alerts/active');

    final List<dynamic> data = response.data;

    final List<Alert> parsedAlerts = data.map((item) {
      return Alert.fromJson(item as Map<String, dynamic>);
    }).toList();

    alertsCache.clear();

    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('offline_alerts_cache');

    _processAlerts(parsedAlerts);
  }

  Future<void> handleSingleAlertUpdate(dynamic message) async {
    debugPrint("SSE Message: $message");
    if (message is! Map<String, dynamic>) return;

    //FCM miał problem, niżej bezpieczne parsowanie tego przyszło przez FCM
    int? id;
    if (message['alertId'] != null) {
      if (message['alertId'] is int) {
        id = message['alertId'] as int;
      } else {
        id = int.tryParse(message['alertId'].toString());
      }
    }

    // Jeśli to pełny obiekt Alert (ma pole subject)
    if (message.containsKey('subject')) {
      final alert = Alert.fromJson(message);
      await _processAlerts([alert]);
      return;
    }

    // Jeśli to częściowy update i mamy go w cache
    if (id != null && alertsCache.containsKey(id)) {
      final existing = alertsCache[id]!;
      Alert updated = existing;

      // Update Severity
      if (message.containsKey('newSeverity')) {
        updated = updated.copyWith(
          severity: _mapIntToSeverity(message['newSeverity']),
        );
      } else {
        updated = updated.copyWith(severity: existing.severity);
      }

      // Update ACK status
      if (message.containsKey('ack')) {
        updated = updated.copyWith(acknowledged: message['ack']);
      }

      // Update Komentarza (obsługa comment z endpointu ack)
      if (message.containsKey('comment') || message.containsKey('message')) {
        updated = updated.copyWith(
          message: message['comment'] ?? message['message'],
        );
      }

      alertsCache[id] = updated;
      notifyListeners();
    } else if (id != null) {
      // Nie mamy w cache? Pobieramy listę, by być up-to-date
      updateAllAlerts();
    }
  }

  Future<void> _processAlerts(List<Alert> incomingAlerts) async {
    debugPrint("Repo: Procesuję ${incomingAlerts.length} alertów");

    // Inicjalizujemy SharedPreferences na początku metody
    final prefs = await SharedPreferences.getInstance();

    // pobieranie alertów które już były
    List<String> persistentNotifiedIds =
        prefs.getStringList('notified_alert_ids') ?? [];
    // Pobieramy aktualny cache offline alertów
    List<String> offlineAlertsCache =
        prefs.getStringList('offline_alerts_cache') ?? [];

    bool alarmOverlayTrigger = false;

    for (var alert in incomingAlerts) {
      alertsCache[alert.id] = alert;

      String alertJson = jsonEncode(alert.toJson());

      offlineAlertsCache.removeWhere((item) {
        final Map<String, dynamic> m = jsonDecode(item);
        return m['id'] == alert.id;
      });
      offlineAlertsCache.add(alertJson);

      debugPrint("DEBUG: Analysis of Alert ID: ${alert.id}");
      debugPrint("  DEBUG: Show severity: ${alert.severity}");

      bool alreadyNotified = persistentNotifiedIds.contains(
        alert.id.toString(),
      );
      debugPrint(
        "  DEBUG: Is ID already in persistent notified list? $alreadyNotified",
      );

      if (alert.severity == AlertSeverity.extreme && !alreadyNotified) {
        debugPrint("DEBUG: Alert is new");

        if (!notifiedAlertIds.contains(alert.id)) {
          notifiedAlertIds.add(alert.id);
        }

        persistentNotifiedIds.add(alert.id.toString());

        alarmOverlayTrigger = true;
      } else {
        debugPrint("DEBUG: Alert is not new (lub nie jest extreme)");
      }
    }

    await prefs.setStringList('notified_alert_ids', persistentNotifiedIds);
    await prefs.setStringList('offline_alerts_cache', offlineAlertsCache);

    if (alarmOverlayTrigger) {
      debugPrint("  DEBUG: Calling showEmergencyOverlay() method");
      navigationService.showEmergencyOverlay();
    } else {
      debugPrint("  DEBUG: No new extreme alerts.");
    }

    notifyListeners();
    debugPrint("--- [DEBUG END] ---");
  }

  Future<void> sendAcknowledge(
    int alertId, {
    String? comment,
    bool isAck = true,
    int? newSeverity,
  }) async {
    final String actionType = isAck ? "ACK" : "COMMENT";
    final String commentText = comment ?? "";

    final userRepo = locator<UserRepository>();
    final currentUser = await userRepo.getCurrentUser();
    //jakby sesja wygasła
    final String authorName = currentUser?.login ?? 'Unknown Mobile';

    final originalAlert = alertsCache[alertId];

    final int currentSeverityInt = originalAlert != null
        ? _mapSeverityToInt(originalAlert.severity)
        : 0;
    final int finalSeverityInt = newSeverity ?? currentSeverityInt;
    final AlertSeverity finalEnumSeverity = AlertSeverity
        .values[finalSeverityInt.clamp(0, AlertSeverity.values.length - 1)];

    if (originalAlert != null) {
      alertsCache[alertId] = originalAlert.copyWith(
        acknowledged: isAck,
        severity: finalEnumSeverity,
        message: commentText.isNotEmpty ? commentText : originalAlert.message,
      );
      notifyListeners();
    }

    final Map<String, dynamic> requestBody = {
      "author": authorName,
      "message": commentText,
      "newSeverity": finalSeverityInt,
      "ack": isAck,
    };

    try {
      final response = await dio.post(
        '/api/alerts/$alertId/ack',
        data: requestBody,
      );

      if (response.statusCode == 200) {
        debugPrint('ACK SUCCESS: Alert $alertId acknowledged.');
        await updateAllAlerts();
      }
    } on DioException catch (e) {
      debugPrint('--- [Detailed Error Log] ---');
      debugPrint('Status Code: ${e.response?.statusCode}');
      debugPrint('Response Data: ${e.response?.data}');
      debugPrint('Headers: ${e.response?.headers}');
    } catch (e) {
      debugPrint('ACK UNKNOWN ERROR: $e');
    }
  }

  Future<void> markAlertAsNotified(int alertId) async {
    final prefs = await SharedPreferences.getInstance();

    // Pobieramy listę już wykrzyczanych ID (jako Stringi)
    List<String> notifiedIds = prefs.getStringList('notified_alert_ids') ?? [];

    // Jeśli tego ID jeszcze nie ma na liście, dodajemy go
    if (!notifiedIds.contains(alertId.toString())) {
      notifiedIds.add(alertId.toString());
      await prefs.setStringList('notified_alert_ids', notifiedIds);
      debugPrint("Zapisano ID $alertId jako alarmowany.");
    }
  }

  Future<bool> isAlertAlreadyNotified(int alertId) async {
    final prefs = await SharedPreferences.getInstance();
    List<String> notifiedIds = prefs.getStringList('notified_alert_ids') ?? [];
    return notifiedIds.contains(alertId.toString());
  }

  Future<void> syncCacheWithSharedPreferences() async {
    final prefs = await SharedPreferences.getInstance();
    List<String> cachedAlertsRaw =
        prefs.getStringList('offline_alerts_cache') ?? [];

    debugPrint(
      "Repo:  Znaleziono ${cachedAlertsRaw.length} alertów w pamięci offline.",
    );

    if (cachedAlertsRaw.isEmpty) {
      alertsCache.clear();
      notifyListeners();
      return;
    }

    // Czyścimy stary RAM, aby idealnie odzwierciedlić stan, który przed chwilą zapisał proces w tle
    alertsCache.clear();

    for (String rawJson in cachedAlertsRaw) {
      try {
        Map<String, dynamic> alertMap = jsonDecode(rawJson);
        Alert alert = Alert.fromJson(alertMap);

        // Wpychamy bezpośrednio do cache w RAM
        alertsCache[alert.id] = alert;
      } catch (e) {
        debugPrint(
          "Repo ERROR: Błąd dekodowania alertu podczas synchronizacji: $e",
        );
      }
    }

    debugPrint("Repo: Pamięć RAM zaktualizowana stanem z dysku.");
    notifyListeners();
  }

  Future<ProblemAction?> getLatestActionForAlert(int alertId) async {
    try {
      final response = await dio.get('/api/alerts/$alertId/actions');

      if (response.data is List) {
        final List<dynamic> data = response.data as List<dynamic>;

        if (data.isNotEmpty) {
          return ProblemAction.fromJson(data.first as Map<String, dynamic>);
        }
      }
      return null;
    } on DioException catch (e) {
      debugPrint(
        "ALERT DEBUG: Issue when getting history for $alertId: ${e.message}",
      );
      return null;
    } catch (e) {
      debugPrint("ALERT DEBUG UNKNOWN ERROR: $e");
      return null;
    }
  }
}
