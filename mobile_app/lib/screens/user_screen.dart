import 'package:alert_app/logic/user_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class UserScreen extends StatelessWidget {
  const UserScreen({super.key});

  @override
  Widget build(BuildContext context) {

        final userViewModel = context.watch<UserViewModel>();

    return Column(
      children: [
        Icon(Icons.account_circle_rounded,size: 100),
        Row(
          children: [
            Text('Username'),
            Spacer(),
            Text(userViewModel.user.login)

          ],),
            Row(
          children: [
            Text('Role'),
            Spacer(),
            Text(userViewModel.user.role)

          ],),
            Row(
          children: [
            Text('Group'),
            Spacer(),
            Text(userViewModel.user.group)

          ],)
    ],);
  }
}