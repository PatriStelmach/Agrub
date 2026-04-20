import 'package:alert_app/data/models/alert_model.dart';
import 'package:flutter/material.dart';




class AlertRepository extends ChangeNotifier {




DateTime lastPing = DateTime.now();

void ping() {
  lastPing = DateTime.now();
  notifyListeners();
}


// very simple placehold for sending ack
Future<void> sendAcknowledge(String id) async {
  

  
 print("ACK sent");
  

}



  final List<Alert> mockData = [
     
  Alert(
    id: 'ALR-001',
    title: 'Database Connection Timeout',
    hostName: 'DB-PROD-PRIMARY',
    ipAddress: '10.0.0.50',
    severity: AlertSeverity.extreme,
    status: AlertStatus.problem,
    createdAt: DateTime.now().subtract(const Duration(minutes: 5)),
    source: 'Zabbix',
    description: 'Baza danych nie odpowiada na zapytania od 5 minut. Wszystkie serwisy zależne leżą.',
  ),

  Alert(
    id: 'ALR-002',
    title: 'High CPU Usage (>90%)',
    hostName: 'WEB-SERVER-01',
    ipAddress: '10.0.0.11',
    severity: AlertSeverity.high,
    status: AlertStatus.acknowledged, // Ktoś już nad tym pracuje
    createdAt: DateTime.now().subtract(const Duration(hours: 2)),
    source: 'Nagios',
    description: 'Proces "nginx" zużywa 98% zasobów procesora.',
  ),

  Alert(
    id: 'ALR-003',
    title: 'Disk Space Warning',
    hostName: 'BACKUP-NODE',
    ipAddress: '192.168.1.100',
    severity: AlertSeverity.medium,
    status: AlertStatus.problem,
    createdAt: DateTime.now().subtract(const Duration(days: 1)),
    source: 'Zabbix',
    description: 'Partycja /home zapełniona w 85%. Sugerowane czyszczenie logów.',
  ),

  Alert(
    id: 'ALR-004',
    title: 'SSL Certificate Expiry',
    hostName: 'kamil-kornatowski.pl',
    ipAddress: '1.1.1.1',
    severity: AlertSeverity.low,
    status: AlertStatus.resolved, // Problem już nie występuje
    createdAt: DateTime.now().subtract(const Duration(hours: 12)),
    source: 'Manual Check',
    description: 'Certyfikat SSL wygaśnie za 14 dni. (Odnowiono pomyślnie)',
  ),

  Alert(
    id: 'ALR-005',
    title: 'System Update Available',
    hostName: 'OFFICE-PC-05',
    ipAddress: '10.0.1.205',
    severity: AlertSeverity.info,
    status: AlertStatus.problem,
    createdAt: DateTime.now().subtract(const Duration(minutes: 30)),
    source: 'Prometheus',
    description: 'Dostępna jest nowa wersja kernela systemu Linux. Ten opis jest celowo bardzo długi, abyś mógł sprawdzić, czy Twój Box w aplikacji mobilnej poprawnie zawija tekst, czy może brzydko go ucina lub powoduje błąd "overflow". Pamiętaj o użyciu TextOverflow.ellipsis!',
      
  
  )
    ];


  }



/*
Ta wersja implementowała abstract class który był implementowany przez mock data i api alert
Miejsce na faktyczną logikę 
class ApiAlertRepository implements AlertRepository {
  @override
  Future<List<Alert>> fetchAlerts() {
    // TODO: implement fetchAlerts
    throw UnimplementedError();
  }

  @override
  Future<void> markAsRead(String id) {
    // TODO: implement markAsRead
    throw UnimplementedError();
  }
  }
  */

