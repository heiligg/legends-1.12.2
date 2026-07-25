package com.heiligg.legends.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class LegendsConfig {

    public static int dashPowerCost = 20;
    public static int oreVeinsPerChunk = 6;
    public static int oreMinY = 8;
    public static int oreMaxY = 48;
    public static boolean fullSetCancelsFallDamage = true;

    public static void load(File file) {
        Configuration config = new Configuration(file);
        try {
            config.load();
            dashPowerCost = config.getInt(
                    "dashPowerCost",
                    "abilities",
                    20,
                    1,
                    100,
                    "Legend power consumed by dash"
            );
            fullSetCancelsFallDamage = config.getBoolean(
                    "fullSetCancelsFallDamage",
                    "abilities",
                    true,
                    "Cancel fall damage while wearing the full legendary set"
            );
            oreVeinsPerChunk = config.getInt(
                    "oreVeinsPerChunk",
                    "world",
                    6,
                    0,
                    32,
                    "Legend ore veins generated per overworld chunk"
            );
            oreMinY = config.getInt("oreMinY", "world", 8, 0, 255, "Minimum Y for legend ore");
            oreMaxY = config.getInt("oreMaxY", "world", 48, 1, 255, "Maximum Y for legend ore");
            if (oreMaxY <= oreMinY) {
                oreMaxY = oreMinY + 1;
            }
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
