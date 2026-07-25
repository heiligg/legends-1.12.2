package com.heiligg.legends.init;

import com.heiligg.legends.LegendsMod;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class ModRecipes {

    private ModRecipes() {
    }

    public static void register() {
        if (LegendsMod.legendOreItem != null && LegendsMod.legendFragment != null) {
            GameRegistry.addSmelting(LegendsMod.legendOreItem, new ItemStack(LegendsMod.legendFragment, 2), 0.7F);
        }
    }
}
