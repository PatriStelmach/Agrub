import 'package:alert_app/data/models/plugin_model.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:alert_app/l10n/app_localizations.dart';

class PluginsScreen extends StatefulWidget {
  const PluginsScreen({super.key});

  @override
  State<PluginsScreen> createState() => _PluginsScreenState();
}

class _PluginsScreenState extends State<PluginsScreen> {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        context.read<PluginsViewModel>().loadPlugins();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;
    final viewModel = context.watch<PluginsViewModel>();

    if (viewModel.isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (viewModel.errorMessage != null) {
      return Scaffold(
        body: Center(
          child: Text(
            viewModel.errorMessage!,
            style: const TextStyle(color: Colors.red),
          ),
        ),
      );
    }
    final sortedList = viewModel.sortedPlugins;
    if (sortedList.isEmpty) {
      return Scaffold(body: Center(child: Text(t.plugins_no_plugins_found)));
    }

    return Column(
      children: [
        _buildSortHeader(context, viewModel, t),
        Expanded(
          child: ListView.builder(
            itemCount: sortedList.length,
            padding: const EdgeInsets.all(10),
            itemBuilder: (context, index) {
              final plugin = sortedList[index];
              return _buildPluginCard(context, plugin, t);
            },
          ),
        ),
      ],
    );
  }

  Widget _buildSortHeader(
    BuildContext context,
    PluginsViewModel viewModel,
    AppLocalizations t,
  ) {
    return Row(
      children: [
        Text(t.plugins_sort_by, style: const TextStyle(fontSize: 25)),
        DropdownButton<String>(
          padding: const EdgeInsets.all(6),
          borderRadius: const BorderRadius.all(Radius.circular(16)),
          value: viewModel.currentSortProperty,
          icon: const Icon(Icons.menu),
          style: TextStyle(
            color: Theme.of(context).textTheme.bodyLarge?.color,
            fontSize: 18,
            fontFamily: 'JetBrainsMono',
          ),
          onChanged: (String? newValue) {
            if (newValue != null) {
              viewModel.sortPluginsBy(newValue);
            }
          },
          items: [
            DropdownMenuItem(
              value: 'fileName',
              child: Text(
                t.plugins_sort_filename,
                style: const TextStyle(fontSize: 25),
              ),
            ),
            DropdownMenuItem(
              value: 'creator',
              child: Text(
                t.plugins_sort_creator,
                style: const TextStyle(fontSize: 25),
              ),
            ),
            DropdownMenuItem(
              value: 'language',
              child: Text(
                t.plugins_sort_language,
                style: const TextStyle(fontSize: 25),
              ),
            ),
          ],
        ),
        IconButton(
          icon: Icon(
            viewModel.isAscending ? Icons.arrow_upward : Icons.arrow_downward,
          ),
          onPressed: () {
            viewModel.sortPluginsBy(
              viewModel.currentSortProperty,
              ascending: !viewModel.isAscending,
            );
          },
        ),
      ],
    );
  }

  Widget _buildPluginCard(
    BuildContext context,
    Plugin plugin,
    AppLocalizations t,
  ) {
    return Card(
      color: plugin.activeColor(context),
      child: ExpansionTile(
        title: Text(plugin.fileName),
        subtitle: Text(plugin.creator),
        leading: Icon(
          plugin.active ? Icons.play_arrow : Icons.pause_circle,
          color: Colors.black,
        ),
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(plugin.language.toString()),
                    Text(plugin.tags.toString()),
                  ],
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(plugin.active.toString()),
                    Text(plugin.updatedAt.toString()),
                  ],
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () => _showCronDialog(context, plugin),
                    child: Text(t.plugins_button_edit),
                  ),
                ),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () => _showRunArgsDialog(context, plugin),
                    child: const Text(t.plugins_button_run),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void _showCronDialog(BuildContext context, Plugin plugin) {
    showDialog(
      context: context,
      builder: (_) => CronEditDialog(plugin: plugin),
    );
  }

  void _showRunArgsDialog(BuildContext context, Plugin plugin) {
    showDialog(
      context: context,
      builder: (_) => RunPluginDialog(plugin: plugin),
    );
  }
}

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
            final viewModel = context.read<PluginsViewModel>();

            final success = await viewModel.saveCronSettings(
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

class RunPluginDialog extends StatefulWidget {
  final Plugin plugin;
  const RunPluginDialog({super.key, required this.plugin});

  @override
  State<RunPluginDialog> createState() => _RunPluginDialogState();
}

class _RunPluginDialogState extends State<RunPluginDialog> {
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
                  content: Text("Błąd uruchamiania: $e"),
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
