package com.jecvay.ecosuites.espaymoney.Listeners;

import com.jecvay.ecosuites.espaymoney.ESPayMoney;
import com.jecvay.ecosuites.espaymoney.I18N;
import org.slf4j.Logger;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.chat.ChatTypes;

import java.util.HashMap;
import java.util.Map;

public class EntityListener {
    private Logger logger;
    private Map<Player, Double> earnRecord;
    private double remindMoney;
    ESPayMoney esp;

    public EntityListener(ESPayMoney esp) {
        this.esp = esp;
        logger = esp.getLogger();
        remindMoney = esp.getMainConfig().getKillRemindMoney();
        earnRecord = new HashMap<>();
    }

    private void doWhenEarning(Player player, double money) {
        Double allEarn = earnRecord.getOrDefault(player,0.0) + money;
        earnRecord.put(player, allEarn);
        if (allEarn >= remindMoney) {
            player.sendMessage(ChatTypes.ACTION_BAR, I18N.getText("kill.remind_money", allEarn));
            earnRecord.put(player, 0.0);
        }
    }

    private boolean doEcoKilling(Player player, String entityId) {
        double price = esp.getMainConfig().getPayEntity(entityId);
        ResultType resultType = esp.getEconomyManager().easyAddMoney(player, price);
        if (resultType == ResultType.SUCCESS) {
            if (price >= 0) {
                doWhenEarning(player, price);
            }
            return true;
        } else if (resultType == ResultType.ACCOUNT_NO_FUNDS) {
            player.sendMessage(ChatTypes.ACTION_BAR, I18N.getText("kill.no_funds", -price));
            return false;
        } else if (resultType == ResultType.ACCOUNT_NO_SPACE) {
            return true;
        } else {
            return false;
        }
    }

    @Listener(order = Order.EARLY, beforeModifications = true)
    public void onKillEntity(DestructEntityEvent.Death event) {
        event.getCause().first(Player.class).ifPresent(player -> {
            Entity entity = event.getTargetEntity();
            String entityId = entity.getType().getId();

            // logger.debug("{} killed entity: {}, Creator: {}", player.getName(), entity.getType().getId(), entity.getCreator());
            if (player.gameMode().get() == GameModes.CREATIVE) {
                // return;
            }
            if (entity.getCreator().isPresent()) {
                // isn't nature
                return;
            }
            event.setCancelled(!doEcoKilling(player, entityId));

        });
    }
}
