# Legends (Minecraft 1.12.2)

Forge adventure/combat mod with legendary gear, shrines, underground ruins, hostile mobs, a guardian boss, and an ascended endgame tier.

## Progression

1. Mine **Legend Ore**, fight **Legend Wraiths**, or loot **shrines/ruins**
2. Craft **Legend Essence** and forge legendary gear
3. Clear **underground ruins** guarded by **Legend Knights**
4. Offer essence at a **Legend Altar** to fight the **Legend Guardian**
5. Upgrade with **Ascended Core** into ascended armor + blade

## Features

- Worldgen: Legend Ore, surface shrines, underground ruins
- Mobs: Legend Wraith, Legend Knight, Legend Guardian (boss)
- Gear: legendary + ascended armor/weapons/tools/bow/staff, shield, amulet
- Consumables: Legend Elixir, Legend Totem (death save)
- Smelting: Legend Ore → Fragments
- Combat polish: shield block bonus, ascended damage resist
- Dash, shockwave, staff bolts, power HUD
- Advancements and `config/legends.cfg`

## Requirements

- Minecraft **1.12.2** / Forge **14.23.5.2860**
- Java **8** to build

## Build

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
./gradlew build
```

Jar: `build/libs/legends-2.2.0.jar`

## Controls

| Input | Ability |
|-------|---------|
| R | Dash (full legendary/ascended armor) |
| Right-click blade | Shockwave |
| Right-click staff | Arcane Bolt |
| Altar + essence | Summon Guardian |
| Totem in hotbar/offhand | Prevent death once |
