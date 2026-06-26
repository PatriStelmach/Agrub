import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';

class PluginsViewModel extends ChangeNotifier {
  final PluginRepository pluginsRepository;

  final Map<int, Plugin> _pluginsCache = {};

  bool _isLoading = false;
  String _currentSortProperty = 'fileName';
  bool _isAscending = true;

  bool get isLoading => _isLoading;
  String get currentSortProperty => _currentSortProperty;
  bool get isAscending => _isAscending;
  List<Plugin> get pluginsList => _pluginsCache.values.toList();

  PluginsViewModel({required this.pluginsRepository});

  ///Fetching plugins from remote source
  Future<void> fetchPlugins() async {
    _isLoading = true;
    notifyListeners();

    final plugins = await pluginsRepository.fetchAllPlugins();

    _pluginsCache.clear();
    for (var plugin in plugins) {
      _pluginsCache[plugin.id] = plugin;
    }

    _isLoading = false;
    notifyListeners();
  }

  ///Sorting method using a sorting factor
  void sortPluginsBy(String property, bool ascending) {
    _currentSortProperty = property;
    _isAscending = ascending;
    sortPlugins(property, ascending);
    notifyListeners();
  }

  ///Method sorting plugins by certain fields, ascending and descending change using compareTo() result values
  List<Plugin> sortPlugins(String sortProperty, bool ascending) {
    final List<Plugin> sortedList = _pluginsCache.values.toList();

    sortedList.sort((a, b) {
      int compareToValue;
      switch (sortProperty) {
        case 'fileName':
          compareToValue = a.fileName.compareTo(b.fileName);
        case 'creator':
          compareToValue = a.language.index.compareTo(b.language.index);
        case 'language':
          compareToValue = a.language.index.compareTo(b.language.index);
        case 'active':
          final valueA = a.active ? 1 : 0;
          final valueB = b.active ? 1 : 0;
          compareToValue = valueA.compareTo(valueB);

        default:
          compareToValue = a.id.compareTo(b.id);
      }

      return ascending ? compareToValue : -compareToValue;
    });

    return sortedList;
  }

  ///Calling force plugin method
  Future<void> startPlugin(Plugin plugin, {String? arguments}) async {
    await pluginsRepository.forcePlugin(plugin, arguments: arguments);
  }

  ///Method saving Cron settings
  Future<bool> saveCronSettings({
    required Plugin plugin,
    required String cronExpression,
    required bool isActive,
  }) async {
    final success = await pluginsRepository.activatePluginWithCron(
      fileName: plugin.fileName,
      extension: plugin.language.value,
      cronExpression: cronExpression,
      active: isActive,
    );

    if (success) {
      await fetchPlugins();
    }

    return success;
  }
}
