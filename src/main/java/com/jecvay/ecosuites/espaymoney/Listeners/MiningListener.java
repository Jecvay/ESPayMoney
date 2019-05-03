package com.jecvay.ecosuites.espaymoney.Listeners;

import com.jecvay.ecosuites.espaymoney.ESPayMoney;
import com.jecvay.ecosuites.espaymoney.I18N;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class MiningListener {

    private Logger logger;
    private Map<Player, Double> earnRecord;
    private double remindMoney;

    ESPayMoney esp;
    public MiningListener(ESPayMoney esp) {
        logger = esp.getLogger();
        this.esp = esp;
        earnRecord = new HashMap<>();
        remindMoney = esp.getMainConfig().getMineRemindMoney();
    }

    private void doWhenEarning(Player player, double money) {
        Double allEarn = earnRecord.getOrDefault(player,0.0) + money;
        earnRecord.put(player, allEarn);
        if (allEarn >= remindMoney) {
            player.sendMessage(ChatTypes.ACTION_BAR, I18N.getText("mining.remind_money", allEarn));
            earnRecord.put(player, 0.0);
        }
    }

    private boolean doEcoMining(Player player, List<String> blockList) {
        double totalEarn = 0;
        for (String blockId : blockList) {
            double price = esp.getMainConfig().getPayBlock(blockId);
            totalEarn += price;
        }
        ResultType resultType = esp.getEconomyManager().easyAddMoney(player, totalEarn);
        if (resultType == ResultType.SUCCESS) {
            if (totalEarn >= 0) {
                // player.sendMessage(I18N.getText("debug.mining.eco.add", Double.toString(price));
                doWhenEarning(player, totalEarn);
            } else {
                // player.sendMessageI18N.getText("debug.mining.eco.sub", Double.toString(-price));
            }
            return true;
        } else if (resultType == ResultType.ACCOUNT_NO_FUNDS) {
            player.sendMessage(ChatTypes.ACTION_BAR, I18N.getText("mining.no_funds", -totalEarn));
            return false;
        } else if (resultType == ResultType.ACCOUNT_NO_SPACE) {
            return true;
        } else {
            return false;
        }

    }

    @Listener(order = Order.EARLY, beforeModifications = true)
    public void onBreakBlock(ChangeBlockEvent.Break event, @Root Player player) {
        UUID uuid = player.getUniqueId();
        boolean isDebug = esp.getDebugMode().isDebug(uuid);
        boolean isEdit = esp.getDebugMode().isEdit(uuid);
        List<String> blockList = new ArrayList<>();
        if (!isDebug && player.gameMode().get() == GameModes.CREATIVE) {
            return;
        }
        event.getTransactions().forEach(trans->{
            if (!trans.isValid()) {
                return;
            }
            String blockId = trans.getOriginal().getState().getId();
            Optional<UUID> creator = trans.getOriginal().getCreator();
            boolean isNatureOre = !creator.isPresent();

            if (!isDebug && isNatureOre)  {
                blockList.add(blockId);
            }

            if (isDebug) {
                event.setCancelled(true);
                double price = esp.getMainConfig().getPayBlock(blockId);
                player.sendMessage(Text.of(
                    TextColors.GRAY, "ID: ",
                    TextColors.GREEN, blockId,
                    TextColors.WHITE, "  price=[", price, "]"
                ));
            }
            if (isEdit) {
                event.setCancelled(true);
                esp.getMainConfig().addPayBlock(blockId, player);
            }
        });

        if (blockList.size() > 0) {
            // Query mining cost and pay to player
            boolean ecoSuccess = doEcoMining(player, blockList);

            if (!ecoSuccess) {
                event.setCancelled(true);
            }
        }
    }
}
