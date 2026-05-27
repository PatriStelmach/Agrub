import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';

class PluginsViewModel extends ChangeNotifier {
  final PluginRepository _repository;

  String _currentSortProperty = 'fileName';
  bool _isAscending = true;

  String get currentSortProperty => _currentSortProperty;
  bool get isAscending => _isAscending;

  //List<Plugin> sortedPlugins = [];

  PluginsViewModel({required PluginRepository pluginsRepository})
    : _repository = pluginsRepository {
    _repository.addListener(notifyListeners);
  }

  List<Plugin> get pluginList => _repository.pluginsCache.values.toList();

  List<Plugin> get sortedPlugins {
    if (pluginList.isEmpty) return [];

    final Map<String, Comparable Function(Plugin)> pluginGetters = {
      'id': (plugin) => plugin.id,
      'fileName': (plugin) => plugin.fileName,
      'creator': (plugin) => plugin.creator,
      'language': (plugin) => plugin.language.value,
      'weight': (plugin) => plugin.weight,
      'updatedAt': (plugin) => plugin.updatedAt,
      'cronExpression': (plugin) => plugin.cronExpression,
      // Boolean nie jest bezpośrednio Comparable, więc rzutujemy go na int (1 lub 0)
      // Dzięki temu aktywne pluginy mogą być na górze lub na dole
      'active': (plugin) => plugin.active ? 1 : 0,

      'log': (plugin) => plugin.log ? 1 : 0,
    };

    final pluginGetter =
        pluginGetters[_currentSortProperty] ??
        (plugin) => plugin.fileName ?? '';
    final newList = List<Plugin>.from(pluginList);
    newList.sort((a, b) {
      final valA = pluginGetter(a);
      final valB = pluginGetter(b);

      if (_isAscending) {
        return valA.compareTo(valB);
      } else {
        return valB.compareTo(valA);
      }
    });
    return newList;

    //notifyListeners();
  }

  void sortPluginsBy(String property, {bool? ascending}) {
    _currentSortProperty = property;
    if (ascending != null) {
      _isAscending = ascending;
    }
    notifyListeners();
  }

  Future<void> loadPlugins() async {
    await _repository.updateAllPlugins();
  }

  Future<void> startPlugin(Plugin plugin, {String? arguments}) async {
    // Simple forcing of plugin execution. rudimentary error hadling
    try {
      await _repository.forcePlugin(plugin, arguments: arguments);

      notifyListeners();
    } catch (e) {
      debugPrint('Placeholder error: $e');
    }
  }
}
