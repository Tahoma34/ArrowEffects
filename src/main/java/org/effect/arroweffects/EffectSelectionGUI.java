package org.effect.arroweffects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EffectSelectionGUI implements Listener {

    public static void openSelectionMenu(Player player, ArrowEffects plugin) {
        String rawTitle = plugin.getConfigManager().getConfig().getString("localization.gui.title",
                "&aВыберите эффект стрелы");
        String inventoryTitle = ChatColor.translateAlternateColorCodes('&', rawTitle);

        int size = plugin.getConfigManager().getConfig().getInt("localization.gui.size", 27);
        if (size < 9) size = 9;
        if (size % 9 != 0) size = 27;

        Inventory inventory = Bukkit.createInventory(null, size, inventoryTitle);

        ConfigurationSection effectsSection =
                plugin.getConfigManager().getConfig().getConfigurationSection("arrow-effects");
        if (effectsSection != null) {
            for (String key : effectsSection.getKeys(false)) {
                String rawDisplayName = effectsSection.getString(key + ".name", key);
                String effectDisplayName = ChatColor.translateAlternateColorCodes('&', rawDisplayName);

                int slot = effectsSection.getInt(key + ".slot", -1);
                if (slot < 0 || slot >= size) continue;

                String materialName = effectsSection.getString(key + ".material", "POTION");
                Material material = Material.matchMaterial(materialName);
                if (material == null) {
                    material = Material.GRASS_BLOCK;
                }

                ItemStack effectItem = new ItemStack(material);
                ItemMeta meta = effectItem.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(effectDisplayName);
                    effectItem.setItemMeta(meta);
                }

                inventory.setItem(slot, effectItem);
            }
        }

        int cancelSlot = plugin.getConfigManager().getConfig().getInt("localization.gui.cancel-effect-slot", 31);
        if (cancelSlot < 0 || cancelSlot >= size) {
            cancelSlot = size - 1;
        }

        String rawCancelName = plugin.getConfigManager().getConfig().getString("localization.gui.cancel-item-name",
                "&cСнять эффект");
        String cancelItemName = ChatColor.translateAlternateColorCodes('&', rawCancelName);

        ItemStack cancelItem = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.setDisplayName(cancelItemName);
            cancelItem.setItemMeta(cancelMeta);
        }
        inventory.setItem(cancelSlot, cancelItem);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Выберите эффект")) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        String cancelName = ChatColor.stripColor(
                ChatColor.translateAlternateColorCodes('&', ArrowEffects.getInstance()
                        .getConfigManager()
                        .getConfig()
                        .getString("localization.gui.cancel-item-name", "")));

        if (displayName.equalsIgnoreCase(cancelName)) {
            ArrowEffects.getInstance().getArrowEffectManager().cancelEffectForPlayer(player);
            player.closeInventory();
            player.sendMessage(ArrowEffects.getInstance().getConfigManager()
                    .getLocalizedMessage("effect-cancel"));
            return;
        }

        ConfigurationSection section = ArrowEffects.getInstance().getConfigManager()
                .getConfig().getConfigurationSection("arrow-effects");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                String nameFromConfig = section.getString(key + ".name");
                if (nameFromConfig != null && ChatColor.stripColor(
                        ChatColor.translateAlternateColorCodes('&', nameFromConfig)
                ).equalsIgnoreCase(displayName)) {

                    if (!player.hasPermission("arroweffects." + key)) {
                        player.sendMessage(ChatColor.RED + "У вас нет прав на использование эффекта " + displayName + "!");
                        return;
                    }

                    ArrowEffects.getInstance().getArrowEffectManager().setEffectForPlayer(player, key);
                    player.closeInventory();

                    String msg = ArrowEffects.getInstance().getConfigManager()
                            .getLocalizedMessage("effect-selected");
                    msg = msg.replace("{effect}", displayName);
                    player.sendMessage(msg);
                    return;
                }
            }
        }
    }
}