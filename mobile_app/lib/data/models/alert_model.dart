import 'package:flutter/material.dart';
import 'dart:convert';

//Below model of Alert will have to be changed to match alert info available from API

enum AlertSeverity { info, low, medium, high, extreme }
enum AlertStatus { problem, acknowledged, resolved }

class Alert {
  final String id;
  final String title;
  final String hostName;
  final String ipAddress;
  final AlertSeverity severity;
  final AlertStatus status;
  final DateTime createdAt;
  final String description;
  final String source;

  Alert({
    required this.id,
    required this.title,
    required this.hostName,
    required this.ipAddress,
    required this.severity,
    required this.status,
    required this.createdAt,
    this.description = '',
    required this.source,
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

    print("Mapowanie alertu: ${json['id']} - tytuł: ${json['title']}");
    return Alert(
    id: json['id'],
    title: json['title'],
    hostName: json['hostName'],
    ipAddress: json['ipAddress'],
    severity: AlertSeverity.values.byName(json['severity']),
    createdAt: DateTime.parse(json['createdAt']),
    status: AlertStatus.values.byName(json['status']),
    description: json['description'],
    source: json['source']

    );
  } 
}

