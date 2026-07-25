# Legends 4.1 — survival playtest checklist

Use a fresh world (or creative smoke, then survival).

## Smoke (creative / `/give`)

- [ ] All new items appear in **Legends** / **Legends Heroes** tabs
- [ ] Codex right-click cycles lore pages
- [ ] Forged / Legendary / Ascended armor equip without crash
- [ ] Ascended pick/axe/shovel mine correctly
- [ ] Hero G/F/V keys fire with full sets
- [ ] Flight / Speed Force / cloak HUD updates after toggle

## Survival path

1. [ ] Mine Wellstone Ore → fragments / smelt
2. [ ] Craft Forged Blade + one armor piece (fragment + iron)
3. [ ] Craft Essence (4 fragments) → Codex
4. [ ] Craft Mark I or Spider starter without bricking progression
5. [ ] Find or craft Shrine Brick path; locate a **Legend Shrine** landmark
6. [ ] Offer Essence at shrine altar → Guardian spawns
7. [ ] Guardian phases announce (II barrage, III wellshock)
8. [ ] Kill Guardian → Ascended Core drop
9. [ ] Craft Ascended Blade + Ascended Pickaxe
10. [ ] Full Legendary set: dash (R) costs power; fall cancel works
11. [ ] Solo player-built altar **without** enough Shrine Brick is rejected (if config on)

## Balance notes to watch

- Legendary recipes need diamonds + essence (not essence-only chests)
- Ascended set no longer stacks Absorb + Regen II by default
- Early hero flight still strong — shrine/Guardian should feel like the mid-game spike

## Build verification

```bash
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
./gradlew build
```
