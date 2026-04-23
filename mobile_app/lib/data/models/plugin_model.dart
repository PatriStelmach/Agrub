import 'package:flutter/material.dart';
import 'dart:convert';

enum PluginLanguage {
  py(".py"),
  sh(".sh"),
  bash(".bash"),
  ps1(".ps1"),
  psm1(".psm1"),
  unknown("");

  final String value;
  const PluginLanguage(this.value);

  static PluginLanguage fromString(String val) {
    return PluginLanguage.values.firstWhere(
      (e) => e.value == val,
      orElse: () => PluginLanguage.unknown,
    );
  }
}

class Plugin {

final String name;
  final String fileName;
  final String creator;
  final PluginLanguage language;
  final int weight;
  final List<String> tags;
  final String cronExpression;
  final DateTime updatedAt;
  final bool active;
  final bool log;

  Plugin({
    required this.name,
    required this.fileName,
    required this.creator,
    required this.language,
    required this.weight,
    required this.tags,
    required this.cronExpression,
    required this.updatedAt,
    required this.active,
    required this.log,
  });


  Color get activeColor {
    switch (active) {
      case true: return Colors.blue;
      case false: return Colors.grey;

    }
  }



  factory Plugin.fromJson(Map<String, dynamic> json) {
    return Plugin(
      name: json['name'] as String,
      fileName: json['fileName'] as String,
      creator: json['creator'] as String,
      language: PluginLanguage.fromString(json['language'] as String),
      weight: json['weight'] as int,
      tags: List<String>.from(json['tags'] ?? []),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      active: json['active'] as bool,
      log: json['log'] as bool,
      cronExpression: json['cronExpression'] as String,
    );
  }
}