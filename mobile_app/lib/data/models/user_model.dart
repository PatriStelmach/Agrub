class UserModel {
  final String login;
  final String email;
  final String role;
  final List<String> groups;

  UserModel({
    required this.login,
    required this.email,
    required this.role,
    required this.groups,
  });

  factory UserModel.fromJwt(Map<String, dynamic> decodedToken) {
    final rawGroups = decodedToken['groups'];

    final groupNames = rawGroups.map((group) {
      final groupMap = group as Map<dynamic, dynamic>;
      return groupMap['name']?.toString() ?? 'None';
    }).toList();

    return UserModel(
      login: decodedToken['sub'] ?? 'Unknown',
      email: decodedToken['email'] ?? '',
      role: decodedToken['role'] ?? 'USER',
      groups: groupNames,
    );
  }
}
