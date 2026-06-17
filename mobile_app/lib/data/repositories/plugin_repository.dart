import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

///Getting "my plugins" data from remote source
class PluginRepository extends ChangeNotifier {
  final Dio dio;

  PluginRepository({required this.dio});

  ///Getting all plugins from backend
  Future<List<Plugin>> fetchAllPlugins() async {
    try {
      final response = await dio.get('/api/local-scripts/list');
      final List<dynamic> data = response.data as List<dynamic>;

      return data.map((item) {
        return Plugin.fromJson(item as Map<String, dynamic>);
      }).toList();
    } catch (e) {
      debugPrint("PLUGIN REPOSITORY DEBUG: Error when connecting to API $e");
      rethrow;
    }
  }

  ///Forcing plugin to run immediately once
  Future<String> forcePlugin(Plugin plugin, {String? arguments}) async {
    try {
      final String fileName = plugin.fileName;
      final String extension = plugin.language.value;

      final String fullFileName = extension.startsWith('.')
          ? '$fileName$extension'
          : '$fileName.$extension';

      final Map<String, String> payload = {};
      if (arguments != null && arguments.isNotEmpty) {
        payload['arguments'] = arguments;
      }

      final response = await dio.post(
        '/api/local-scripts/$fullFileName/run',
        data: payload,
      );

      return response.data.toString();
    } catch (e) {
      debugPrint(
        "PLUGIN REPOSITORY DEBUG: Error while running ${plugin.fileName}: $e",
      );
      return "Run error: $e";
    }
  }

  ///Modifying plugin run options and setting it as active or inactive
  Future<bool> activatePluginWithCron({
    required String fileName,
    required String extension,
    required String cronExpression,
    required bool active,
  }) async {
    try {
      final String fullFileName = extension.startsWith('.')
          ? '$fileName$extension'
          : '$fileName.$extension';

      final response = await dio.put(
        '/api/local-scripts/$fullFileName/edit',
        data: {'cronExpression': cronExpression, 'active': active},
      );

      return response.statusCode == 200;
    } catch (e) {
      debugPrint("PLUGIN REPOSITORY DEBUG: Cron save error: $e");
      return false;
    }
  }
}
