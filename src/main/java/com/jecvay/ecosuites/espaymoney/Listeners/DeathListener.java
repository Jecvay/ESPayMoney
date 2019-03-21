package com.jecvay.ecosuites.espaymoney.Listeners;

import com.jecvay.ecosuites.espaymoney.ESPayMoney;
import com.jecvay.ecosuites.espaymoney.Manager.EconomyManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.service.economy.transaction.ResultType;

public class DeathListener {
    private ESPayMoney esp;
    private EconomyManager economyManager;

    public DeathListener(ESPayMoney esp, EconomyManager economyManager) {
        this.esp = esp;
        this.economyManager = economyManager;
    }

    @Listener
    public void onPlayerDie(DestructEntityEvent.Death event) {
        if(event.getTargetEntity() instanceof Player) {
            Player player = (Player)event.getTargetEntity();
            ResultType result = economyManager.easyAddMoney(player, 100.0D);
            if (result == ResultType.SUCCESS) {
                //
            } else {
                //
            }
        }
    }
}
