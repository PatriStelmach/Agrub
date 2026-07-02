import 'package:alert_app/data/models/plugin_model.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class PluginDialog extends StatefulWidget {
  final Plugin plugin;
  const PluginDialog({super.key, required this.plugin});

  @override
  State<PluginDialog> createState() => _PluginDialogState();
}

class _PluginDialogState extends State<PluginDialog> {
  final _argsController = TextEditingController();

  @override
  void dispose() {
    _argsController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;

    return AlertDialog(
      title: Text(t.plugins_args_title(widget.plugin.fileName)),
      content: TextField(
        controller: _argsController,
        decoration: InputDecoration(
          labelText: t.plugins_args_field_args,
          hintText: '--verbose --mode=production',
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text(t.plugins_args_button_cancel),
        ),
        ElevatedButton(
          onPressed: () async {
            final args = _argsController.text.trim();
            final enteredArgs = args.isNotEmpty ? args : null;

            final viewModel = context.read<PluginsViewModel>();
            final messenger = ScaffoldMessenger.of(context);
            final pluginName = widget.plugin.fileName;

            Navigator.pop(context);

            try {
              await viewModel.startPlugin(
                widget.plugin,
                arguments: enteredArgs,
              );

              messenger.showSnackBar(
                SnackBar(
                  content: Text(t.plugins_args_toast_triggered(pluginName)),
                  backgroundColor: Colors.green,
                ),
              );
            } catch (e) {
              messenger.showSnackBar(
                SnackBar(
                  content: Text(t.plugins_cron_toast_error),
                  backgroundColor: Colors.red,
                ),
              );
            }
          },
          child: Text(t.plugins_args_button_run),
        ),
      ],
    );
  }
}
