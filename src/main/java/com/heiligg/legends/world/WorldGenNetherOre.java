package com.heiligg.legends.world;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.config.LegendsConfig;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;

public final class WorldGenNetherOre {

    private WorldGenNetherOre() {
    }

    public static void generate(World world, Random random, int chunkX, int chunkZ) {
        if (LegendsMod.netherLegendOre == null) {
            return;
        }
        WorldGenMinable generator = new WorldGenMinable(
                LegendsMod.netherLegendOre.getDefaultState(),
                6,
                BlockMatcher.forBlock(Blocks.NETHERRACK)
        );
        for (int i = 0; i < LegendsConfig.netherOreVeinsPerChunk; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int y = 10 + random.nextInt(100);
            int z = chunkZ * 16 + random.nextInt(16);
            generator.generate(world, random, new BlockPos(x, y, z));
        }
    }
}
