import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class PluginsScreen extends StatefulWidget {
  const PluginsScreen({super.key});

  @override
  State<PluginsScreen> createState() => _PluginsScreenState();
}

class _PluginsScreenState extends State<PluginsScreen> {
 String dropDownValue = 'fileName';


  @override
  Widget build(BuildContext context) {
 final pluginsViewModel = context.watch<PluginsViewModel>();
    
if (pluginsViewModel.pluginList.isEmpty) {
    return const Scaffold( 
      body: Center(
        child: Text("No plugins found. Consider checking the button in Debug Screen!"),
      ),
    );
}
else {
  pluginsViewModel.sortPluginsBy(dropDownValue);
}

    
    return Column(
      
          children: [

            Row(
              children: [
                Text("Sort by - ", style: TextStyle(fontSize: 30)),
                DropdownButton<String>(
                            padding: EdgeInsets.all(6),
                            borderRadius: BorderRadius.all(Radius.circular(16)),
                            value: dropDownValue, 
                            icon: const Icon(Icons.menu),
                            style: const TextStyle(color: Colors.black), 
                            onChanged: (String? newValue) {
                              setState(() {
                                dropDownValue = newValue!;
                              });
                            },
                            items:const[
                            DropdownMenuItem<String>(value: 'fileName', child: Text('File Name',style:TextStyle(fontSize: 30))),
                            DropdownMenuItem<String>(value: 'creator', child: Text('Creator',style:TextStyle(fontSize: 30))),
                            DropdownMenuItem<String>(value: 'language', child: Text('Language',style:TextStyle(fontSize: 30))),
                            ]
                            ),
              ]
            ),
                

          


            Expanded(
              child: ListView.builder(
              itemCount: pluginsViewModel.sortedPlugins.length,
              itemBuilder: (context, index) {
                final plugin = pluginsViewModel.sortedPlugins[index];
              
                return Card (
                  color: plugin.activeColor,
                   child: ExpansionTile(
                   title: Text(plugin?.fileName ?? 'No name'),
                  subtitle: Text(plugin?.creator ?? 'Unknown creator'),
                  leading: Icon(plugin.active ? Icons.play_arrow : Icons.pause_circle, color: Colors.black),
                  children: [
                    Column(
                      children: [
                        Row(
                          children: [
                            Text(plugin.language.toString()),
                            Spacer(),
                            Text(plugin?.tags.toString() ?? 'Unknown Time'),
                          ],),
                          Row(
                            children: [
                              Text(plugin?.active.toString()?? 'Unknown Status'),
                              Spacer(),
                              Text(plugin?.updatedAt.toString() ?? 'Unknown Severity'),
                            ],),
                     
                        SizedBox(
                          width: double.infinity,
                          child:ElevatedButton(onPressed:() { 
                            
                            if(plugin!.active) {
                              pluginsViewModel.stopPlugin(plugin.fileName);
                          
                            }
                            else {
                              pluginsViewModel.startPlugin(plugin.fileName);
                            }
                            
                            }, 
                            
                            child: Text(
                              plugin.active ? 'Stop plugin' : 'Start plugin')) 
                          ),

                          SizedBox(
                            width: double.infinity,
                          child:ElevatedButton(onPressed:() { },
                            

                            
                            child: Text('Force plugin')) 
                          ),
                          
                        
                      ]
                      
                    
                    )
              
                  ],
                  )
                   );
                            
                            },
              padding: const EdgeInsets.all(10),
              scrollDirection: Axis.vertical,
              ),
              ),
              ],
    );
        
  

  }
}