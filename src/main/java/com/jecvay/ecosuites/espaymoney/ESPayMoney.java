package com.jecvay.ecosuites.espaymoney;

import com.google.inject.Inject;
import com.jecvay.ecosuites.espaymoney.Listeners.EconomyListener;
import com.jecvay.ecosuites.espaymoney.Listeners.EntityListener;
import com.jecvay.ecosuites.espaymoney.Listeners.MiningListener;
import com.jecvay.ecosuites.espaymoney.Manager.ESPCommandManager;
import com.jecvay.ecosuites.espaymoney.Manager.EconomyManager;
import org.apache.commons.lang3.LocaleUtils;
import org.bstats.sponge.Metrics2;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

@Plugin(
        id = "espaymoney",
        name = "ESPayMoney",
        description = "Sponge plugin, pay money when some event emitted.",
        authors = {
                "Jecvay"
        }
)
public class ESPayMoney {

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private PluginContainer pluginContainer;

    @Inject
    private Metrics2 metrics;

    private MainConfig mainConfig;
    private EconomyManager economyManager;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        /*
        * During this state, the plugin gets ready for initialization.
        * Access to a default logger instance and access to information
        * regarding preferred configuration file locations is available.
        * */

        // init config
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                logger.error("Failed to create main config directory: {}", e);
            }
        }
        mainConfig = new MainConfig(this, configDir);

        // init i18n service
        I18N.setLogger(logger);
        I18N.setPlugin(this);
        String localeStr = mainConfig.getNode("lang").getString();
        if (localeStr == null || localeStr.isEmpty()) {
            localeStr = "en_US";
        }
        Locale locale = LocaleUtils.toLocale(localeStr);
        I18N.setLocale(locale);
    }

    private void registerCustomListeners() {
        // init economyManager
        economyManager = new EconomyManager(this);
        game.getEventManager().registerListeners(this, new EconomyListener(this, economyManager));

        // init miningListener
        if (mainConfig.getNode("modules", "pay_mining").getBoolean()) {
            game.getEventManager().registerListeners(this, new MiningListener(this));
            logger.info(I18N.getString("mining.enabled"));
        }

        // init entityListener
        if (mainConfig.getNode("modules", "pay_killing").getBoolean()) {
            game.getEventManager().registerListeners(this, new EntityListener(this));
            logger.info(I18N.getString("kill.enabled"));
        }
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        /*
        * During this state, the plugin should finish any work needed in order to be functional.
        * Global event handlers should get registered in this stage.
        * */

        registerCustomListeners();
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent event) {
        /*
        * By this state, inter-plugin communication should be ready to occur.
        * Plugins providing an API should be ready to accept basic requests.
        * */
    }

    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        /*
        * The server instance exists, and worlds are loaded. Command registration is handled during this state.
        * */
        CommandManager commandManager = Sponge.getCommandManager();
        commandManager.register(this, new ESPCommandManager(this).getSpec(), "espaymoney", "esp");
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info(I18N.getString("plugin.starting"));
    }

    @Listener
    public void onServerReload(GameReloadEvent event) {
        reloadPlugin();
    }

    public void reloadPlugin() {
        logger.info(I18N.getString("plugin.reload"));
        mainConfig.reload();
        // game.getEventManager().unregisterListeners(this);
        game.getEventManager().unregisterPluginListeners(this);
        registerCustomListeners();
    }

    @Listener
    public void onStopping(GameStoppingEvent event) {
        /*
         * This state occurs immediately before the final tick, before the worlds are saved.
         * */
    }

    public Logger getLogger() {
        return this.logger;
    }

    public MainConfig getMainConfig() {
        return this.mainConfig;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public PluginContainer getContainer() {
        return pluginContainer;
    }

    public Path getConfigDir() {
        return configDir;
    }
}
