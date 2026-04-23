import 'dart:convert';

import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:alert_app/services/navigation_service.dart';


class PluginsRepository extends ChangeNotifier {


final Map<String, Plugin> pluginsCache = {};


//MOCK: getting alerts from local JSON
//FINAL: updating full list via REST when opening the app
Future<void> updateAllPlugins() async {

     final String response = await rootBundle.loadString('assets/mocks/plugins.json');
    final List<dynamic> decodedData = jsonDecode(response);
    final List<Plugin> parsedPlugins = decodedData.map((item) {
      return Plugin.fromJson(item as Map<String, dynamic>);
    }).toList();

  for (var plugin in parsedPlugins) {
      pluginsCache[plugin.fileName] = plugin;


    }

  notifyListeners();

}





// very simple placehold for sending ack
Future<void> startPlugin(String id) async {
  

  
 print("Plugin started");
  

}



}


