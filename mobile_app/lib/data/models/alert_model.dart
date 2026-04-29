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

//severity int to severity enum
int sevInt = json['severity'] ?? 0;
int sevIndex = json['severity'] is int ? json['severity'] : 0;
  if (sevIndex >= AlertSeverity.values.length) {
    sevIndex = AlertSeverity.values.length - 1; // Ustaw extreme jeśli wyjdzie poza zakres
  }
  final sev = AlertSeverity.values[sevIndex];

  // String status to enum status
  String rawStatus = (json['status'] ?? 'sent').toString().toLowerCase();
  AlertStatus stat = AlertStatus.sent;
  if (rawStatus == 'sent') stat = AlertStatus.sent;
  if (rawStatus == 'inprogress') stat = AlertStatus.inProgress;
  if (rawStatus == 'done') stat = AlertStatus.done;




    return Alert(
    id: json['id'],
    subject: json['subject'],
    source: json['source'],
    severity: sev,
    createdAt: DateTime.parse(json['createdAt']),
    status: stat,
    message: json['message'],
  

    );
  } 
}

