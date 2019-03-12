# ESPayMoney

### Sponge plugin, pay money when mining, or killing entities.
* Pay money to player who \[break a block/kill an entity\] which `price > 0`.
* If player have not enough money, then he cannot \[break the block/kill the entity\] which `price < 0`.
* You cannot earn from break the block which is placed by players, also cannot earn from kill the entity made by players.
* You can set different price to different block type.
* There will be a tip on the `Action Bar` every time when player earn `remind_money`
* You can custom the content of tips in `assets/i18n.properties`

### Commands
- `/esp reload` reload configuration and RE register listeners. (permission=`espaymoney.reload`)


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

```
# en_US, zh_CN
lang=en_US

modules {
    pay_mining = true,
    pay_killing = true,
}

pay_mining {
    remind_money: 1000,
    other_blocks: 1,
    blocks {
        "minecraft:sand" : 10,
        "minecraft:wool[color=black]" : -10,
        "minecraft:wool" : -1,
        "minecraft:stone" : 1,
        "minecraft:stone[variant=granite]" : 2,
    }
}

pay_killing {
    remind_money: 500,
    other_entity: 1,
    entities {
        "minecraft:chicken" : -20,
        "minecraft:cow": 20,
    }
}
```
