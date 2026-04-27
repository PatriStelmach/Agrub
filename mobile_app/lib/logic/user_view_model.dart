

import 'package:alert_app/data/models/user_model.dart';
import 'package:alert_app/data/repositories/user_repository.dart';
import 'package:flutter/material.dart';

class UserViewModel extends ChangeNotifier{

final UserRepository repository;
UserViewModel({required this.repository}) {
  repository.addListener(notifyListeners);
}


UserModel get user => repository.testUser;

}