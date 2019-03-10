package com.jecvay.ecosuites.espaymoney;

import com.google.inject.Inject;
import com.jecvay.ecosuites.espaymoney.Listeners.MiningListener;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

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

    private MainConfig mainConfig;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        /*
        * During this state, the plugin gets ready for initialization.
        * Access to a default logger instance and access to information
        * regarding preferred configuration file locations is available.
        * */
        mainConfig = new MainConfig(this, configDir);
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        /*
        * During this state, the plugin should finish any work needed in order to be functional.
        * Global event handlers should get registered in this stage.
        * */
        game.getEventManager().registerListeners(this, new MiningListener(this));
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
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("ESPayMoney starting");
    }

    @Listener
    public void onServerReload(GameReloadEvent event) {
        logger.info("ESPayMoney reloading");
        mainConfig.reload();
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

    public PluginContainer getContainer() {
        return pluginContainer;
    }
}
