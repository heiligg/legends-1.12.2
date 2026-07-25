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

public class WorldGenLegendShrine extends WorldGenerator {

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (LegendsMod.legendBrick == null || LegendsMod.legendAltar == null) {
            return false;
        }

        BlockPos base = worldIn.getHeight(position);
        if (base.getY() < 60 || base.getY() > 90) {
            return false;
        }

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                BlockPos ground = base.add(x, -1, z);
                if (!worldIn.getBlockState(ground).isOpaqueCube()) {
                    return false;
                }
            }
        }

        IBlockState brick = LegendsMod.legendBrick.getDefaultState();

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = 0; y <= 5; y++) {
                    worldIn.setBlockState(base.add(x, y, z), Blocks.AIR.getDefaultState(), 2);
                }
                worldIn.setBlockState(base.add(x, 0, z), brick, 2);
            }
        }

        for (int i = -3; i <= 3; i++) {
            worldIn.setBlockState(base.add(i, 1, -3), brick, 2);
            worldIn.setBlockState(base.add(i, 1, 3), brick, 2);
            worldIn.setBlockState(base.add(-3, 1, i), brick, 2);
            worldIn.setBlockState(base.add(3, 1, i), brick, 2);
        }

        int[][] corners = {{-3, -3}, {-3, 3}, {3, -3}, {3, 3}};
        for (int[] c : corners) {
            for (int y = 1; y <= 4; y++) {
                worldIn.setBlockState(base.add(c[0], y, c[1]), brick, 2);
            }
            worldIn.setBlockState(base.add(c[0], 5, c[1]), Blocks.GLOWSTONE.getDefaultState(), 2);
        }

        worldIn.setBlockState(base.add(0, 1, 0), LegendsMod.legendAltar.getDefaultState(), 2);
        worldIn.setBlockState(base.add(0, 0, 0), brick, 2);
        worldIn.setBlockState(base.add(0, 1, 3), Blocks.AIR.getDefaultState(), 2);
        worldIn.setBlockState(base.add(0, 2, 3), Blocks.AIR.getDefaultState(), 2);

        BlockPos chestPos = base.add(0, 1, -2);
        worldIn.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 2);
        TileEntity te = worldIn.getTileEntity(chestPos);
        if (te instanceof TileEntityChest && LegendsMod.legendFragment != null) {
            TileEntityChest chest = (TileEntityChest) te;
            chest.setInventorySlotContents(0, new ItemStack(LegendsMod.legendFragment, 2 + rand.nextInt(3)));
            if (LegendsMod.legendEssence != null && rand.nextFloat() < 0.45F) {
                chest.setInventorySlotContents(1, new ItemStack(LegendsMod.legendEssence, 1));
            }
        }

        return true;
    }
}
