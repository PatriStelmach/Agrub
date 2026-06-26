import 'package:alert_app/data/models/plugin_model.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class CronEditDialog extends StatefulWidget {
  final Plugin plugin;
  const CronEditDialog({super.key, required this.plugin});

  @override
  State<CronEditDialog> createState() => _CronEditDialogState();
}

class _CronEditDialogState extends State<CronEditDialog> {
  late TextEditingController _cronController;
  late TextEditingController _argsController;
  late bool _isActive;

  @override
  void initState() {
    super.initState();
    _cronController = TextEditingController(text: widget.plugin.cronExpression);
    _argsController = TextEditingController(text: '');
    _isActive = widget.plugin.active;
  }

  @override
  void dispose() {
    _cronController.dispose();
    _argsController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;

    return AlertDialog(
      title: Text(t.plugins_cron_title(widget.plugin.fileName)),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _cronController,
              decoration: InputDecoration(
                labelText: t.plugins_cron_field_cron,
                hintText: '*/5 * * * *',
              ),
            ),
            TextField(
              controller: _argsController,
              decoration: InputDecoration(
                labelText: t.plugins_cron_field_args,
                hintText: '--verbose --save-logs',
              ),
            ),
            const SizedBox(height: 16),
            SwitchListTile(
              title: Text(t.plugins_cron_switch_active),
              value: _isActive,
              onChanged: (value) => setState(() => _isActive = value),
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text(t.plugins_cron_button_cancel),
        ),
        ElevatedButton(
          onPressed: () async {
            final pluginsViewModel = context.read<PluginsViewModel>();

            final success = await pluginsViewModel.saveCronSettings(
              plugin: widget.plugin,
              cronExpression: _cronController.text,
              isActive: _isActive,
            );

            if (context.mounted) {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text(
                    success
                        ? t.plugins_cron_toast_success
                        : t.plugins_cron_toast_error,
                  ),
                  backgroundColor: success ? Colors.green : Colors.red,
                ),
              );
            }
          },
          child: Text(t.plugins_cron_button_save),
        ),
      ],
    );
  }
}
