package com.jecvay.ecosuites.espaymoney.Manager;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DebugMode {

    private Map<UUID, PlayerDebugInfo> playerDebugInfoMap;

    public DebugMode() {
        playerDebugInfoMap = new HashMap<>();
    }

    private PlayerDebugInfo touchPlayerInfo(UUID uuid) {
        if (!playerDebugInfoMap.containsKey(uuid)) {
            PlayerDebugInfo playerDebugInfo = new PlayerDebugInfo(uuid);
            playerDebugInfoMap.put(uuid, playerDebugInfo);
            return playerDebugInfo;
        } else {
            return playerDebugInfoMap.get(uuid);
        }
    }

    public boolean isDebug(UUID uuid) {
        PlayerDebugInfo playerDebugInfo = touchPlayerInfo(uuid);
        return playerDebugInfo.isDebug();
    }

    public void setDebug(UUID uuid, boolean tag) {
        PlayerDebugInfo playerDebugInfo = touchPlayerInfo(uuid);
        playerDebugInfo.setDebug(tag);
    }

    public boolean isEdit(UUID uuid) {
        PlayerDebugInfo playerDebugInfo = touchPlayerInfo(uuid);
        return playerDebugInfo.isEdit();
    }

    public void setEdit(UUID uuid, boolean tag) {
        PlayerDebugInfo playerDebugInfo = touchPlayerInfo(uuid);
        playerDebugInfo.setEdit(tag);
    }
}

class PlayerDebugInfo {
    private Boolean debugTag = false;
    private Boolean editTag = false;
    private UUID uuid = null;

    PlayerDebugInfo(UUID uuid) {
        this.uuid = uuid;
    }

    private Player getPlayer() {
        Optional<Player> playerOptional = Sponge.getServer().getPlayer(uuid);
        return playerOptional.orElse(null);
    }

    boolean isDebug() {
        return debugTag;
    }

    void setDebug(boolean tag) {
        debugTag = tag;
        Player player = getPlayer();
        if (player != null) {
            if (tag) {
                player.offer(Keys.GAME_MODE, GameModes.CREATIVE);
            } else {
                player.offer(Keys.GAME_MODE, GameModes.SURVIVAL);
            }
        }
    }

    boolean isEdit() {
        return editTag;
    }

    void setEdit(boolean tag) {
        editTag = tag;
    }

}
