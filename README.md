# Legends (Minecraft 1.12.2)

Forge adventure/combat mod with legendary gear, shrines, hostile wraiths, a guardian boss, and an ascended endgame tier.

## Progression

1. Mine **Legend Ore** or loot fragments from **Legend Wraiths** / shrines
2. Craft **Legend Essence** and forge legendary gear
3. Find a **Legend Shrine** (or craft an altar) and offer essence to summon the **Legend Guardian**
4. Defeat the guardian for an **Ascended Core**, then upgrade gear to the ascended tier

## Features

- Legend Ore, Fragments, Essence, Bricks, Altar
- Legendary armor/weapons/tools + amulet
- Ascended armor + blade (guardian drops)
- Legend Wraith night mobs
- Legend Guardian boss (boss bar, ranged bolts)
- Shrine worldgen with chest loot
- Dash ability, shockwave, staff bolts, power HUD
- Advancements and `config/legends.cfg`

## Requirements

- Minecraft **1.12.2**
- Minecraft Forge **14.23.5.2860**
- Java **8** for building

## Build

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
./gradlew build
```

Jar: `build/libs/legends-2.0.0.jar`

## Controls

| Key | Ability | Requirement |
|-----|---------|-------------|
| R | Dash | Full legendary or ascended armor |
| Right-click blade | Shockwave | Legendary/Ascended blade |
| Right-click staff | Arcane Bolt | Legendary staff |
| Use altar + essence | Summon Guardian | Legend Altar |
