import 'package:alert_app/data/models/plugin_model.dart';
import 'package:flutter/material.dart';
import 'package:dio/dio.dart';


class PluginsRepository extends ChangeNotifier {


final Map<String, Plugin> pluginsCache = {};

final Dio dio;
PluginsRepository({required this.dio});
//MOCK: getting alerts from local JSON
//FINAL: updating full list via REST when opening the app
Future<void> updateAllPlugins() async {

  try {
    
List<Plugin> allPlugins = [];
    int currentPage = 0;
    bool hasMorePages = true;

    while (hasMorePages) {
      final response = await dio.get(
        'http://10.0.2.2:10000/api/plugins/library',
        queryParameters: {
          'page': currentPage,
          'size': 20, 
        },
      );

    //mock version:
    //final String response = await rootBundle.loadString('assets/mocks/plugins.json');
    //final List<dynamic> decodedData = jsonDecode(response);

//final response = await dio.get('http://10.0.2.2:10000/api/plugins/library');
final Map<String, dynamic> responseData = response.data as Map<String, dynamic>;
   
final List<dynamic> data = responseData['content'] ?? [];

final List<Plugin> pagePlugins = data.map((item) {
          return Plugin.fromJson(item as Map<String, dynamic>);
        }).toList();
        
        allPlugins.addAll(pagePlugins);

int totalPages = responseData['totalPages'] ?? 0;
currentPage++;

if (currentPage >= totalPages) {
          hasMorePages = false;
        }
       else {
        hasMorePages = true; 
      }
    }

  for (var plugin in allPlugins) {
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

Future<void> stopPlugin(String id) async {
  

  
 print("Plugin stopped");
  

}



}


