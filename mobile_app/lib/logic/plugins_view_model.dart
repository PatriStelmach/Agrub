import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';

class PluginsViewModel extends ChangeNotifier {
  final PluginsRepository _repository;

  PluginsViewModel({required PluginsRepository pluginsRepository})
    : _repository = pluginsRepository {
    _repository.addListener(notifyListeners);
  }

  List<Plugin> sortedPlugins = [];

  List<Plugin> get pluginList => _repository.pluginsCache.values.toList();

  void sortPluginsBy(String property) {
    final Map<String, Comparable Function(Plugin)> pluginGetters = {
      // fileName traktujemy jako unikalny identyfikator (zamiast id)
      'id': (plugin) => plugin.id,

      'fileName': (plugin) => plugin.fileName,

      'creator': (plugin) => plugin.creator,

      // Sortowanie po nazwie rozszerzenia (String)
      'language': (plugin) => plugin.language.value,

      'weight': (plugin) => plugin.weight,

      'updatedAt': (plugin) => plugin.updatedAt,

      'cronExpression': (plugin) => plugin.cronExpression,

      // Boolean nie jest bezpośrednio Comparable, więc rzutujemy go na int (1 lub 0)
      // Dzięki temu aktywne pluginy mogą być na górze lub na dole
      'active': (plugin) => plugin.active ? 1 : 0,

      'log': (plugin) => plugin.log ? 1 : 0,
    };

    final pluginGetter = pluginGetters[property]!;

    final newList = List<Plugin>.from(pluginList);
    newList.sort((a, b) => pluginGetter(a).compareTo(pluginGetter(b)));
    sortedPlugins = newList;

    notifyListeners();
  }

  Future<void> startPlugin(Plugin plugin, {String? arguments}) async {
    // VERY simple function for sending ack via repository function. rudimentary error hadling
    try {
      await _repository.forcePlugin(plugin);

      notifyListeners();
    } catch (e) {
      debugPrint('Placeholder error: $e');
    }
  }

  // Future<void> stopPlugin(String pluginFileName, String extension) async {
  //   // VERY simple function for sending ack via repository function. rudimentary error hadling
  //   try {
  //     await pluginsRepository.startPlugin(plugin);

  //     notifyListeners();
  //   } catch (e) {
  //     print('Placeholder error: $e');
  //   }
  // }
}
