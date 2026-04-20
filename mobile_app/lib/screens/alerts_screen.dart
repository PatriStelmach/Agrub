import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class AlertsScreen extends StatefulWidget {
  const AlertsScreen({super.key});

  @override
  State<AlertsScreen> createState() => _AlertsScreenState();
}

class _AlertsScreenState extends State<AlertsScreen> {
  String dropDownValue = 'id';
  
  @override
  Widget build(BuildContext context) {

    final alertsViewModel = context.watch<AlertsViewModel>();
    

    
    return Column(
      
          children: [

            Row(
              children: [
                Text("Sort by - "),
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
            DropdownMenuItem<String>(value: 'id', child: Text('ID')),
            DropdownMenuItem<String>(value: 'title', child: Text('Title')),
            DropdownMenuItem<String>(value: 'createdAt', child: Text('CreatedAt')),
            ]
            ),
                ElevatedButton(onPressed: () {alertsViewModel.sortAlertsBy(dropDownValue);}, child: Text("Sortuj"))
              ],
            ),    

          


            Expanded(
              child: Container(
              decoration: BoxDecoration(
                border: Border.all(color: Colors.black),
                borderRadius: BorderRadius.circular(12),
              ),
              
                child: ListView.builder(
                itemCount: alertsViewModel.alertsList.length,
                itemBuilder: (context, index) {
                  final alert = alertsViewModel.alertsList[index];

                  return Card (
                    color: alert.severityColor,
                     child: ExpansionTile(
                     title: Text(alert.title),
                    subtitle: Text(alert.hostName),
                    leading: Icon(Icons.warning, color: Colors.black),
                    children: [
                      Column(
                        children: [
                          Row(
                            children: [
                              Text(alert.source),
                              Spacer(),
                              Text(alert.createdAt.toString()),
                            ],),
                            Row(
                              children: [
                                Text(alert.status.toString()),
                                Spacer(),
                                Text(alert.severity.toString()),
                              ],),
                       
                          SizedBox(
                            width: double.infinity,
                            child:ElevatedButton(onPressed:() { alertsViewModel.acknowledgeAlert(alert.id);}, child: Text('Acknowledge')) 
                            )
                          
                        ]
                        

                      )

                    ],
                    )
                     );
              
              },
                padding: const EdgeInsets.all(10),
                scrollDirection: Axis.vertical,
                )
              ),
              ),
              ]
    );

  }
}