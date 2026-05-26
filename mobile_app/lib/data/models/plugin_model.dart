import 'package:flutter/material.dart';

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
  final int id;
  final String fileName;
  final String creator;
  final PluginLanguage language;
  final int weight;
  final List<String> tags;
  final String cronExpression;
  final String? arguments;
  final DateTime updatedAt;
  final bool active;
  final bool log;

  Plugin({
    required this.id,
    required this.fileName,
    required this.creator,
    required this.language,
    required this.weight,
    required this.tags,
    required this.cronExpression,
    this.arguments,
    required this.updatedAt,
    required this.active,
    required this.log,
  });

  Color activeColor(BuildContext context) {
    return active
        ? Theme.of(context).colorScheme.primary
        : Colors.grey.withOpacity(0.4);
  }

  factory Plugin.fromJson(Map<String, dynamic> json) {
    return Plugin(
      id: json['id'] ?? 0,
      fileName: json['fileName'] ?? 'No fileName',
      creator: json['creator'] ?? 'No creator',
      language: PluginLanguage.fromString(json['language'] ?? 'No language'),
      weight: json['weight'] ?? 0,
      tags: List<String>.from(json['tags'] ?? []),
      updatedAt: DateTime.parse(
        json['updatedAt'] ?? DateTime.now().toIso8601String(),
      ),
      active: json['active'] ?? false,
      log: json['log'] ?? false,
      cronExpression: json['cronExpression'] ?? ' ',
    );
  }
}
