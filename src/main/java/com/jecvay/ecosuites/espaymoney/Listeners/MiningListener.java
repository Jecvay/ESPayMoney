package com.jecvay.ecosuites.espaymoney.Listeners;

import com.jecvay.ecosuites.espaymoney.ESPayMoney;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;

import java.util.Optional;
import java.util.UUID;

public class MiningListener {

    private Logger logger;
    public MiningListener(ESPayMoney esp) {
        logger = esp.getLogger();
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onBreakBlock(ChangeBlockEvent.Break event) {
        // skip the events which are not emitted by player.
        Cause cause = event.getCause();
        Optional<Player> player = cause.first(Player.class);
        if (!player.isPresent()) {
            return;
        }
        event.getTransactions().forEach(trans->{
            Optional<UUID> creator = trans.getOriginal().getCreator();
            boolean isPresent = creator.isPresent();
            if (isPresent) {
                logger.info("No");
                event.setCancelled(true);
            } else {
                logger.info("Yes");
            }
        });
    }
}
