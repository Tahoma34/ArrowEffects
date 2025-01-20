package org.effect.arroweffects;

import org.bukkit.plugin.java.JavaPlugin;

public class ArrowEffects extends JavaPlugin {

    private static ArrowEffects instance;
    private ConfigManager configManager;
    private ArrowEffectManager arrowEffectManager;

    public static ArrowEffects getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ArrowEffectManager getArrowEffectManager() {
        return arrowEffectManager;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configManager = new ConfigManager(this);
        arrowEffectManager = new ArrowEffectManager();

        getCommand("sarrow").setExecutor(new SArrowCommand(this));

        getServer().getPluginManager().registerEvents(new ArrowListener(this), this);
        getServer().getPluginManager().registerEvents(new EffectSelectionGUI(), this);

        getLogger().info("ArrowEffects плагин включён!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ArrowEffects плагин выключен!");
    }
}


//        ######################################
//        #  ArrowEffects - Premium Edition    #
//        #  by Tahoma34                       #
//        #  Version: 1.4 - SNAPSHOT           #
//        ######################################