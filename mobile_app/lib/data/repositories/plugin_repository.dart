import 'package:alert_app/data/datasources/plugin_remote_data_source.dart';
import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';

///Getting "my plugins" data from remote source
class PluginRepository extends ChangeNotifier {
  final Dio dio;
  final PluginRemoteDataSource pluginRemoteDataSource;

  PluginRepository({required this.dio, required this.pluginRemoteDataSource});

  ///Getting all plugins from backend and exposing the method to higher layers
  Future<List<Plugin>> fetchAllPlugins() async {
    return pluginRemoteDataSource.getAllPlugins();
  }

  ///Forcing plugin to run immediately once with optional arguments
  Future<String> forcePlugin(Plugin plugin, {String? arguments}) async {
    final String fileName = plugin.fileName;
    final String extension = plugin.language.value;

    final String fullFileName = '$fileName$extension';

    final Map<String, String> payload = {};
    if (arguments != null && arguments.isNotEmpty) {
      payload['arguments'] = arguments;
    }

    try {
      final response = await pluginRemoteDataSource.runPlugin(
        fullFileName,
        payload: payload,
      );
      return response;
    } catch (e) {
      debugPrint(
        'PLUGIN REPOSITORY - Error while forcing plungin, error message: $e',
      );
      return '';
    }
  }

  ///Modifying plugin run options and setting it as active or inactive
  Future<bool> activatePluginWithCron({
    required String fileName,
    required String extension,
    required String cronExpression,
    required bool active,
  }) async {
    final String fullFileName = '$fileName$extension';

    final response = await pluginRemoteDataSource.activatePluginWithCron(
      fullFileName,
      cronExpression,
      active,
    );

    if (response) {
      return true;
    } else {
      debugPrint('PLUGIN REPOSITORY - Activating plugin with Cron failed');
      return false;
    }
  }
}
