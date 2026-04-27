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
    
if (alertsViewModel.alertsList.isEmpty) {
    return const Scaffold( 
      body: Center(
        child: Text("No alerts found. Consider checking the button in Debug Screen!"),
      ),
    );
}
else {
  alertsViewModel.sortAlertsBy(dropDownValue);
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
                            DropdownMenuItem<String>(value: 'id', child: Text('ID',style:TextStyle(fontSize: 30))),
                            DropdownMenuItem<String>(value: 'title', child: Text('Title',style:TextStyle(fontSize: 30))),
                            DropdownMenuItem<String>(value: 'createdAt', child: Text('CreatedAt',style:TextStyle(fontSize: 30))),
                            ]
                            ),
              ]
            ),
                

          


            Expanded(
              child: ListView.builder(
              itemCount: alertsViewModel.sortedAlerts.length,
              itemBuilder: (context, index) {
                final alert = alertsViewModel.sortedAlerts[index];
              
                return Card (
                  color: alert?.severityColor,
                   child: ExpansionTile(
                   title: Text(alert?.title ?? 'No Title'),
                  subtitle: Text(alert?.hostName ?? 'Unknown Host'),
                  leading: Icon(Icons.warning, color: Colors.black),
                  children: [
                    Column(
                      children: [
                        Row(
                          children: [
                            Text(alert?.source ?? 'Unknown Host'),
                            Spacer(),
                            Text(alert?.createdAt.toString() ?? 'Unknown Time'),
                          ],),
                          Row(
                            children: [
                              Text(alert?.status.toString()?? 'Unknown Status'),
                              Spacer(),
                              Text(alert?.severity.toString() ?? 'Unknown Severity'),
                            ],),
                     
                        SizedBox(
                          width: double.infinity,
                          child:ElevatedButton(onPressed:() { alertsViewModel.acknowledgeAlert(alert!.id);}, child: Text('Acknowledge')) 
                          )
                        
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