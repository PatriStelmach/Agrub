import 'package:flutter/material.dart';
import 'dart:convert';

//Below model of Alert will have to be changed to match alert info available from API

enum AlertSeverity { info, low, medium, high, extreme }
enum AlertStatus { sent, inProgress, done}

class Alert {
final int id;
  final String subject;       
  final String source;    
  final AlertSeverity severity;
  final AlertStatus status;
  final DateTime createdAt;
  final String message; 
  

  Alert({
    required this.id,
    required this.subject,
    required this.source,
    required this.severity,
    required this.status,
    required this.createdAt,
    this.message = '',
  });


  Color get severityColor {
    switch (severity) {
      case AlertSeverity.extreme: return Colors.redAccent;
      case AlertSeverity.high: return Colors.orange;
      case AlertSeverity.medium: return Colors.yellow;
      case AlertSeverity.low: return Colors.green;
      default: return Colors.grey;
    }
  }


factory Alert.fromJson(Map<String,dynamic> json) {

// Pomocnicza funkcja do bezpiecznego wyciągania int (obsługuje int i String)
  int asInt(dynamic value, {int defaultValue = 0}) {
    if (value == null) return defaultValue;
    if (value is int) return value;
    return int.tryParse(value.toString()) ?? defaultValue;
  }

  // 1. Obsługa Severity (Index -> Enum)
  int sevIndex = asInt(json['severity']);
  if (sevIndex < 0 || sevIndex >= AlertSeverity.values.length) {
    sevIndex = AlertSeverity.values.length - 1; // Fallback na Extreme
  }
  final sev = AlertSeverity.values[sevIndex];

  // 2. Obsługa Statusu (String -> Enum)
  String rawStatus = (json['status'] ?? 'sent').toString().toLowerCase();
  AlertStatus stat;
  switch (rawStatus) {
    case 'inprogress':
    case 'in_progress': // Na wypadek literówek w backendzie
      stat = AlertStatus.inProgress;
      break;
    case 'done':
      stat = AlertStatus.done;
      break;
    default:
      stat = AlertStatus.sent;
  }

  // 3. Budowa obiektu
  return Alert(
    id: asInt(json['id']),
    subject: json['subject']?.toString() ?? '',
    source: json['source']?.toString() ?? 'System',
    severity: sev,
    // createdAt z FCM też przyjdzie jako String, DateTime.parse sobie z tym poradzi
    createdAt: DateTime.tryParse(json['createdAt']?.toString() ?? '') ?? DateTime.now(),
    status: stat,
    message: json['message']?.toString() ?? '',
  );
  } 
}

