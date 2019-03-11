# ESPayMoney

### Sponge plugin, pay money when mining.
* Pay money to player who break a block (when mining) which `price > 0`.
* If player have not enough money, then he cannot break any blocks which `price < 0`.
* You can set different price to different block type.
* There will be a tip on the `Action Bar` every time when player earn `remind_money`
* You can custom the content of tips in `assets/i18n.properties`

### Configuration

```
lang=zh_CN

modules {
    pay_mining {
        enabled=true
    }
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
```
