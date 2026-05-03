import 'dart:convert';

import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:alert_app/services/navigation_service.dart';
import 'package:http/http.dart' as http;


class PluginsRepository extends ChangeNotifier {


final Map<String, Plugin> pluginsCache = {};


//MOCK: getting alerts from local JSON
//FINAL: updating full list via REST when opening the app
Future<void> updateAllPlugins() async {

  try {
    final url = Uri.parse('http://10.0.2.2:10000/api/plugins/library');

    final response = await http.get(url);


    //mock version:
   // final String response = await rootBundle.loadString('assets/mocks/plugins.json');
    final List<dynamic> decodedData = jsonDecode(response.body);
    final List<Plugin> parsedPlugins = decodedData.map((item) {
      return Plugin.fromJson(item as Map<String, dynamic>);
    }).toList();

  for (var plugin in parsedPlugins) {
      pluginsCache[plugin.fileName] = plugin;


    }

  notifyListeners();
  debugPrint("PLUGIN DEBUG: Loaded ${pluginsCache.length} plugin(s)");
  }
  
  catch (e) {
    debugPrint("PLUGIN DEBUG: Error when connecting to API $e");
  }

}





// very simple placehold for sending ack
Future<void> startPlugin(String id) async {
  

  
 print("Plugin started");
  

}



}


