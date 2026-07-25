package com.heiligg.legends.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LegendsConfig {

    public static int dashPowerCost = 25;
    public static int oreVeinsPerChunk = 5;
    public static int oreMinY = 8;
    public static int oreMaxY = 48;
    public static boolean fullSetCancelsFallDamage = true;
    public static int wraithSpawnWeight = 6;
    public static int shrineChance = 20;
    public static int ruinChance = 30;
    public static int netherOreVeinsPerChunk = 7;

    public static double guardianHealth = 200.0D;
    public static double guardianDamage = 11.0D;
    public static int guardianBoltCooldown = 40;
    public static boolean altarRequiresShrine = true;
    public static int altarEssenceCost = 1;
    public static int ascendedRegenAmplifier = 0;
    public static int ascendedResistAmplifier = 0;

    public static void load(File file) {
        Configuration config = new Configuration(file);
        try {
            config.load();
            dashPowerCost = config.getInt(
                    "dashPowerCost",
                    "abilities",
                    25,
                    1,
                    100,
                    "Legend power consumed by dash"
            );
            fullSetCancelsFallDamage = config.getBoolean(
                    "fullSetCancelsFallDamage",
                    "abilities",
                    true,
                    "Cancel fall damage while wearing a full legendary/ascended set"
            );
            ascendedRegenAmplifier = config.getInt(
                    "ascendedRegenAmplifier",
                    "abilities",
                    0,
                    0,
                    3,
                    "Regeneration amplifier for full Ascended set (0 = Regen I)"
            );
            ascendedResistAmplifier = config.getInt(
                    "ascendedResistAmplifier",
                    "abilities",
                    0,
                    0,
                    3,
                    "Resistance amplifier for full Ascended set (0 = Resist I)"
            );
            oreVeinsPerChunk = config.getInt(
                    "oreVeinsPerChunk",
                    "world",
                    5,
                    0,
                    32,
                    "Legend ore veins generated per overworld chunk"
            );
            oreMinY = config.getInt("oreMinY", "world", 8, 0, 255, "Minimum Y for legend ore");
            oreMaxY = config.getInt("oreMaxY", "world", 48, 1, 255, "Maximum Y for legend ore");
            if (oreMaxY <= oreMinY) {
                oreMaxY = oreMinY + 1;
            }
            wraithSpawnWeight = config.getInt(
                    "wraithSpawnWeight",
                    "mobs",
                    6,
                    0,
                    100,
                    "Spawn weight for Legend Wraiths in monster biomes"
            );
            shrineChance = config.getInt(
                    "shrineChance",
                    "world",
                    20,
                    1,
                    200,
                    "1-in-N chance per chunk to attempt a Legend Shrine"
            );
            ruinChance = config.getInt(
                    "ruinChance",
                    "world",
                    30,
                    1,
                    300,
                    "1-in-N chance per chunk to attempt an underground Legend Ruin"
            );
            netherOreVeinsPerChunk = config.getInt(
                    "netherOreVeinsPerChunk",
                    "world",
                    7,
                    0,
                    32,
                    "Nether Legend Ore veins per nether chunk"
            );
            guardianHealth = config.getFloat(
                    "guardianHealth",
                    "mobs",
                    200.0F,
                    40.0F,
                    1000.0F,
                    "Legend Guardian max health"
            );
            guardianDamage = config.getFloat(
                    "guardianDamage",
                    "mobs",
                    11.0F,
                    1.0F,
                    50.0F,
                    "Legend Guardian melee damage"
            );
            guardianBoltCooldown = config.getInt(
                    "guardianBoltCooldown",
                    "mobs",
                    40,
                    10,
                    200,
                    "Ticks between Guardian arcane bolts (phase 1)"
            );
            altarRequiresShrine = config.getBoolean(
                    "altarRequiresShrine",
                    "world",
                    true,
                    "Guardian summon requires nearby Legend Brick (shrine-style arena)"
            );
            altarEssenceCost = config.getInt(
                    "altarEssenceCost",
                    "world",
                    1,
                    1,
                    8,
                    "Legend Essence consumed to awaken the Guardian"
            );
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
