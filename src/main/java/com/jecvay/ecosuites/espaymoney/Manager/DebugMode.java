package com.jecvay.ecosuites.espaymoney.Manager;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DebugMode {

    private Map<UUID, PlayerDebugInfo> playerDebugInfoMap;

    public DebugMode() {
        playerDebugInfoMap = new HashMap<>();
    }

    private PlayerDebugInfo touchPlayerInfo(UUID uuid) {
        if (!playerDebugInfoMap.containsKey(uuid)) {
            PlayerDebugInfo playerDebugInfo = new PlayerDebugInfo();
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

    boolean isDebug() {
        return debugTag;
    }

    void setDebug(boolean tag) {
        debugTag = tag;
    }

    boolean isEdit() {
        return editTag;
    }

    void setEdit(boolean tag) {
        editTag = tag;
    }

}
