import 'package:alert_app/data/models/plugin_model.dart';
import 'package:alert_app/locator.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

class PluginsRepository extends ChangeNotifier {
  final Map<String, Plugin> pluginsCache = {};

  final Dio dio = locator<Dio>();

  Future<void> updateAllPlugins() async {
    try {
      final response = await dio.get('/api/local-scripts/list');

      final List<dynamic> data = response.data as List<dynamic>;

      pluginsCache.clear();

      final List<Plugin> allPlugins = data.map((item) {
        return Plugin.fromJson(item as Map<String, dynamic>);
      }).toList();

      for (var plugin in allPlugins) {
        pluginsCache[plugin.fileName] = plugin;
      }

      notifyListeners();
      debugPrint("PLUGIN DEBUG: Loaded ${pluginsCache.length} plugin(s)");
    } catch (e) {
      debugPrint("PLUGIN DEBUG: Error when connecting to API $e");
    }
  }

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
      debugPrint("PLUGIN DEBUG: Error while running ${plugin.fileName}: $e");
      return "Run error: $e";
    }
  }

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

      if (response.statusCode == 200) {
        if (pluginsCache.containsKey(fileName)) {
          final oldPlugin = pluginsCache[fileName]!;
        }
        notifyListeners();
        updateAllPlugins();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint("PLUGIN DEBUG: Cron save error: $e");
      return false;
    }
  }

  Future<void> stopPlugin(String id) async {}
}
