import 'package:alert_app/data/models/plugin_model.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';
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
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      if (mounted) {
        await context.read<PluginsViewModel>().loadPlugins();
        if (mounted) {
          setState(() {
            _isLoading = false;
          });
        }
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final t = AppLocalizations.of(context)!;
    final pluginsViewModel = context.watch<PluginsViewModel>();

    if (_isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (pluginsViewModel.pluginList.isEmpty) {
      return Scaffold(body: Center(child: Text(t.plugins_no_plugins_found)));
    }

    final sortedList = pluginsViewModel.sortedPlugins;
    final currentSort = pluginsViewModel.currentSortProperty;
    final isAsc = pluginsViewModel.isAscending;

    return Column(
      children: [
        Row(
          children: [
            Text(t.plugins_sort_by, style: TextStyle(fontSize: 30)),
            DropdownButton<String>(
              padding: EdgeInsets.all(6),
              borderRadius: BorderRadius.all(Radius.circular(16)),
              value: currentSort,
              icon: const Icon(Icons.menu),
              style: TextStyle(
                color: Theme.of(context).textTheme.bodyLarge?.color,
                fontSize: 18,
                fontFamily: 'JetBrainsMono',
              ),
              onChanged: (String? newValue) {
                if (newValue != null) {
                  pluginsViewModel.sortPluginsBy(newValue);
                }
              },

              items: [
                DropdownMenuItem<String>(
                  value: 'fileName',
                  child: Text(
                    t.plugins_sort_filename,
                    style: TextStyle(fontSize: 30),
                  ),
                ),
                DropdownMenuItem<String>(
                  value: 'creator',
                  child: Text(
                    t.plugins_sort_creator,
                    style: TextStyle(fontSize: 30),
                  ),
                ),
                DropdownMenuItem<String>(
                  value: 'language',
                  child: Text(
                    t.plugins_sort_language,
                    style: TextStyle(fontSize: 30),
                  ),
                ),
              ],
            ),
            IconButton(
              icon: Icon(isAsc ? Icons.arrow_upward : Icons.arrow_downward),
              onPressed: () {
                pluginsViewModel.sortPluginsBy(currentSort, ascending: !isAsc);
              },
            ),
          ],
        ),

        Expanded(
          child: ListView.builder(
            itemCount: sortedList.length,
            itemBuilder: (context, index) {
              final plugin = sortedList[index];

              return Card(
                color: plugin.activeColor(context),
                child: ExpansionTile(
                  title: Text(plugin?.fileName ?? t.plugins_tile_no_name),
                  subtitle: Text(
                    plugin?.creator ?? t.plugins_tile_unknown_creator,
                  ),
                  leading: Icon(
                    plugin.active ? Icons.play_arrow : Icons.pause_circle,
                    color: Colors.black,
                  ),
                  children: [
                    Column(
                      children: [
                        Row(
                          children: [
                            Text(plugin.language.toString()),
                            Spacer(),
                            Text(
                              plugin?.tags.toString() ??
                                  t.plugins_tile_unknown_time,
                            ),
                          ],
                        ),
                        Row(
                          children: [
                            Text(
                              plugin?.active.toString() ??
                                  t.plugins_tile_unknown_status,
                            ),
                            Spacer(),
                            Text(
                              plugin?.updatedAt.toString() ??
                                  t.plugins_tile_unknown_severity,
                            ),
                          ],
                        ),

                        SizedBox(
                          width: double.infinity,
                          child: ElevatedButton(
                            onPressed: () {
                              showCronDialog(context, plugin, t);
                            },

                            child: Text(t.plugins_button_edit),
                          ),
                        ),
                        SizedBox(
                          width: double.infinity,
                          child: ElevatedButton(
                            onPressed: () {
                              showRunArgsDialog(
                                context,
                                plugin,
                                pluginsViewModel,
                                t,
                              );
                            },

                            child: Text('Run plugin'),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              );
            },
            padding: const EdgeInsets.all(10),
            scrollDirection: Axis.vertical,
          ),
        ),
      ],
    );
  }
}

void showCronDialog(BuildContext context, Plugin plugin, AppLocalizations t) {
  final cronController = TextEditingController(text: plugin.cronExpression);
  final argsController = TextEditingController(text: '');
  bool isActive = plugin.active;
  final theme = Theme.of(context);

  showDialog(
    context: context,
    builder: (context) {
      // StatefullBuilder pozwala na odświeżanie Switcha wewnątrz okienka dialogowego
      return StatefulBuilder(
        builder: (context, setState) {
          return AlertDialog(
            title: Text(t.plugins_cron_title(plugin.fileName)),
            content: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  TextField(
                    controller: cronController,
                    decoration: InputDecoration(
                      labelText: t.plugins_cron_field_cron,
                      hintText: '*/5 * * * *',
                    ),
                  ),
                  TextField(
                    controller: argsController,
                    style: theme.textTheme.bodyLarge,
                    decoration: InputDecoration(
                      labelText: t.plugins_cron_field_args,
                      hintText: '--verbose --save-logs',
                    ),
                  ),
                  const SizedBox(height: 16),
                  SwitchListTile(
                    title: Text(t.plugins_cron_switch_active),
                    value: isActive,
                    onChanged: (value) {
                      setState(() {
                        isActive = value;
                      });
                    },
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
                  final success = await context
                      .read<PluginsRepository>()
                      .activatePluginWithCron(
                        fileName: plugin.fileName,
                        extension: plugin.language.value,
                        cronExpression: cronController.text,
                        active: isActive,
                      );

                  if (context.mounted) {
                    Navigator.pop(context);
                    // snackbar z informacja zwrotna
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text(
                          success
                              ? t.plugins_cron_toast_success
                              : t.plugins_cron_toast_error,
                        ),
                      ),
                    );
                  }
                },
                child: Text(t.plugins_cron_button_save),
              ),
            ],
          );
        },
      );
    },
  );
}

void showRunArgsDialog(
  BuildContext context,
  Plugin plugin,
  PluginsViewModel viewModel,
  AppLocalizations t,
) {
  final argsController = TextEditingController();

  showDialog(
    context: context,
    builder: (context) {
      return AlertDialog(
        title: Text(
          t.plugins_args_title(plugin.fileName),
          style: Theme.of(context).textTheme.titleLarge,
        ),
        content: TextField(
          controller: argsController,
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
            onPressed: () {
              final String? enteredArgs = argsController.text.trim().isNotEmpty
                  ? argsController.text.trim()
                  : null;

              viewModel.startPlugin(plugin, arguments: enteredArgs);

              Navigator.pop(context);

              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text(
                    t.plugins_args_toast_triggered(plugin.fileName),
                  ),
                ),
              );
            },
            child: Text(t.plugins_args_button_run),
          ),
        ],
      );
    },
  );
}
