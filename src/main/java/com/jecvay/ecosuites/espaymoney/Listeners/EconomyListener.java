package com.jecvay.ecosuites.espaymoney.Listeners;

import com.jecvay.ecosuites.espaymoney.ESPayMoney;
import com.jecvay.ecosuites.espaymoney.Manager.EconomyManager;
import com.jecvay.ecosuites.espaymoney.I18N;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.service.economy.EconomyService;

public class EconomyListener {
    private ESPayMoney esp;
    private EconomyManager economyManager;

    public EconomyListener(ESPayMoney esp, EconomyManager economyManager) {
        this.esp = esp;
        this.economyManager = economyManager;
        esp.getLogger().info(I18N.getString("eco.listener.init"));
    }

    @Listener
    public void onProviderChange(ChangeServiceProviderEvent event) {
        esp.getLogger().info("EconomyService changed!");
        if (event.getNewProvider() instanceof EconomyService) {
            economyManager.setEconomyService((EconomyService) event.getNewProvider());
        }
    }
}
