# Legends (Minecraft 1.12.2)

Forge adventure mod built around the **Well of Legends** — ore and essence from fallen heroes, shrine trials, Ascended gear, and a full Heroes subcategory.

**Version:** 4.1.0

## Progression

```
Wellstone Ore → Fragments → Forged kit
         ↓
    Legend Essence → Legendary gear / Hero starter suits
         ↓
   Legend Shrine (Codex + Altar) → Guardian trial (3 phases)
         ↓
   Ascended Core → Ascended weapons, tools, armor / late Iron suits
```

Read the **Legend Codex** (shrine chest or book + Essence) for the in-game path.

## Fantasy loop

| Tier | Content |
|------|---------|
| **Forged** | Fragment + iron mid-tier armor/blade — resistance, half fall damage |
| **Legendary** | Essence + diamond gear — regen/resist set, dash (R), shockwave blade, staff bolts |
| **Ascended** | Core upgrades — stronger tools/weapons/armor (tuned set bonuses) |

World: Wellstone ore (overworld + nether), **Legend Shrines** (landmark + altar + loot), underground **Legend Ruins** (knights), Wraiths in biomes, **Legend Guardian** boss (phased fight; altar needs Shrine Brick foundation).

## Heroes subcategory

Creative tab **Legends Heroes** · **G / F / V** abilities · energy HUD

- Iron Man — 9 suits · Spider-Man · Flash · Captain America + Vibranium Shield

See prior notes / advancements under Call of Heroes.

## Config (`legends.cfg`)

Dash cost, ore/shrine/ruin rates, Guardian HP/damage/bolt cooldown, altar shrine requirement, Ascended potion amplifiers.

## Build

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
./gradlew build
```

Jar: `build/libs/legends-4.1.0.jar`

Playtest checklist: `docs/PLAYTEST.md`
