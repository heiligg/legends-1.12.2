# Legends (Minecraft 1.12.2)

Forge adventure mod with legendary gear, world content, bosses, and **full superhero suit kits**.

## Superheroes (v3.5) — full kits

Controls (full suit required): **G** primary · **F** secondary · **V** special

### Iron Man
- Set: resistance, fire immunity, strength, mining boost
- **G** Repulsor blast · **F** Flight toggle · **V** Unibeam
- Sprint while flying for thruster boost + energy drain
- Night vision while airborne; thruster particles; Jarvis status lines

### Spider-Man
- Set: jump, speed, night vision; no fall damage
- **G** Web Zip · **F** Web Shot (slow/weaken) · **V** Spider Leap
- Sneak in air = glide; sneak + wall = cling
- Spider-Sense alerts when hostiles are nearby

### Flash
- Set: speed + haste; no fall damage
- **G** Speed Burst · **F** Speed Force toggle · **V** Blink teleport
- Water running while sprinting; lightning trail
- Speed Force: huge haste/mining; sprint hits deal bonus damage

### Captain America
- Set: resistance + absorption
- **G** Shield throw (needs Vibranium Shield) · **F** Shield Bash AOE · **V** Rally buffs
- Vibranium Shield: hold RMB block; sneak+RMB throw (returns)
- Reduced fall damage; stronger blocking

## Build

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
./gradlew build
```

Jar: `build/libs/legends-3.5.0.jar`
