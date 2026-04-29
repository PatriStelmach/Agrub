import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';

class PluginsViewModel extends ChangeNotifier {

final PluginsRepository pluginsRepository;

PluginsViewModel({required this.pluginsRepository});

List<Plugin> sortedPlugins = [];

List<Plugin> get pluginList => pluginsRepository.pluginsCache.values.toList();


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
newList.sort((a,b) => pluginGetter(a).compareTo(pluginGetter(b)));
sortedPlugins = newList;

notifyListeners();
}





Future<void> startPlugin(String pluginFileName) async {

// VERY simple function for sending ack via repository function. rudimentary error hadling
  try {
    await pluginsRepository.startPlugin(pluginFileName);
    
    notifyListeners();
  } catch (e) {
      print('Placeholder error: $e');

  }
}

Future<void> stopPlugin(String pluginFileName) async {

// VERY simple function for sending ack via repository function. rudimentary error hadling
  try {
    await pluginsRepository.startPlugin(pluginFileName);
    
    notifyListeners();
  } catch (e) {
      print('Placeholder error: $e');

  }
}

}