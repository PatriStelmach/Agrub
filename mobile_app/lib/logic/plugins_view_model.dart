import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';

class PluginsViewModel extends ChangeNotifier {
  final PluginRepository _repository;

  List<Plugin> _plugins = [];
  List<Plugin> _sortedPlugins = [];
  bool _isLoading = false;
  String? _errorMessage;
  String _currentSortProperty = 'fileName';
  bool _isAscending = true;

  List<Plugin> get sortedPlugins => _sortedPlugins;
  bool get isLoading => _isLoading;
  String? get errorMessage => _errorMessage;
  String get currentSortProperty => _currentSortProperty;
  bool get isAscending => _isAscending;
  PluginsViewModel({required PluginRepository pluginsRepository})
    : _repository = pluginsRepository;

  Future<void> loadPlugins() async {
    _setLoading(true);
    _clearError();

    try {
      _plugins = await _repository.fetchAllPlugins();
      _applySorting();
    } catch (e) {
      _errorMessage = "Nie udało się załadować wtyczek: $e";
      notifyListeners();
    } finally {
      _setLoading(false);
    }
  }

  ///Sorting method using a sorting factor
  void sortPluginsBy(String property, {bool? ascending}) {
    _currentSortProperty = property;
    if (ascending != null) {
      _isAscending = ascending;
    }
    _applySorting();
  }

  /// Private method for sorting
  void _applySorting() {
    if (_plugins.isEmpty) {
      _sortedPlugins = [];
      notifyListeners();
      return;
    }

    final Map<String, Comparable Function(Plugin)> pluginGetters = {
      'id': (plugin) => plugin.id,
      'fileName': (plugin) => plugin.fileName,
      'creator': (plugin) => plugin.creator,
      'language': (plugin) => plugin.language.value,
      'weight': (plugin) => plugin.weight,
      'updatedAt': (plugin) => plugin.updatedAt,
      'cronExpression': (plugin) => plugin.cronExpression,
      'active': (plugin) => plugin.active ? 1 : 0,
      'log': (plugin) => plugin.log ? 1 : 0,
    };

    final pluginGetter =
        pluginGetters[_currentSortProperty] ??
        (plugin) => plugin.fileName ?? '';
    final newList = List<Plugin>.from(_plugins);
    newList.sort((a, b) {
      final valA = pluginGetter(a);
      final valB = pluginGetter(b);

      return _isAscending ? valA.compareTo(valB) : valB.compareTo(valA);
    });

    _sortedPlugins = newList;
    notifyListeners();
  }

  ///Calling force plugin method
  Future<void> startPlugin(Plugin plugin, {String? arguments}) async {
    await _repository.forcePlugin(plugin, arguments: arguments);
  }

  ///Method saving Cron settings
  Future<bool> saveCronSettings({
    required Plugin plugin,
    required String cronExpression,
    required bool isActive,
  }) async {
    final success = await _repository.activatePluginWithCron(
      fileName: plugin.fileName,
      extension: plugin.language.value,
      cronExpression: cronExpression,
      active: isActive,
    );

    if (success) {
      await loadPlugins();
    }

    return success;
  }

  void _setLoading(bool value) {
    _isLoading = value;
    notifyListeners();
  }

  void _clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
