package com.jecvay.ecosuites.espaymoney;

import com.jecvay.ecosuites.espaymoney.Utils.PriceRange;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MainConfig {

    final private String mainConfName = "config.conf";

    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ESPayMoney esp;
    private Logger logger;
    private CommentedConfigurationNode node = null;

    private CommentedConfigurationNode miningNode = null;
    private CommentedConfigurationNode killNode = null;

    private Map<String, PriceRange> configCache;
    private Map<String, PriceRange> priceCache;

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
            killNode = this.node.getNode("pay_killing");
            configCache = new HashMap<>();
            priceCache = new HashMap<>();
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
        if (!configCache.containsKey("remind_money")) {
            String value = miningNode.getNode("remind_money").getString();
            configCache.put("remind_money", new PriceRange(value));
        }
        return configCache.get("remind_money").get();
    }

    public double getKillRemindMoney() {
        if (!configCache.containsKey("remind_money")) {
            String value = killNode.getNode("remind_money").getString();
            configCache.put("remind_money", new PriceRange(value));
        }
        return configCache.get("remind_money").get();
    }

    public double getPayOtherBlock() {
        if (!configCache.containsKey("other_blocks")) {
            String value = miningNode.getNode("other_blocks").getString();
            configCache.put("other_blocks", new PriceRange(value));
        }
        return configCache.get("other_blocks").get();
    }

    public double getPayBlock(final String blockId) {
        CommentedConfigurationNode priceNode = miningNode.getNode("blocks", blockId);
        String blockCommonId = blockId;

        // search first matched traits
        if (priceNode.getValue() == null) {
            blockCommonId = blockId.split("\\[", 2)[0];
            priceNode = miningNode.getNode("blocks", blockCommonId);
        }
        if (priceNode.getValue() == null) {
            return getPayOtherBlock();
        }

        // get from cache
        if (!priceCache.containsKey(blockCommonId)) {
            priceCache.put(blockCommonId, new PriceRange(priceNode.getString()));
        }
        return priceCache.get(blockCommonId).get();
    }

    public double getPayOtherEntity() {
        if (!configCache.containsKey("other_entity")) {
            String value = killNode.getNode("other_entity").getString();
            configCache.put("other_entity", new PriceRange(value));
        }
        return configCache.get("other_entity").get();
    }

    public double getPayEntity(final String entityId) {
        CommentedConfigurationNode priceNode = killNode.getNode("entities", entityId);
        String entityCommonId = entityId;
        if (priceNode.getValue() == null) {
            entityCommonId = entityId.split("\\[", 2)[0];
            priceNode = killNode.getNode("entities", entityCommonId);
        }
        if (priceNode.getValue() == null) {
            return getPayOtherEntity();
        }

        // get from cache
        if (!priceCache.containsKey(entityCommonId)) {
            priceCache.put(entityCommonId, new PriceRange(priceNode.getString()));
        }
        return priceCache.get(entityCommonId).get();
    }

}
