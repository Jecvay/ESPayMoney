package com.jecvay.ecosuites.espaymoney.Listeners;

import com.jecvay.ecosuites.espaymoney.ESPayMoney;
import com.jecvay.ecosuites.espaymoney.I18N;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

public class MiningListener {

    private Logger logger;
    ESPayMoney esp;
    public MiningListener(ESPayMoney esp) {
        logger = esp.getLogger();
        this.esp = esp;
    }

    private boolean doEcoMining(Player player, String blockId) {
        double price = esp.getMainConfig().getPayBlock(blockId);
        logger.debug("{} break block: {}", player.getName(), blockId);
        player.sendMessage(Text.of(
                TextColors.BLUE, player.getName(),
                TextColors.GRAY, " break block: ",
                TextColors.GREEN, blockId,
                TextColors.WHITE, "[", price, "]"
        ));

        ResultType resultType = esp.getEconomyManager().easyAddMoney(player, price);
        if (resultType == ResultType.SUCCESS) {
            if (price >= 0) {
                player.sendMessage(Text.of(MessageFormat.format(I18N.get("mining.eco.add"), Double.toString(price))));
            } else {
                player.sendMessage(Text.of(MessageFormat.format(I18N.get("mining.eco.sub"), Double.toString(-price))));
            }
            return true;
        } else if (resultType == ResultType.ACCOUNT_NO_FUNDS) {
            player.sendMessage(ChatTypes.ACTION_BAR, Text.of(
                    MessageFormat.format(I18N.get("mining.no_funds"), -price)));
            return false;
        } else if (resultType == ResultType.ACCOUNT_NO_SPACE) {
            return true;
        } else {
            return false;
        }
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onBreakBlock(ChangeBlockEvent.Break event, @Root Player player) {
        event.getTransactions().forEach(trans->{
            String blockId = trans.getOriginal().getState().getId();
            Optional<UUID> creator = trans.getOriginal().getCreator();
            boolean isNatureOre = !creator.isPresent();
            if (isNatureOre)  {
                // Query mining cost and pay to player
                event.setCancelled(doEcoMining(player, blockId));
            }
        });
    }
}
