class AlertAction {
  final int id;
  final String author;
  final String message;
  final bool ack;
  final int newSeverity;
  final DateTime createdAt;

  AlertAction({
    required this.id,
    required this.author,
    required this.message,
    required this.ack,
    required this.newSeverity,
    required this.createdAt,
  });

  factory AlertAction.fromJson(Map<String, dynamic> json) {
    return AlertAction(
      id: json['id'] as int,
      author: json['author'] ?? 'Unknown',
      message: json['message'] ?? '',
      ack: json['ackUpdate'] ?? false,
      newSeverity: json['newSeverity'] ?? 0,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'].toString())
          : DateTime.now(),
    );
  }
}
