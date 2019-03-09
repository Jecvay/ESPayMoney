package com.jecvay.ecosuites.espaymoney;

import com.typesafe.config.parser.ConfigNode;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ESPayMoney esp;
    private Logger logger;
    private CommentedConfigurationNode node = null;

    ConfigManager(ESPayMoney esp, Path path) {
        this.esp = esp;
        this.logger = esp.getLogger();
        this.configLoader = HoconConfigurationLoader.builder().setPath(path).build();
        try {
            if (!Files.exists(path)) {
                esp.getContainer().getAsset("config.conf").get().copyToFile(path);
            }
            this.node = this.configLoader.load();
            CommentedConfigurationNode blockNode = this.node.getNode("modules", "blockCheats", "enabled");
            logger.info("aaabbb", blockNode.getBoolean());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


}
