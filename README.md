# Legends (Minecraft 1.12.2)

Forge adventure mod with legendary gear, world content, bosses, and a **complete Heroes subcategory**.

## Heroes (v4.0) — finished kit

Creative tab: **Legends Heroes**  
Controls (full matching set): **G** primary · **F** secondary · **V** special  
Energy HUD shows the active hero/suit and ability line. Flight, Speed Force, and cloak sync to the client.

### Iron Man — 9 suits

| Suit | Craft path | Abilities |
|------|------------|-----------|
| **Mark I** | Iron + coal | G Flamethrower · F Rocket Jump · V Smoke Screen |
| **Mark III** | Iron + essence | G Repulsor · F Flight · V Unibeam |
| **Mark V** | Mark III + gold | G Quick Repulsor · F Flight · V Pulse Wave |
| **Mark VII** | Mark III + diamond | G Repulsor · F Flight · V Flare Burst |
| **Mark XLII** | Mark VII + gold blocks | G Repulsor · F Flight · V Auto-Repair |
| **War Machine** | Mark III + iron blocks / TNT | G Missile Barrage · F Hover · V Minigun |
| **Hulkbuster** | War Machine + obsidian | G Power Fist · F Heavy Hover · V Ground Pound |
| **Mark L** | Mark XLII + diamond / nether star | G Nano-Repulsor · F Flight · V Nanite Surge |
| **Stealth** | Mark V + ender pearls | G Suppressed Shot · F Silent Flight · V Cloak |

### Spider-Man
- **G** Web Zip (pulls toward looked blocks) · **F** Web Shot · **V** Spider Leap
- Sneak glide / wall cling · Spider-Sense

### Flash
- **G** Speed Burst · **F** Speed Force toggle · **V** Blink
- Water running · momentum melee · mining boost

### Captain America
- **G** Shield throw (needs Vibranium Shield) · **F** Bash · **V** Rally
- Vibranium Shield: hold RMB to block · sneak+RMB to throw (returns)

### Hero advancements
Call of Heroes → Suit Up / Spider / Flash / Cap → War Machine, Mark L, Vibranium Shield, Assemble

## Legendary loop

Ore → essence → legendary / ascended gear, altar, totem, elixir, shrines, ruins, Wraith / Knight / Guardian boss.

## Build

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
./gradlew build
```

Jar: `build/libs/legends-4.0.0.jar`
