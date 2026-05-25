import 'package:flutter/material.dart';

enum AlertSeverity { info, lowest, low, medium, high, extreme }

enum AlertStatus { sent, inProgress, done }

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

  Color severityColor(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    switch (severity) {
      //oklch(0.632 0.254 21.972)
      case AlertSeverity.extreme:
        return const Color(0xFFFC2E41);
      //  oklch(70.5% 0.213 47.604)
      case AlertSeverity.high:
        return const Color(0xFFFF7B2E);
      //  oklch(76.9% 0.290 70.08)
      case AlertSeverity.medium:
        return const Color(0xFFFFB400);
      //  oklch(90.5% 0.182 98.111)
      case AlertSeverity.low:
        return const Color(0xFFE5FA31);
      // oklch(0.731 0.255 137.694)
      case AlertSeverity.lowest:
        return const Color(0xFF00FA54);

      case AlertSeverity.info:
      default:
        // Tryb ciemny oklch(0.70 0.14 242) Jasny oklch(0.79 0.14 242)
        return isDark ? const Color(0xFF4DB0FF) : const Color(0xFF8AD2FF);
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

  factory Alert.fromJson(Map<String, dynamic> json) {
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
      createdAt:
          DateTime.tryParse(json['createdAt']?.toString() ?? '') ??
          DateTime.now(),
      status: stat,
      message: json['message']?.toString() ?? '',
      acknowledged: json['acknowledged'] is bool
          ? json['acknowledged']
          : json['acknowledged']?.toString().toLowerCase() == 'true',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'subject': subject,
      'source': source,
      'severity': AlertSeverity.values.indexOf(severity),
      'status': status.toString().split('.').last,
      'createdAt': createdAt.toIso8601String(),
      'message': message,
      'author': author,
      'acknowledged': acknowledged,
    };
  }
}
