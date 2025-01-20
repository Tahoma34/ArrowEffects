// ConfigManager.java
package org.effect.arroweffects;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final ArrowEffects plugin;
    private FileConfiguration config;

    public ConfigManager(ArrowEffects plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean isWorldBlacklisted(String worldName) {
        return config.getStringList("blacklisted-worlds").contains(worldName);
    }

    public String getLocalizedMessage(String path) {
        String msg = config.getString("localization.messages." + path, "Сообщение не найдено.");
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}