import 'package:alert_app/logic/user_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class UserScreen extends StatelessWidget {
  const UserScreen({super.key});

  @override
  Widget build(BuildContext context) {

        final userViewModel = context.watch<UserViewModel>();

    return Padding(
      padding: const EdgeInsets.all(18.0),
      child: Column(
        children: [
          Icon(Icons.account_circle_rounded,size: 100),
          Row(
            children: [
              Text('Username', style: TextStyle(fontSize: 30)),
              Spacer(),
              Text(userViewModel.user.login, style: TextStyle(fontSize: 30))
      
            ],),


              Row(
            children: [
              Text('E-mail', style: TextStyle(fontSize: 30)),
              Spacer(),
              Text(userViewModel.user.email, style: TextStyle(fontSize: 30))
      
            ],),




              Row(
            children: [
              Text('Role', style: TextStyle(fontSize: 30)),
              Spacer(),
              Text(userViewModel.user.role, style: TextStyle(fontSize: 30))
      
            ],),
              Row(
            children: [
              Text('Group', style: TextStyle(fontSize: 30)),
              Spacer(),
              Text(userViewModel.user.group, style: TextStyle(fontSize: 30))
      
            ],)
      ],),
    );
  }
}