package com.heiligg.legends.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLegendaryAxe extends ItemAxe {

    public ItemLegendaryAxe(ToolMaterial material) {
        // Attack damage / speed match diamond axe-ish but stronger via material
        super(material, 10.0F, -3.0F);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        // Strip a 3x3 of logs around the clicked block when sneaking
        if (!player.isSneaking()) {
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }

        ItemStack stack = player.getHeldItem(hand);
        int broken = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos target = pos.add(dx, dy, dz);
                    net.minecraft.block.state.IBlockState state = worldIn.getBlockState(target);
                    if (state.getBlock().isWood(worldIn, target)) {
                        if (!worldIn.isRemote) {
                            worldIn.destroyBlock(target, true);
                        }
                        broken++;
                    }
                }
            }
        }

        if (broken > 0) {
            if (!worldIn.isRemote) {
                stack.damageItem(Math.max(1, broken / 3), player);
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GOLD + "Sneak + use: clear nearby logs");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
