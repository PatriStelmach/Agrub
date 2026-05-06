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

    debugPrint("--- REBUILD ---");
    final alertsViewModel = context.watch<AlertsViewModel>();
    
    if (alertsViewModel.alertsList.isEmpty) {
    return const Scaffold( 
      body: Center(
        child: Text("No alerts found. Consider checking the button in Debug Screen!"),
      ),
    );
}

final sortedList = alertsViewModel.sortedAlerts;
    
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
              itemCount: sortedList.length,
              itemBuilder: (context, index) {
                final alert = alertsViewModel.sortedAlerts[index];
              
                return Card (
                  color: alert?.severityColor,
                   child: ExpansionTile(
                   title: Text(alert?.subject ?? 'No Title'),
                  subtitle: Text(alert.acknowledged ? 'ACKNOWLEDGED' : 'NOT ACKNOWLEDGED'),
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
                          child:ElevatedButton(onPressed:() { _openAckDialog(context, alert.id);}, child: Text('Acknowledge')) 
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


class AckDialog extends StatefulWidget {
  final int alertId;
  const AckDialog({super.key, required this.alertId});

  @override
  State<AckDialog> createState() => _AckDialogState();
}

class _AckDialogState extends State<AckDialog> {
  late TextEditingController _controller;

  bool isAck = true;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();
  }

  @override
  void dispose() {
    _controller.dispose(); 
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
title: Text('Actions for Alert ${widget.alertId}'),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _controller,
              decoration: const InputDecoration(labelText: 'Your comment'),
            ),
            const SizedBox(height: 15),
            CheckboxListTile(
              title: const Text("Acknowledge alert"),
              value: isAck,
              contentPadding: EdgeInsets.zero,
              onChanged: (bool? value) {
                setState(() {
                  isAck = value ?? false;
                });
              },
            ),
          ],
        ),
      ),
      actions: [
        TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),

        ElevatedButton(
          onPressed: () {
            final String commentValue = _controller.text;
            context.read<AlertsViewModel>().acknowledgeAlert(widget.alertId, comment: commentValue, isAck: this.isAck );
            Navigator.pop(context);
          },
          child: const Text('ACK'),
        ),
      ],
    );
  }
}


void _openAckDialog(BuildContext context, int alertId) {

  showDialog(
    context: context,
    builder: (context) => AckDialog(alertId: alertId),
  );
}
