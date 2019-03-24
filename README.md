# ESPayMoney

#### *Earn or Spend by mining or kill entity!*

### Why you need
As X-ray mod become more and more stronger, we should anti-xray by limit the rate of getting ore. If mine a stone will earn $1 in game, then mine a iron-ore should cost $50 ~ $100. and player can decide selling this iron-ore or keep them. this is a good way to anti-xray. even you allow players to use xray mod, they will not get benefit from it.

And if you create a Boss Mob which give player lots of precious items at harvest time, then cost money to kill this mob is a good way to make the server's economy more balance.

### Feature
* You can set price of blocks and entities, **support random range for price**.
* Pay money to player who \[break a block/kill an entity\] which `price > 0`.
* If player have not enough money, then he cannot \[break the block/kill the entity\] which `price < 0`.
* You cannot earn from break the block which is placed by players, also cannot earn from kill the entity made by players.
* You can set different price to different block type.
* There will be a tip on the `Action Bar` every time when player earn `remind_money`
* You can custom the content of tips in `config/espaymoney/lang` folder

### Commands
- `/esp reload` reload configuration and RE register listeners. (permission=`espaymoney.admin.reload`)
- `/esp debug` enable/disable your debug mode. In this mode plugin will print the info of block when you break one. (permission=`espaymoney.admin.debug`)
- `/esp fast` enable/disable your FastEdit mode. In this mode plugin will auto add block's info into your config file with a default value of "-1~1" when you break one. (permission=`espaymoney.admin.edit`)


### Permissions

##### admin
- `espaymoney.reload`

##### player
- `<None>`


### Modify messages' translation
1. Edit config.conf, set `lang=en_US` or `lang=zh_CN`
1. After server started, in your plugin's config folder there will be a lang folder which contain the i18n[_zh_CN].properties file.
1. Edit this file and restart your server.


### Configuration

```hocon
# en_US, zh_CN
lang=en_US

modules {
    pay_mining = true,
    pay_killing = true,
}

pay_mining {
    remind_money = 1000,
    other_blocks = 30~40,
    blocks {
        "minecraft:sand" = -10~15,
        "minecraft:wool[color=black]" = -10~10,
        "minecraft:wool" = -1,
        "minecraft:stone" = 1,
        "minecraft:stone[variant=granite]" = 2,
        
        # fuzzy match
        "@blue"=233
        "@yellow"=-233

        # auto added by fastedit mode
        "minecraft:wool[color=white]"="-1~1"
    }
}

pay_killing {
    remind_money = 500,
    other_entity = 10~20,
    entities {
        "minecraft:chicken" = -20,
        "minecraft:cow" = -5~15,
    }
}
```

### Support
[Discord](https://discord.gg/z6s3yhg)
