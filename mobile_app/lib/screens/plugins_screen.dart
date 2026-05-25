import 'package:alert_app/data/models/plugin_model.dart';
import 'package:alert_app/data/repositories/plugin_repository.dart';
import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

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
        final vm = context.read<PluginsViewModel>();
        vm.sortPluginsBy(vm.currentSortProperty);
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final pluginsViewModel = context.watch<PluginsViewModel>();
    final currentSort = pluginsViewModel.currentSortProperty;
    final isAsc = pluginsViewModel.isAscending;

    if (pluginsViewModel.pluginList.isEmpty) {
      return const Scaffold(
        body: Center(
          child: Text(
            "No plugins found. Consider checking the button in Debug Screen!",
          ),
        ),
      );
    }

    final sortedList = pluginsViewModel.sortedPlugins;

    return Column(
      children: [
        Row(
          children: [
            Text("Sort by - ", style: TextStyle(fontSize: 30)),
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

              items: const [
                DropdownMenuItem<String>(
                  value: 'fileName',
                  child: Text('File Name', style: TextStyle(fontSize: 30)),
                ),
                DropdownMenuItem<String>(
                  value: 'creator',
                  child: Text('Creator', style: TextStyle(fontSize: 30)),
                ),
                DropdownMenuItem<String>(
                  value: 'language',
                  child: Text('Language', style: TextStyle(fontSize: 30)),
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
                color: plugin.activeColor,
                child: ExpansionTile(
                  title: Text(plugin?.fileName ?? 'No name'),
                  subtitle: Text(plugin?.creator ?? 'Unknown creator'),
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
                            Text(plugin?.tags.toString() ?? 'Unknown Time'),
                          ],
                        ),
                        Row(
                          children: [
                            Text(plugin?.active.toString() ?? 'Unknown Status'),
                            Spacer(),
                            Text(
                              plugin?.updatedAt.toString() ??
                                  'Unknown Severity',
                            ),
                          ],
                        ),

                        SizedBox(
                          width: double.infinity,
                          child: ElevatedButton(
                            onPressed: () {
                              showCronDialog(context, plugin);
                            },

                            child: Text('Edit plugin'),
                          ),
                        ),
                        SizedBox(
                          width: double.infinity,
                          child: ElevatedButton(
                            onPressed: () {
                              pluginsViewModel.startPlugin(plugin);
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

void showCronDialog(BuildContext context, Plugin plugin) {
  final cronController = TextEditingController(text: plugin.cronExpression);
  bool isActive = plugin.active;

  showDialog(
    context: context,
    builder: (context) {
      // StatefullBuilder pozwala na odświeżanie Switcha wewnątrz okienka dialogowego
      return StatefulBuilder(
        builder: (context, setState) {
          return AlertDialog(
            title: Text('Plugin schedule: ${plugin.fileName}'),
            content: Column(
              mainAxisSize:
                  MainAxisSize.min, // Okienko dopasuje się do zawartości
              children: [
                TextField(
                  controller: cronController,
                  decoration: const InputDecoration(
                    labelText: 'CRON expression',
                    hintText: '*/5 * * * *',
                  ),
                ),
                const SizedBox(height: 16),
                SwitchListTile(
                  title: const Text('Active?'),
                  value: isActive,
                  onChanged: (value) {
                    setState(() {
                      isActive = value;
                    });
                  },
                ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('Cancel'),
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
                    Navigator.pop(context); // Zamykamy overlay
                    // Opcjonalnie: pokaż mały SnackBar z informacją o sukcesie
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text(success ? 'Saved' : 'Error')),
                    );
                  }
                },
                child: const Text('Save'),
              ),
            ],
          );
        },
      );
    },
  );
}
