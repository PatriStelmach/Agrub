import 'package:alert_app/data/models/plugin_model.dart';
import 'package:alert_app/data/services/alarm_service.dart';
import 'package:alert_app/locator.dart';
import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';

class PluginRemoteDataSource {
  final Dio dio;

  PluginRemoteDataSource({required this.dio});

  ///Getting "My plugins" list from backend
  Future<List<Plugin>> getAllPlugins() async {
    try {
      final response = await dio.get('/api/local-scripts/list');
      final List<dynamic> rawData = response.data as List<dynamic>;

      return rawData
          .map((plugin) => Plugin.fromJson(plugin as Map<String, dynamic>))
          .toList();
    } catch (e) {
      await locator<AlarmService>().showEmergencyOverlay('Connection');
      debugPrint(
        "PLUGIN REMOTE DATA SOURCE - Connection error - getAllPlugins() failed",
      );

      rethrow;
    }
  }

  /// Running specified plugin with potential arguments
  Future<String> runPlugin(
    String fullFileName, {
    Map<String, String>? payload,
  }) async {
    try {
      final fileName = fullFileName;
      final response = await dio.post(
        '/api/local-scripts/$fileName/run',
        data: payload,
      );
      return response.data.toString();
    } catch (e) {
      await locator<AlarmService>().showEmergencyOverlay('Connection');
      debugPrint(
        "PLUGIN REMOTE DATA SOURCE - Connection error - runPlugin() failed",
      );
      rethrow;
    }
  }

  ///Activating plugin with given Cron expression
  Future<bool> activatePluginWithCron(
    String fileName,
    String cronExpression,
    bool active,
  ) async {
    try {
      final response = await dio.put(
        '/api/local-scripts/$fileName/edit',
        data: {'cronExpression': cronExpression, 'active': active},
      );

      return response.data;
    } catch (e) {
      debugPrint(
        "PLUGIN REMOTE DATA SOURCE - Connection error - activatePluginWithCrone() failed",
      );
      return false;
    }
  }
}
