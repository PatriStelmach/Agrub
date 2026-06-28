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

            Row(
              children: [
                Text(
                  t.user_name,
                  style: const TextStyle(fontSize: 24, color: Colors.grey),
                ),
                const Spacer(),

                Text(
                  user.name,
                  style: const TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),

            Row(
              children: [
                Text(
                  t.user_surname,
                  style: const TextStyle(fontSize: 24, color: Colors.grey),
                ),
                const Spacer(),
                Text(
                  user.surname,
                  style: const TextStyle(
                    fontSize: 24,
                    color: Colors.grey,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),

            Row(
              children: [
                Text(
                  t.user_label_username,
                  style: const TextStyle(fontSize: 24, color: Colors.grey),
                ),
                const Spacer(),

                Text(
                  user.login,
                  style: const TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            Row(
              children: [
                Text(
                  t.user_label_email,
                  style: const TextStyle(fontSize: 24, color: Colors.grey),
                ),
                const Spacer(),
                Text(
                  user.email,
                  style: const TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            Row(
              children: [
                Text(
                  t.user_label_role,
                  style: const TextStyle(fontSize: 24, color: Colors.grey),
                ),
                const Spacer(),
                Text(
                  user.role,
                  style: const TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
            Row(
              children: [
                Text(
                  t.user_label_group,
                  style: const TextStyle(fontSize: 24, color: Colors.grey),
                ),
                const Spacer(),
                Text(
                  user.groups.toString(),
                  style: const TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
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
