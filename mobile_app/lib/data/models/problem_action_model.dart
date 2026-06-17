class ProblemAction {
  final int id;
  final String author;
  final String message;
  final bool ack;
  final int newSeverity;
  final DateTime createdAt;

  ProblemAction({
    required this.id,
    required this.author,
    required this.message,
    required this.ack,
    required this.newSeverity,
    required this.createdAt,
  });

  factory ProblemAction.fromJson(Map<String, dynamic> json) {
    return ProblemAction(
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
