// SArrowCommand.java
package org.effect.arroweffects;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.List;

public class SArrowCommand implements CommandExecutor {

    private final ArrowEffects plugin;

    public SArrowCommand(ArrowEffects plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (sender instanceof Player player) {
                EffectSelectionGUI.openSelectionMenu(player, plugin);
                return true;
            }
            sender.sendMessage(plugin.getConfigManager().getLocalizedMessage("not-a-player"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "cancel" -> {
                if (sender instanceof Player player) {
                    plugin.getArrowEffectManager().cancelEffectForPlayer(player);
                    player.sendMessage(plugin.getConfigManager().getLocalizedMessage("effect-cancel"));
                } else {
                    sender.sendMessage(plugin.getConfigManager().getLocalizedMessage("not-a-player"));
                }
                return true;
            }
            case "glow" -> {
                if (sender instanceof Player player) {
                    if (!player.hasPermission("arroweffects.glow")) {
                        player.sendMessage(plugin.getConfigManager().getLocalizedMessage("no-permission"));
                        return true;
                    }
                    boolean isNowEnabled = plugin.getArrowEffectManager().toggleGlow(player);
                    if (isNowEnabled) {
                        player.sendMessage(ChatColor.GREEN + "Свечение стрел включено!");
                    } else {
                        player.sendMessage(ChatColor.RED + "Свечение стрел выключено!");
                    }
                } else {
                    sender.sendMessage(plugin.getConfigManager().getLocalizedMessage("not-a-player"));
                }
                return true;
            }
            case "help" -> {
                List<String> helpList = plugin.getConfigManager().getConfig()
                        .getStringList("localization.messages.help-command");
                if (!helpList.isEmpty()) {
                    for (String line : helpList) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                    }
                } else {
                    sender.sendMessage(ChatColor.GREEN + "/sarrow - открыть GUI выбора эффектов.");
                    sender.sendMessage(ChatColor.GREEN + "/sarrow cancel - отменить эффект стрелы.");
                    sender.sendMessage(ChatColor.GREEN + "/sarrow glow - включить/выключить свечение стрел.");
                    sender.sendMessage(ChatColor.GREEN + "/sarrow reload - перезагрузить конфигурацию.");
                }
                return true;
            }
            case "reload" -> {
                if (!sender.hasPermission("arroweffects.reload")) {
                    sender.sendMessage(plugin.getConfigManager().getLocalizedMessage("no-permission"));
                    return true;
                }
                plugin.getConfigManager().reload();
                sender.sendMessage(plugin.getConfigManager().getLocalizedMessage("reload-success"));
                return true;
            }
            default -> {
                sender.sendMessage(plugin.getConfigManager().getLocalizedMessage("unknown-command"));
                return true;
            }
        }
    }
}