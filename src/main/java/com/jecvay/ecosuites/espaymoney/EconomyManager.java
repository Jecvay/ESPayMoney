package com.jecvay.ecosuites.espaymoney;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Optional;

public class EconomyManager {
    private Currency defaultCurrency;
    private EconomyService economyService;
    private ESPayMoney esp;
    private EventContext evenContext;

    public EconomyManager(ESPayMoney esp) {
        this.esp = esp;
        evenContext = EventContext.builder().add(EventContextKeys.PLUGIN, esp.getContainer()).build();
        reloadEconomyProvider();
    }

    public void setEconomyService(EconomyService economyService) {
        this.economyService = economyService;
        defaultCurrency = economyService.getDefaultCurrency();
    }

    public void reloadEconomyProvider() {
        Optional<EconomyService> serviceOptional = Sponge.getServiceManager().provide(EconomyService.class);
        if (serviceOptional.isPresent()) {
            setEconomyService(serviceOptional.get());
        } else {
            esp.getLogger().error(I18N.get("plugin.no_economy"));
        }
    }

    public ResultType easyAddMoney(Player player, Double money) {
        if (money >= 0) {
            return deposit(player, money);
        } else if (money < 0) {
            return withdraw(player, -money);
        }
        return ResultType.FAILED;
    }

    public ResultType withdraw(Player player, Double money) {
        Optional<UniqueAccount> uOpt = economyService.getOrCreateAccount(player.getUniqueId());
        if (uOpt.isPresent()) {
            TransactionResult result = uOpt.get().withdraw(
                economyService.getDefaultCurrency(), BigDecimal.valueOf(money),
                Cause.of(evenContext, esp.getContainer())
            );
            return result.getResult();
        }
        return ResultType.FAILED;
    }

    public ResultType deposit(Player player, Double money) {
        Optional<UniqueAccount> uOpt = economyService.getOrCreateAccount(player.getUniqueId());
        if (uOpt.isPresent()) {
            TransactionResult result = uOpt.get().deposit(
                    economyService.getDefaultCurrency(), BigDecimal.valueOf(money),
                    Cause.of(evenContext, esp.getContainer())
            );
            return result.getResult();
        }
        return ResultType.FAILED;
    }

}
