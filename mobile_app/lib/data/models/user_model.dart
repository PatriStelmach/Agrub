

class UserModel {
final String login;
  final String email;
  final String role;
  final String group;

UserModel({required this.login, required this.email, required this.role, required this.group});

  // Creating user based on JWT data
  factory UserModel.fromJwt(Map<String, dynamic> decodedToken) {
    return UserModel(
      // Klucze (np. 'sub', 'role') muszą zgadzać się z tym, co koledzy wysyłają w tokenie
      login: decodedToken['sub'] ?? 'Unknown', 
      email: decodedToken['email'] ?? '',
      role: decodedToken['role'] ?? 'USER',
      group: decodedToken['group'] ?? 'None',
    );
  }
}