package com.jecvay.ecosuites.espaymoney.Listeners;

import com.jecvay.ecosuites.espaymoney.ESPayMoney;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.Optional;
import java.util.UUID;

public class MiningListener {

    private Logger logger;
    public MiningListener(ESPayMoney esp) {
        logger = esp.getLogger();
    }

    @Listener
    public void onBreakBlock(ChangeBlockEvent.Break event) {
        event.getTransactions().forEach(trans->{
            Optional<UUID> creator = trans.getOriginal().getCreator();
            boolean isPresent = creator.isPresent();
            if (isPresent) {
                logger.info("No");
            } else {
                logger.info("Yes");
            }
        });
    }
}
