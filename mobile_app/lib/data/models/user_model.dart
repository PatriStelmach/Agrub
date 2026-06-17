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
    final rawGroups = decodedToken['groups'];
    String groupName = 'None';

    if (rawGroups != null && rawGroups is List && rawGroups.isNotEmpty) {
      final firstGroup = rawGroups.first;
      if (firstGroup is Map) {
        groupName = firstGroup['name']?.toString() ?? 'None';
      }
    }

    return UserModel(
      login: decodedToken['sub'] ?? 'Unknown',
      email: decodedToken['email'] ?? '',
      role: decodedToken['role'] ?? 'USER',
      group: groupName,
    );
  }
}
