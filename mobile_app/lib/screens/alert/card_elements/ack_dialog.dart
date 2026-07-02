import 'package:alert_app/data/models/alert_model.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/alerts_view_model.dart';
import 'package:alert_app/logic/user_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class AckDialog extends StatefulWidget {
  final Alert alert;
  final AppLocalizations t;

  const AckDialog({super.key, required this.alert, required this.t});

  @override
  State<AckDialog> createState() => _AckDialogState();
}

class _AckDialogState extends State<AckDialog> {
  late TextEditingController _controller;
  late bool isAck;
  late int selectedSeverity;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController();

    selectedSeverity = widget.alert.severity.index;
    isAck = widget.alert.acknowledged;
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final t = widget.t;

    return AlertDialog(
      title: Text(t.alerts_dialog_title(widget.alert.id.toString())),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _controller,
              decoration: InputDecoration(
                labelText: t.alerts_dialog_field_comment,
              ),
            ),
            const SizedBox(height: 15),
            Text(
              t.alerts_dialog_section_severity,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 5),
            DropdownButtonFormField<int>(
              initialValue: selectedSeverity,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                contentPadding: EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 5,
                ),
              ),
              items: [
                DropdownMenuItem(
                  value: 0,
                  child: Text(t.alerts_dialog_severity_unknown),
                ),
                DropdownMenuItem(
                  value: 1,
                  child: Text(t.alerts_dialog_severity_info),
                ),
                DropdownMenuItem(
                  value: 2,
                  child: Text(t.alerts_dialog_severity_low),
                ),
                DropdownMenuItem(
                  value: 3,
                  child: Text(t.alerts_dialog_severity_medium),
                ),
                DropdownMenuItem(
                  value: 4,
                  child: Text(t.alerts_dialog_severity_high),
                ),
                DropdownMenuItem(
                  value: 5,
                  child: Text(t.alerts_dialog_severity_critical),
                ),
              ],
              onChanged: (int? newValue) {
                if (newValue != null) {
                  setState(() => selectedSeverity = newValue);
                }
              },
            ),
            CheckboxListTile(
              title: Text(t.alerts_dialog_check_ack),
              value: isAck,
              contentPadding: EdgeInsets.zero,
              onChanged: (bool? value) {
                if (value != null) {
                  setState(() => isAck = value);
                }
              },
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text(t.alerts_dialog_button_cancel),
        ),
        ElevatedButton(
          onPressed: () {
            final commentValue = _controller.text;
            final currentUser = context.read<UserViewModel>().user;
            final currentAuthor = currentUser?.email ?? "Mobile User";

            context.read<AlertsViewModel>().acknowledgeAlert(
              widget.alert.id,
              author: currentAuthor,
              comment: commentValue,
              newSeverity: selectedSeverity,
              isAck: isAck,
            );

            Navigator.pop(context);
          },
          child: Text(t.alerts_dialog_button_update),
        ),
      ],
    );
  }
}
