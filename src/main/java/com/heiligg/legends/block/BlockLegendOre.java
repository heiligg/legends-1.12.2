package com.heiligg.legends.block;

import com.heiligg.legends.LegendsMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockLegendOre extends Block {

    public BlockLegendOre() {
        super(Material.ROCK);
        setHardness(3.5F);
        setResistance(8.0F);
        setHarvestLevel("pickaxe", 2);
        setSoundType(SoundType.STONE);
        setCreativeTab(LegendsMod.TAB);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return LegendsMod.legendFragment;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1 + random.nextInt(2);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0) {
            int bonus = random.nextInt(fortune + 2) - 1;
            if (bonus < 0) {
                bonus = 0;
            }
            return quantityDropped(random) * (bonus + 1);
        }
        return quantityDropped(random);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        return MathHelper.getInt(rand, 2, 5);
    }
}
