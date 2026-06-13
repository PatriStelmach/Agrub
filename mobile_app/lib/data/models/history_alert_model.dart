import 'package:flutter/material.dart';

// Using Enhanced enums to easily print normal strings in views
enum AlertSeverity {
  unknown('UNKNOWN'),
  info('INFO'),
  low('LOW'),
  medium('MEDIUM'),
  high('HIGH'),
  critical('CRITICAL');

  final String label;
  const AlertSeverity(this.label);
}

enum AlertStatus { sent, inProgress, done }

class HistoryAlert {
  final int id;
  final String subject;
  final String source;
  final AlertSeverity severity;
  final AlertStatus status;
  final DateTime createdAt;
  final DateTime closedAt;
  final String message;
  final String? author;
  final bool acknowledged;

  HistoryAlert({
    required this.id,
    required this.subject,
    required this.source,
    required this.severity,
    required this.status,
    required this.createdAt,
    required this.closedAt,
    this.message = '',
    this.author,
    required this.acknowledged,
  });

  Color severityColor(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    switch (severity) {
    //oklch(0.632 0.254 21.972)
      case AlertSeverity.critical:
        return const Color(0xFFF40031);
    //  oklch(70.5% 0.213 47.604)
      case AlertSeverity.high:
        return isDark ? const Color(0xFFFE9A00) : const Color(0xFFE98600);
    //  oklch(76.9% 0.290 70.08)
      case AlertSeverity.medium:
        return isDark ? const Color(0xFFFFDF20) : const Color(0xFFD6B900);
    //  oklch(90.5% 0.182 98.111)
      case AlertSeverity.low:
        return isDark? const Color(0xFF48CF00) : const Color(0xFF08A800);
    // oklch(0.731 0.255 137.694)
      case AlertSeverity.info:
        return isDark? const Color(0xFF61C8FF) : const Color(0xFF00A3FF);

      case AlertSeverity.unknown:
      default:
      // Tryb ciemny oklch(0.70 0.14 242) Jasny oklch(0.79 0.14 242)
        return isDark ? const Color(0xFF314158) : const Color(0xFFE2E8F0);
    }
  }

  HistoryAlert copyWith({
    int? id,
    String? subject,
    String? source,
    AlertSeverity? severity,
    AlertStatus? status,
    DateTime? createdAt,
    DateTime? closedAt,
    String? message,
    String? author,
    bool? acknowledged,
  }) {
    return HistoryAlert(
      id: id ?? this.id,
      subject: subject ?? this.subject,
      source: source ?? this.source,
      severity: severity ?? this.severity,
      status: status ?? this.status,
      createdAt: createdAt ?? this.createdAt,
      closedAt: closedAt ?? this.closedAt,
      message: message ?? this.message,
      author: author ?? this.author,
      acknowledged: acknowledged ?? this.acknowledged,
    );
  }

  factory HistoryAlert.fromJson(Map<String, dynamic> json) {
    //Utility function for parsing int ( taking in both int and String types)
    int asInt(dynamic value, {int defaultValue = 0}) {
      if (value == null) return defaultValue;
      if (value is int) return value;
      return int.tryParse(value.toString()) ?? defaultValue;
    }

    // Severity mapping to enum
    int sevIndex = asInt(json['severity']);
    if (sevIndex < 0 || sevIndex >= AlertSeverity.values.length) {
      sevIndex = AlertSeverity.values.length - 1;
    }
    final sev = AlertSeverity.values[sevIndex];

    // Status mapping to enum
    final String rawStatus = (json['status'] ?? 'sent').toString().toLowerCase();
    AlertStatus stat;
    switch (rawStatus) {
      case 'inprogress':
      case 'in_progress':
        stat = AlertStatus.inProgress;
        break;
      case 'done':
        stat = AlertStatus.done;
        break;
      default:
        stat = AlertStatus.sent;
    }

    // Building object
    return HistoryAlert(
      id: asInt(json['id']),
      subject: json['subject']?.toString() ?? '',
      source: json['source']?.toString() ?? 'System',
      severity: sev,
      // Prepared for createdAt from FCM (comes as String), DateTime.parse will handle it
      createdAt:
      DateTime.tryParse(json['createdAt']?.toString() ?? '') ??
          DateTime.now(),
      closedAt:
      DateTime.tryParse(json['closedAt']?.toString() ?? '') ??
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
      'closedAt' : closedAt.toIso8601String(),
      'message': message,
      'author': author,
      'acknowledged': acknowledged,
    };
  }
}
