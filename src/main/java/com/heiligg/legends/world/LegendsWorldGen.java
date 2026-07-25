package com.heiligg.legends.world;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.config.LegendsConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class LegendsWorldGen implements IWorldGenerator {

    private final WorldGenLegendShrine shrineGen = new WorldGenLegendShrine();
    private final WorldGenLegendRuin ruinGen = new WorldGenLegendRuin();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 0 || LegendsMod.legendOre == null) {
            return;
        }

        int range = Math.max(1, LegendsConfig.oreMaxY - LegendsConfig.oreMinY);
        WorldGenMinable generator = new WorldGenMinable(LegendsMod.legendOre.getDefaultState(), 5);
        for (int i = 0; i < LegendsConfig.oreVeinsPerChunk; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int y = LegendsConfig.oreMinY + random.nextInt(range);
            int z = chunkZ * 16 + random.nextInt(16);
            generator.generate(world, random, new BlockPos(x, y, z));
        }

        if (random.nextInt(Math.max(1, LegendsConfig.shrineChance)) == 0) {
            int x = chunkX * 16 + 8 + random.nextInt(8);
            int z = chunkZ * 16 + 8 + random.nextInt(8);
            shrineGen.generate(world, random, new BlockPos(x, 0, z));
        }

        if (random.nextInt(Math.max(1, LegendsConfig.ruinChance)) == 0) {
            int x = chunkX * 16 + 4 + random.nextInt(8);
            int z = chunkZ * 16 + 4 + random.nextInt(8);
            ruinGen.generate(world, random, new BlockPos(x, 0, z));
        }
    }
}
