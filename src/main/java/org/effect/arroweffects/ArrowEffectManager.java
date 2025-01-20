package org.effect.arroweffects;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArrowEffectManager {

    private final Map<UUID, String> playerEffects = new HashMap<>();
    private final Map<UUID, Boolean> playerGlow = new HashMap<>();

    public String getEffectForPlayer(Player player) {
        return playerEffects.getOrDefault(player.getUniqueId(), "NONE");
    }

    public void setEffectForPlayer(Player player, String effectName) {
        playerEffects.put(player.getUniqueId(), effectName);
    }

    public void cancelEffectForPlayer(Player player) {
        playerEffects.put(player.getUniqueId(), "NONE");
    }

    public boolean isGlowEnabled(Player player) {
        return playerGlow.getOrDefault(player.getUniqueId(), false);
    }

    public boolean toggleGlow(Player player) {
        boolean current = isGlowEnabled(player);
        playerGlow.put(player.getUniqueId(), !current);
        return !current;
    }
}