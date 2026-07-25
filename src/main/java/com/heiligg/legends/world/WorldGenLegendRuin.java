package com.heiligg.legends.world;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.entity.EntityLegendKnight;
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
 * Small underground ruin chamber guarded by Legend Knights.
 */
public class WorldGenLegendRuin extends WorldGenerator {

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        if (LegendsMod.legendBrick == null) {
            return false;
        }

        int y = 12 + rand.nextInt(28);
        BlockPos origin = new BlockPos(position.getX(), y, position.getZ());

        // Prefer carving into solid stone
        int solid = 0;
        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                for (int dy = 0; dy < 5; dy++) {
                    if (worldIn.getBlockState(origin.add(x, dy, z)).isOpaqueCube()) {
                        solid++;
                    }
                }
            }
        }
        if (solid < 80) {
            return false;
        }

        IBlockState brick = LegendsMod.legendBrick.getDefaultState();

        for (int x = 0; x < 7; x++) {
            for (int z = 0; z < 7; z++) {
                for (int dy = 0; dy < 5; dy++) {
                    BlockPos p = origin.add(x, dy, z);
                    boolean wall = x == 0 || x == 6 || z == 0 || z == 6 || dy == 0 || dy == 4;
                    if (wall) {
                        worldIn.setBlockState(p, brick, 2);
                    } else {
                        worldIn.setBlockState(p, Blocks.AIR.getDefaultState(), 2);
                    }
                }
            }
        }

        // Entrance hole
        worldIn.setBlockState(origin.add(3, 1, 0), Blocks.AIR.getDefaultState(), 2);
        worldIn.setBlockState(origin.add(3, 2, 0), Blocks.AIR.getDefaultState(), 2);

        // Lighting
        worldIn.setBlockState(origin.add(1, 1, 1), Blocks.TORCH.getDefaultState(), 2);
        worldIn.setBlockState(origin.add(5, 1, 5), Blocks.TORCH.getDefaultState(), 2);

        // Loot chest
        BlockPos chestPos = origin.add(3, 1, 5);
        worldIn.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 2);
        TileEntity te = worldIn.getTileEntity(chestPos);
        if (te instanceof TileEntityChest) {
            TileEntityChest chest = (TileEntityChest) te;
            if (LegendsMod.legendFragment != null) {
                chest.setInventorySlotContents(0, new ItemStack(LegendsMod.legendFragment, 3 + rand.nextInt(4)));
            }
            if (LegendsMod.legendEssence != null && rand.nextFloat() < 0.55F) {
                chest.setInventorySlotContents(2, new ItemStack(LegendsMod.legendEssence, 1));
            }
            if (LegendsMod.legendAmulet != null && rand.nextFloat() < 0.12F) {
                chest.setInventorySlotContents(4, new ItemStack(LegendsMod.legendAmulet));
            }
            if (LegendsMod.legendElixir != null && rand.nextFloat() < 0.4F) {
                chest.setInventorySlotContents(6, new ItemStack(LegendsMod.legendElixir, 1 + rand.nextInt(2)));
            }
        }

        // Spawn knights
        int knights = 1 + rand.nextInt(2);
        for (int i = 0; i < knights; i++) {
            EntityLegendKnight knight = new EntityLegendKnight(worldIn);
            knight.setLocationAndAngles(
                    origin.getX() + 2.5D + i,
                    origin.getY() + 1.0D,
                    origin.getZ() + 3.5D,
                    rand.nextFloat() * 360.0F,
                    0.0F
            );
            knight.onInitialSpawn(worldIn.getDifficultyForLocation(knight.getPosition()), null);
            worldIn.spawnEntity(knight);
        }

        return true;
    }
}
