import 'package:alert_app/data/models/user_model.dart';
import 'package:flutter/material.dart';

//connecting to server to check authorization of user

class UserRepository extends ChangeNotifier {

  
final UserModel testUser = UserModel(id: 1, login: 'Donald Duck', email: 'dduck@alert.com', role: 'admin', group: 'admins');

}