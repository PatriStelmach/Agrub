import 'package:flutter/material.dart';

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
  final String? author;
  final bool acknowledged;
  

  Alert({
    required this.id,
    required this.subject,
    required this.source,
    required this.severity,
    required this.status,
    required this.createdAt,
    this.message = '',
    this.author,
    required this.acknowledged,
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


Alert copyWith({
    int? id,
    String? subject,
    String? source,
    AlertSeverity? severity,
    AlertStatus? status,
    DateTime? createdAt,
    String? message,
    String? author,
    bool? acknowledged,
  }) {
    return Alert(
      id: id ?? this.id,
      subject: subject ?? this.subject,
      source: source ?? this.source,
      severity: severity ?? this.severity,
      status: status ?? this.status,
      createdAt: createdAt ?? this.createdAt,
      message: message ?? this.message,
      author: author ?? this.author,
      acknowledged: acknowledged ?? this.acknowledged,
    );
  }


factory Alert.fromJson(Map<String,dynamic> json) {

//Utility function for parsing int ( taking in both int and String types)
  int asInt(dynamic value, {int defaultValue = 0}) {
    if (value == null) return defaultValue;
    if (value is int) return value;
    return int.tryParse(value.toString()) ?? defaultValue;
  }

  // Severity mapping to enum
  int sevIndex = asInt(json['severity']);
  if (sevIndex < 0 || sevIndex >= AlertSeverity.values.length) {
    sevIndex = AlertSeverity.values.length - 1; // Fallback na Extreme
  }
  final sev = AlertSeverity.values[sevIndex];

  // Status mapping to enum
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


  // Building object
  return Alert(
    id: asInt(json['id']),
    subject: json['subject']?.toString() ?? '',
    source: json['source']?.toString() ?? 'System',
    severity: sev,
    // createdAt z FCM też przyjdzie jako String, DateTime.parse sobie z tym poradzi
    createdAt: DateTime.tryParse(json['createdAt']?.toString() ?? '') ?? DateTime.now(),
    status: stat,
    message: json['message']?.toString() ?? '',
    acknowledged: json['acknowledged'] ?? false, 
  );
  } 
}

