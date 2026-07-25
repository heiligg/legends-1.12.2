package com.heiligg.legends.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLegendaryPickaxe extends ItemPickaxe {

    public ItemLegendaryPickaxe(ToolMaterial material) {
        super(material);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        // Ensure new picks start with a slight efficiency feel via enchant-like NBT marker
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        stack.getTagCompound().setBoolean("LegendTool", true);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return super.getDestroySpeed(stack, state) * 1.25F;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        // Extra durability resilience: 20% chance not to consume durability
        if (!player.world.isRemote && player.world.rand.nextFloat() < 0.20F) {
            // Temporarily boost unbreakable-style by repairing 1 damage after break via event-less trick:
            // handled in onBlockDestroyed by healing one damage.
            if (!itemstack.hasTagCompound()) {
                itemstack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
            }
            itemstack.getTagCompound().setBoolean("LegendRepairPulse", true);
        }
        return super.onBlockStartBreak(itemstack, pos, player);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, net.minecraft.entity.EntityLivingBase entityLiving) {
        boolean result = super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
        if (!worldIn.isRemote && stack.hasTagCompound() && stack.getTagCompound().getBoolean("LegendRepairPulse")) {
            stack.getTagCompound().setBoolean("LegendRepairPulse", false);
            if (stack.isItemDamaged()) {
                stack.setItemDamage(Math.max(0, stack.getItemDamage() - 1));
            }
        }
        return result;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GOLD + "25% faster mining");
        tooltip.add(TextFormatting.GRAY + "Chance to spare durability");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
