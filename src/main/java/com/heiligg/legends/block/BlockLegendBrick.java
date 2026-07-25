package com.heiligg.legends.block;

import com.heiligg.legends.LegendsMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockLegendBrick extends Block {

    public BlockLegendBrick() {
        super(Material.ROCK);
        setHardness(2.5F);
        setResistance(12.0F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(LegendsMod.TAB);
    }
}
