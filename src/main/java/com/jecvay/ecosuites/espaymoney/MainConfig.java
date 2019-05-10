package com.jecvay.ecosuites.espaymoney;

import com.jecvay.ecosuites.espaymoney.Utils.PriceRange;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
    private Map<String, PriceRange> fuzzyBlockCache;
    private Map<String, PriceRange> fuzzyEntityCache;

    // Permission Group Name --> Factor
    private Map<String, Float> discountMap;
    private Map<String, Float> gainMap;

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

            fuzzyBlockCache = new HashMap<>();
            fuzzyEntityCache = new HashMap<>();
            initFuzzyCache();

            discountMap = new HashMap<>();
            gainMap = new HashMap<>();
            initVipMap();
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("reload failed: {}", mainConfName);
        }
    }

    private void initFuzzyCache() {
        ConfigurationNode root = miningNode.getNode("blocks");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : root.getChildrenMap().entrySet()) {
            String key = (String) entry.getKey();
            if (key == null || key.length() == 0 || !key.startsWith("@")) continue;
            ConfigurationNode node = entry.getValue();
            PriceRange priceRange = new PriceRange(node.getString());
            fuzzyBlockCache.put(key, priceRange);
        }
    }

    private void initVipMap() {
        ConfigurationNode discountNode = miningNode.getNode("discount");
        ConfigurationNode gainNode = miningNode.getNode("gain");
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : discountNode.getChildrenMap().entrySet()) {
            String key = (String) entry.getKey();
            if (key == null || key.length() == 0) continue;
            ConfigurationNode node = entry.getValue();
            float factor = node.getFloat();
            discountMap.put(key, factor);
        }
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : gainNode.getChildrenMap().entrySet()) {
            String key = (String) entry.getKey();
            if (key == null || key.length() == 0) continue;
            ConfigurationNode node = entry.getValue();
            float factor = node.getFloat();
            gainMap.put(key, factor);
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
        if (priceCache.containsKey(blockId)) {
            return priceCache.get(blockId).get();
        }
        CommentedConfigurationNode priceNode = miningNode.getNode("blocks", blockId);

        // search fully match
        if (priceNode.getValue() != null) {
            PriceRange priceRange = new PriceRange(priceNode.getString());
            priceCache.put(blockId, priceRange);
            return priceRange.get();
        }

        // search first matched traits
        String blockCommonId = blockId.split("\\[", 2)[0];
        priceNode = miningNode.getNode("blocks", blockCommonId);
        if (priceNode.getValue() != null) {
            PriceRange priceRange = new PriceRange(priceNode.getString());
            priceCache.put(blockId, priceRange);
            return priceRange.get();
        }

        // fuzzy match
        for (Map.Entry<String, PriceRange> entry : fuzzyBlockCache.entrySet()) {
            if (blockId.contains(entry.getKey().split("@")[1])) {
               PriceRange priceRange = entry.getValue();
               priceCache.put(blockId, priceRange);
               return priceRange.get();
            }
        }

        return getPayOtherBlock();
    }

    public void addPayBlock(final String blockId, Player player) {
        String preValue = miningNode.getNode("blocks", blockId).getString();
        if (preValue == null || preValue.length() == 0) {
            miningNode.getNode("blocks", blockId).setValue("-1~1");
            try {
                this.configLoader.save(this.node);
                player.sendMessage(Text.of(
                        TextColors.GRAY, "ID: ",
                        TextColors.GREEN, blockId,
                        TextColors.WHITE, "Added to config"
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            player.sendMessage(I18N.getText("cmd.edit.already_have"));
        }
    }

    public double getPayOtherEntity() {
        if (!configCache.containsKey("other_entity")) {
            String value = killNode.getNode("other_entity").getString();
            configCache.put("other_entity", new PriceRange(value));
        }
        return configCache.get("other_entity").get();
    }

    public double getPayEntity(final String entityId) {
        if (priceCache.containsKey(entityId)) {
            return priceCache.get(entityId).get();
        }
        CommentedConfigurationNode priceNode = miningNode.getNode("entities", entityId);

        // search fully match
        if (priceNode.getValue() != null) {
            PriceRange priceRange = new PriceRange(priceNode.getString());
            priceCache.put(entityId, priceRange);
            return priceRange.get();
        }

        // search first matched traits
        String blockCommonId = entityId.split("\\[", 2)[0];
        priceNode = miningNode.getNode("entities", blockCommonId);
        if (priceNode.getValue() != null) {
            PriceRange priceRange = new PriceRange(priceNode.getString());
            priceCache.put(entityId, priceRange);
            return priceRange.get();
        }

        // fuzzy match
        for (Map.Entry<String, PriceRange> entry : fuzzyBlockCache.entrySet()) {
            if (entityId.contains(entry.getKey().split("@")[1])) {
                PriceRange priceRange = entry.getValue();
                priceCache.put(entityId, priceRange);
                return priceRange.get();
            }
        }

        return getPayOtherEntity();
    }

    public float getGainFactor(String permGroupName) {
        if (gainMap.containsKey(permGroupName)) {
            return gainMap.get(permGroupName);
        }
        return -1;
    }

    public float getDiscountFactor(String permGroupName) {
        if (discountMap.containsKey(permGroupName)) {
            return discountMap.get(permGroupName);
        }
        return -1;
    }

}
