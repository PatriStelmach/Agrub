import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class UserScreen extends StatelessWidget {
  const UserScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final userViewModel = context.watch<UserViewModel>();
    final user = userViewModel.user;
    final t = AppLocalizations.of(context)!;

    if (user == null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 10),
            Text(t.user_loading_data),
          ],
        ),
      );
    }

    return Padding(
      padding: const EdgeInsets.all(18.0),
      child: SingleChildScrollView(
        child: Column(
          children: [
            const Icon(Icons.account_circle_rounded, size: 100),
            const SizedBox(height: 20),

            _buildDataRow(t.user_label_username, user.login),
            _buildDataRow(t.user_label_email, user.email),
            _buildDataRow(t.user_label_role, user.role),
            _buildDataRow(t.user_label_group, user.group),

            const SizedBox(height: 30),

            ElevatedButton(
              onPressed: () {
                context.read<UserViewModel>().signOut();
              },
              child: Text(t.user_button_logout),
            ),
          ],
        ),
      ),
    );
  }

  // Helper for building rows
  Widget _buildDataRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        children: [
          Text(label, style: const TextStyle(fontSize: 24, color: Colors.grey)),
          const Spacer(),
          Text(
            value,
            style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
          ),
        ],
      ),
    );
  }
}
