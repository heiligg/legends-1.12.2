package com.heiligg.legends.block;

import com.heiligg.legends.LegendsMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockNetherLegendOre extends Block {

    public BlockNetherLegendOre() {
        super(Material.ROCK, MapColor.NETHERRACK);
        setHardness(4.0F);
        setResistance(10.0F);
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
        return 2 + random.nextInt(3);
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return quantityDropped(random) + random.nextInt(fortune + 1);
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        return MathHelper.getInt(rand, 3, 7);
    }
}
