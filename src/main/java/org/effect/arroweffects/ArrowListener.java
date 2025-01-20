package org.effect.arroweffects;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ArrowListener implements Listener {

    private final ArrowEffects plugin;
    private final Map<UUID, BukkitTask> arrowTasks = new HashMap<>();

    public ArrowListener(ArrowEffects plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        String worldName = player.getWorld().getName();
        if (!player.hasPermission("arroweffects.bypass.world")
                && plugin.getConfigManager().isWorldBlacklisted(worldName)) {
            player.sendMessage(plugin.getConfigManager().getLocalizedMessage("world-blacklisted"));
            event.setCancelled(true);
            return;
        }

        String effect = plugin.getArrowEffectManager().getEffectForPlayer(player);
        if ("NONE".equalsIgnoreCase(effect)) {
            return;
        }

        if (event.getProjectile() instanceof Arrow arrow) {

            if (plugin.getArrowEffectManager().isGlowEnabled(player)) {
                arrow.setGlowing(true);
            }

            BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(
                    plugin,
                    () -> spawnArrowParticles(arrow, effect),
                    0L, 2L
            );
            arrowTasks.put(arrow.getUniqueId(), task);
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow) {
            UUID arrowId = arrow.getUniqueId();
            if (arrowTasks.containsKey(arrowId)) {
                arrowTasks.get(arrowId).cancel();
                arrowTasks.remove(arrowId);
            }
        }
    }

    private void spawnArrowParticles(Arrow arrow, String effect) {
        if (arrow.isDead() || !arrow.isValid()) {
            UUID arrowId = arrow.getUniqueId();
            if (arrowTasks.containsKey(arrowId)) {
                arrowTasks.get(arrowId).cancel();
                arrowTasks.remove(arrowId);
            }
            return;
        }

        Location loc = arrow.getLocation();

        if ("rainbow-wave".equalsIgnoreCase(effect)) {
            handleRainbowEffect(arrow, loc);
        } else if ("cubic".equalsIgnoreCase(effect)) {
            handleCubicEffect(arrow, loc);
        } else {
            List<String> particles = plugin.getConfigManager()
                    .getConfig()
                    .getStringList("arrow-effects." + effect + ".particles");

            for (String particleName : particles) {
                try {
                    Particle particle = Particle.valueOf(particleName.toUpperCase());
                    arrow.getWorld().spawnParticle(particle, loc, 5, 0.2, 0.2, 0.2, 0.01);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    private void handleRainbowEffect(Arrow arrow, Location loc) {
        Color[] rainbowColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE};
        int step = arrow.getTicksLived() % rainbowColors.length;
        Particle.DustOptions dustOptions = new Particle.DustOptions(rainbowColors[step], 1);
        arrow.getWorld().spawnParticle(Particle.REDSTONE, loc, 10, 0.2, 0.2, 0.2, dustOptions);
    }

    private void handleCubicEffect(Arrow arrow, Location loc) {
        String materialName = plugin.getConfigManager()
                .getConfig()
                .getString("arrow-effects.cubic.material", "GRASS_BLOCK") // Материал по умолчанию
                .toUpperCase();

        Material material;
        try {
            material = Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            material = Material.GRASS_BLOCK; // Резервный материал
        }

        arrow.getWorld().spawnParticle(
                Particle.BLOCK_CRACK,
                loc,
                10,
                0.5, 0.5, 0.5, // Радиус распространения частиц
                material.createBlockData()
        );
    }
}