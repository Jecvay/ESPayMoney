package com.jecvay.ecosuites.espaymoney;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MainConfig {

    final private String mainConfName = "config.conf";

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ESPayMoney esp;
    private Logger logger;
    private CommentedConfigurationNode node = null;

    private CommentedConfigurationNode miningNode= null;

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
            miningNode = this.node.getNode("pay_mining");
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

    public double getMineRemindMoney() {
        return miningNode.getNode("remind_money").getDouble();
    }

    public double getPayOtherBlock() {
        return miningNode.getNode("other_blocks").getDouble();
    }

    public double getPayBlock(final String blockId) {
        CommentedConfigurationNode priceNode = miningNode.getNode("blocks", blockId);
        if (priceNode.getValue() == null) {
            String blockCommonId = blockId.split("\\[", 2)[0];
            priceNode = miningNode.getNode("blocks", blockCommonId);
        }
        if (priceNode.getValue() == null) {
            return getPayOtherBlock();
        } else {
            return priceNode.getDouble();
        }
    }

}
