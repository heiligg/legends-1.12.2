package com.heiligg.legends.world;

import com.heiligg.legends.LegendsMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

/**
 * Multi-level Legend Shrine landmark: approach stairs, courtyard, pedestal room.
 */
public class WorldGenLegendShrine extends WorldGenerator {

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (LegendsMod.legendBrick == null || LegendsMod.legendAltar == null) {
            return false;
        }

        BlockPos base = worldIn.getHeight(position);
        if (base.getY() < 62 || base.getY() > 95) {
            return false;
        }

        for (int x = -5; x <= 5; x++) {
            for (int z = -6; z <= 5; z++) {
                BlockPos ground = base.add(x, -1, z);
                if (!worldIn.getBlockState(ground).getMaterial().isSolid()) {
                    return false;
                }
            }
        }

        IBlockState brick = LegendsMod.legendBrick.getDefaultState();
        IBlockState air = Blocks.AIR.getDefaultState();
        IBlockState glow = Blocks.GLOWSTONE.getDefaultState();
        IBlockState slab = Blocks.STONE_SLAB.getDefaultState();

        // Clear volume
        for (int x = -5; x <= 5; x++) {
            for (int z = -6; z <= 5; z++) {
                for (int y = 0; y <= 8; y++) {
                    worldIn.setBlockState(base.add(x, y, z), air, 2);
                }
            }
        }

        // Courtyard floor
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                worldIn.setBlockState(base.add(x, 0, z), brick, 2);
            }
        }

        // Outer low walls + pillars
        for (int i = -5; i <= 5; i++) {
            worldIn.setBlockState(base.add(i, 1, -5), brick, 2);
            worldIn.setBlockState(base.add(i, 1, 5), brick, 2);
            worldIn.setBlockState(base.add(-5, 1, i), brick, 2);
            worldIn.setBlockState(base.add(5, 1, i), brick, 2);
        }
        int[][] pillars = {{-5, -5}, {-5, 5}, {5, -5}, {5, 5}, {0, -5}, {0, 5}, {-5, 0}, {5, 0}};
        for (int[] p : pillars) {
            for (int y = 1; y <= 5; y++) {
                worldIn.setBlockState(base.add(p[0], y, p[1]), brick, 2);
            }
            worldIn.setBlockState(base.add(p[0], 6, p[1]), glow, 2);
        }

        // Approach stairs (south opening)
        for (int z = -6; z <= -5; z++) {
            worldIn.setBlockState(base.add(0, 0, z), brick, 2);
            worldIn.setBlockState(base.add(-1, 0, z), brick, 2);
            worldIn.setBlockState(base.add(1, 0, z), brick, 2);
        }
        worldIn.setBlockState(base.add(0, 1, -5), air, 2);
        worldIn.setBlockState(base.add(0, 2, -5), air, 2);
        worldIn.setBlockState(base.add(0, 1, -6), slab, 2);

        // Inner pedestal room (raised)
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                worldIn.setBlockState(base.add(x, 1, z), brick, 2);
            }
        }
        for (int y = 2; y <= 4; y++) {
            for (int i = -2; i <= 2; i++) {
                if (Math.abs(i) == 2) {
                    worldIn.setBlockState(base.add(i, y, -2), brick, 2);
                    worldIn.setBlockState(base.add(i, y, 2), brick, 2);
                    worldIn.setBlockState(base.add(-2, y, i), brick, 2);
                    worldIn.setBlockState(base.add(2, y, i), brick, 2);
                }
            }
        }
        // Doorway north of pedestal into room from courtyard
        worldIn.setBlockState(base.add(0, 2, -2), air, 2);
        worldIn.setBlockState(base.add(0, 3, -2), air, 2);

        // Altar pedestal
        worldIn.setBlockState(base.add(0, 2, 0), LegendsMod.legendAltar.getDefaultState(), 2);
        worldIn.setBlockState(base.add(0, 1, 0), brick, 2);
        worldIn.setBlockState(base.add(1, 2, 1), glow, 2);
        worldIn.setBlockState(base.add(-1, 2, 1), glow, 2);
        worldIn.setBlockState(base.add(1, 2, -1), glow, 2);
        worldIn.setBlockState(base.add(-1, 2, -1), glow, 2);

        // Loot chest
        BlockPos chestPos = base.add(0, 1, 4);
        worldIn.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 2);
        TileEntity te = worldIn.getTileEntity(chestPos);
        if (te instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) te;
            if (LegendsMod.legendFragment != null) {
                chest.setInventorySlotContents(0, new ItemStack(LegendsMod.legendFragment, 3 + rand.nextInt(4)));
            }
            if (LegendsMod.legendEssence != null && rand.nextFloat() < 0.55F) {
                chest.setInventorySlotContents(1, new ItemStack(LegendsMod.legendEssence, 1 + rand.nextInt(2)));
            }
            if (LegendsMod.legendCodex != null) {
                chest.setInventorySlotContents(2, new ItemStack(LegendsMod.legendCodex));
            }
            if (LegendsMod.legendBrickItem != null) {
                chest.setInventorySlotContents(3, new ItemStack(LegendsMod.legendBrickItem, 4 + rand.nextInt(5)));
            }
        }

        return true;
    }
}
