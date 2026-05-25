class UserModel {
  final String login;
  final String email;
  final String role;
  final String group;

  UserModel({
    required this.login,
    required this.email,
    required this.role,
    required this.group,
  });

  // Creating user based on JWT data
  factory UserModel.fromJwt(Map<String, dynamic> decodedToken) {
    return UserModel(
      login: decodedToken['sub'] ?? 'Unknown',
      email: decodedToken['email'] ?? '',
      role: decodedToken['role'] ?? 'USER',
      group:
          (decodedToken['groups'] != null &&
              (decodedToken['groups'] as List).isNotEmpty)
          ? (decodedToken['groups'] as List).first.toString()
          : 'None',
    );
  }
}
