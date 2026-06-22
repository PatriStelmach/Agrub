import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:alert_app/screens/login_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class UserScreen extends StatelessWidget {
  const UserScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final userViewModel = context.watch<UserViewModel>();
    final user = userViewModel.user;
    final t = AppLocalizations.of(context)!;

    if (user == null || userViewModel.isLoading) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const CircularProgressIndicator(),
            const SizedBox(height: 10),
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

            UserDataRow(label: t.user_label_username, value: user.login),
            UserDataRow(label: t.user_label_email, value: user.email),
            UserDataRow(label: t.user_label_role, value: user.role),
            UserDataRow(
              label: t.user_label_group,
              value: user.groups.toString(),
            ),

            const SizedBox(height: 30),

            ElevatedButton(
              onPressed: () {
                context.read<UserViewModel>().signOut();

                Navigator.of(context).pushAndRemoveUntil(
                  MaterialPageRoute(builder: (context) => const LoginScreen()),
                  (Route<dynamic> route) => false,
                );
              },
              child: Text(t.user_button_logout),
            ),
          ],
        ),
      ),
    );
  }
}

class UserDataRow extends StatelessWidget {
  final String label;
  final String value;

  const UserDataRow({super.key, required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
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
