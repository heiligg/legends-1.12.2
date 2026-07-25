# Legends (Minecraft 1.12.2)

Forge mod that adds legendary armor, weapons, trinkets, ore, and hero abilities.

## Features

- **Legend Ore / Fragments** – overworld ore that drops fragments; craft 4 into essence
- **Legendary Armor** – full set grants regeneration, resistance, and a dash ability
- **Legendary Blade** – high-damage sword with a shockwave special
- **Legendary Bow** – fires a triple volley with bonus damage
- **Legendary Staff** – launches arcane bolt projectiles
- **Legendary Pickaxe / Axe** – faster mining and sneak multi-log clearing
- **Legend Amulet** – hotbar/offhand trinket for Night Vision and Speed
- **Legend Essence** – crafting material (also found in dungeon-like loot chests)
- Power HUD while wearing legendary armor
- Advancements for discovering and forging legendary gear

## Requirements

- Minecraft **1.12.2**
- Minecraft Forge **14.23.5.2860**
- Java **8** for building

## Build

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
./gradlew build
```

The mod jar is written to `build/libs/legends-1.3.0.jar`.

## Controls

| Key | Ability | Requirement |
|-----|---------|-------------|
| R | Dash | Full legendary armor + power |
| Right-click (blade) | Shockwave | Legendary blade in hand |
| Right-click (staff) | Arcane Bolt | Legendary staff in hand |

## Config

On first run Forge writes `config/legends.cfg` with:

- dash power cost
- fall-damage cancel for the full armor set
- legend ore vein count / Y range

## Creative Tab

Look under **Legends** in the creative inventory.
