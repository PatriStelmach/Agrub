import 'package:alert_app/logic/plugins_view_model.dart';
import 'package:alert_app/screens/plugin/plugin_card.dart';
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
        context.read<PluginsViewModel>().fetchPlugins();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    debugPrint("--- REBUILD PLUGINS SCREEN ---");
    final pluginsViewModel = context.watch<PluginsViewModel>();
    final t = AppLocalizations.of(context)!;

    if (pluginsViewModel.isLoading && pluginsViewModel.pluginsList.isEmpty) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    if (pluginsViewModel.pluginsList.isEmpty) {
      return Scaffold(body: Center(child: Text(t.plugins_no_plugins_found)));
    }

    final sortedList = pluginsViewModel.sortPlugins(
      pluginsViewModel.currentSortProperty,
      pluginsViewModel.isAscending,
    );

    return Column(
      children: [
        _buildSortHeader(context, pluginsViewModel, t),
        Expanded(
          child: ListView.builder(
            itemCount: sortedList.length,
            padding: const EdgeInsets.all(10),
            itemBuilder: (context, index) {
              final plugin = sortedList[index];
              return PluginCard(plugin: plugin, t: t);
            },
          ),
        ),
      ],
    );
  }

  Widget _buildSortHeader(
    BuildContext context,
    PluginsViewModel pluginsViewModel,
    AppLocalizations t,
  ) {
    final currentSort = pluginsViewModel.currentSortProperty;
    final isAsc = pluginsViewModel.isAscending;
    return Row(
      children: [
        Text(t.plugins_sort_by, style: const TextStyle(fontSize: 25)),
        DropdownButton<String>(
          padding: const EdgeInsets.all(6),
          borderRadius: const BorderRadius.all(Radius.circular(16)),
          value: currentSort,
          icon: const Icon(Icons.menu),
          style: TextStyle(
            color: Theme.of(context).textTheme.bodyLarge?.color,
            fontSize: 18,
            fontFamily: 'JetBrainsMono',
          ),
          onChanged: (String? newValue) {
            if (newValue != null) {
              pluginsViewModel.sortPluginsBy(newValue, isAsc);
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
          icon: Icon(isAsc ? Icons.arrow_upward : Icons.arrow_downward),
          onPressed: () {
            pluginsViewModel.sortPluginsBy(currentSort, !isAsc);
          },
        ),
      ],
    );
  }
}
