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
      case AlertSeverity.critical:
        return const Color(0xFFF40031);
      case AlertSeverity.high:
        return isDark ? const Color(0xFFFE9A00) : const Color(0xFFE98600);
      case AlertSeverity.medium:
        return isDark ? const Color(0xFFFFDF20) : const Color(0xFFD6B900);
      case AlertSeverity.low:
        return isDark ? const Color(0xFF48CF00) : const Color(0xFF08A800);
      case AlertSeverity.info:
        return isDark ? const Color(0xFF61C8FF) : const Color(0xFF00A3FF);

      case AlertSeverity.unknown:
        return isDark ? const Color(0xFF314158) : const Color(0xFFE2E8F0);
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
    int rawSeverity = json['severity'];
    if (rawSeverity < 0 || rawSeverity >= AlertSeverity.values.length) {
      rawSeverity = AlertSeverity.values.length - 1;
    }
    final decodedSeverity = AlertSeverity.values[rawSeverity];

    final String rawStatus = (json['status'] ?? 'sent')
        .toString()
        .toLowerCase();
    AlertStatus decodedStatus;
    switch (rawStatus) {
      case 'inprogress':
      case 'in_progress':
        decodedStatus = AlertStatus.inProgress;
        break;
      case 'done':
        decodedStatus = AlertStatus.done;
        break;
      default:
        decodedStatus = AlertStatus.sent;
    }

    return Alert(
      id: json['id'],
      subject: json['subject']?.toString() ?? '',
      source: json['source']?.toString() ?? 'System',
      severity: decodedSeverity,
      //Additional parsing is here because of FCM date formatting
      createdAt:
          DateTime.tryParse(json['createdAt']?.toString() ?? '') ??
          DateTime.now(),
      status: decodedStatus,
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
      'status': status.name,
      'createdAt': createdAt.toIso8601String(),
      'message': message,
      'author': author,
      'acknowledged': acknowledged,
    };
  }
}
