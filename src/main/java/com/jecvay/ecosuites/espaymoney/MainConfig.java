package com.jecvay.ecosuites.espaymoney;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainConfig {

    final private String mainConfName = "config.conf";

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ESPayMoney esp;
    private Logger logger;
    private CommentedConfigurationNode node = null;

    MainConfig(ESPayMoney esp, Path configDir) {
        Path path = configDir.resolve(mainConfName);
        this.esp = esp;
        this.logger = esp.getLogger();
        this.configLoader = HoconConfigurationLoader.builder().setPath(path).build();
        try {
            if (!Files.exists(path)) {
                esp.getContainer().getAsset(mainConfName).get().copyToFile(path);
            } else {
                upgradeConf();
            }
            reload();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void reload() {
        try {
            this.node = this.configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("reload failed: {}", mainConfName);
        }
    }

    private void upgradeConf() {
        // merge config.conf from newer jar/assets.
    }

    public CommentedConfigurationNode getNode(@Nonnull final Object... keys) {
        return this.node.getNode(keys);
    }

}
