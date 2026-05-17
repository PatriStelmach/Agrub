import 'package:alert_app/logic/user_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class UserScreen extends StatelessWidget {
  const UserScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // context.watch sprawia, że widget przebuduje się, gdy wywołasz notifyListeners()
    final userViewModel = context.watch<UserViewModel>();
    final user = userViewModel.user;

    // 1. Zabezpieczenie na wypadek, gdyby dane jeszcze nie dotarły
    if (user == null) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 10),
            Text("Ładowanie danych użytkownika..."),
          ],
        ),
      );
    }

    // 2. Jeśli dane są dostępne, renderujemy UI
    return Padding(
      padding: const EdgeInsets.all(18.0),
      child: SingleChildScrollView( // Dodane na wypadek mniejszych ekranów
        child: Column(
          children: [
            const Icon(Icons.account_circle_rounded, size: 100),
            const SizedBox(height: 20),
            
            _buildDataRow('Username', user.login),
            _buildDataRow('E-mail', user.email),
            _buildDataRow('Role', user.role),
            _buildDataRow('Group', user.group),
            
            const SizedBox(height: 30),
            
            // Przycisk wylogowania (opcjonalnie, ale przydatne do testów)
            ElevatedButton(
              onPressed: () {context.read<UserViewModel>().signOut();},
              child: const Text("Wyloguj się"),
            )
          ],
        ),
      ),
    );
  }

  // Helper do budowania wierszy, żeby kod nie był powtarzalny
  Widget _buildDataRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        children: [
          Text(label, style: const TextStyle(fontSize: 24, color: Colors.grey)),
          const Spacer(),
          Text(value, style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
        ],
      ),
    );
  }
}