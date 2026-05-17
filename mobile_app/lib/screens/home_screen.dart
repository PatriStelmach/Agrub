import 'package:alert_app/logic/general_layout_view_model.dart';
import 'package:flutter/material.dart';
import 'package:alert_app/logic/home_view_model.dart';
import 'package:provider/provider.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final homeViewModel = context.watch<HomeViewModel>();
    
    final DateTime lastPingTime = homeViewModel.repository.lastPing;
    final String shortPingTime = 
        "${lastPingTime.hour}:${lastPingTime.minute.toString().padLeft(2, '0')}:${lastPingTime.second.toString().padLeft(2, '0')}";
    
    final int activeAlertsCount = homeViewModel.repository.alertsCache.length;
    
    final criticalAlerts = homeViewModel.latestCriticalAlerts();
    final latestCritical = criticalAlerts.isNotEmpty ? criticalAlerts.first : null;

    return Scaffold(
      body: SingleChildScrollView( 
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                _buildStatCard(
                  context,
                  title: "Last Ping",
                  value: shortPingTime,
                  icon: Icons.sync,
                  color: Colors.blueGrey,
                ),
                const SizedBox(width: 12),
                _buildStatCard(
                  context,
                  title: "Active Alerts",
                  value: activeAlertsCount.toString(),
                  icon: Icons.notifications_active,
                  color: activeAlertsCount > 0 ? Colors.orange : Colors.green,
                ),
              ],
            ),
            const SizedBox(height: 24),

            const Text('Latest Critical Incident', 
                style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            if (latestCritical != null)
              Card(
                elevation: 4,
                color: latestCritical.severityColor.withOpacity(0.9),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                child: ListTile(
                  contentPadding: const EdgeInsets.all(16),
                  leading: const CircleAvatar(
                    backgroundColor: Colors.white,
                    child: Icon(Icons.priority_high, color: Colors.red),
                  ),
                  title: Text(latestCritical.subject, 
                      style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
                  subtitle: Text("Acknowledged? ${latestCritical.acknowledged}\nTime: ${latestCritical.createdAt}"),
                  isThreeLine: true,
                ),
              )
            else
              const Card(
                child: ListTile(
                  title: Text("No critical alerts"),
                  leading: Icon(Icons.check_circle, color: Colors.green),
                ),
              ),
            
            const SizedBox(height: 24),

            const Text('Quick Navigation', 
                style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            Row(
              children: [
                _buildNavTile(
                  context,
                  label: "ALL ALERTS",
                  icon: Icons.list_alt,
                  color: Colors.indigo,
                  onTap: () {
                    context.read<GeneralLayoutViewModel>().changePage('Alerts');
                  },
                ),
                const SizedBox(width: 12),
                _buildNavTile(
                  context,
                  label: "PLUGINS",
                  icon: Icons.extension,
                  color: Colors.teal,
                  onTap: () {
                    context.read<GeneralLayoutViewModel>().changePage('Plugins');
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatCard(BuildContext context, 
      {required String title, required String value, required IconData icon, required Color color}) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: color.withOpacity(0.1),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: color.withOpacity(0.5)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: color),
            const SizedBox(height: 8),
            Text(title, style: TextStyle(color: color, fontWeight: FontWeight.w600)),
            Text(value, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          ],
        ),
      ),
    );
  }

  Widget _buildNavTile(BuildContext context, 
      {required String label, required IconData icon, required Color color, required VoidCallback onTap}) {
    return Expanded(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Container(
          height: 120,
          decoration: BoxDecoration(
            color: color,
            borderRadius: BorderRadius.circular(16),
            boxShadow: [BoxShadow(color: color.withOpacity(0.3), blurRadius: 8, offset: const Offset(0, 4))],
          ),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(icon, color: Colors.white, size: 40),
              const SizedBox(height: 8),
              Text(label, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 16)),
            ],
          ),
        ),
      ),
    );
  }
}