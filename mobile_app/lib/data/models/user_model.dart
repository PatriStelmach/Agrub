class UserModel {
  final String name;
  final String surname;
  final String email;
  final String role;
  final List<dynamic> groups;

  UserModel({
    required this.name,
    required this.surname,
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
      name: decodedToken['firstname'] ?? 'Unknown',
      surname: decodedToken['surname'] ?? 'Unknown',
      email: decodedToken['sub'] ?? ' Unknown',
      role: decodedToken['role'] ?? 'USER',
      groups: groupNames,
    );
  }
}
