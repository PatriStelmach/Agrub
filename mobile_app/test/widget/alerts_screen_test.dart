import 'package:alert_app/screens/alerts_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('Touching alert card', (WidgetTester tester) async {
    await tester.pumpWidget(const MaterialApp(home: AlertsScreen()));
  });
}
