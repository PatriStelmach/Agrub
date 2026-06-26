import 'package:alert_app/data/models/plugin_model.dart';
import 'package:alert_app/l10n/app_localizations.dart';
import 'package:alert_app/screens/plugin/card_elements/cron_edit_dialog.dart';
import 'package:alert_app/screens/plugin/card_elements/plugin_dialog.dart';
import 'package:flutter/material.dart';

class PluginCard extends StatelessWidget {
  final Plugin plugin;
  final AppLocalizations t;

  const PluginCard({super.key, required this.plugin, required this.t});
  @override
  Widget build(BuildContext context) {
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
                    child: Text(t.plugins_button_run),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
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
    builder: (_) => PluginDialog(plugin: plugin),
  );
}
