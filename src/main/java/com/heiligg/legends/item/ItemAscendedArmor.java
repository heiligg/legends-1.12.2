package com.heiligg.legends.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAscendedArmor extends ItemArmor {

    public static final int MAX_POWER = 150;
    public static final String POWER_KEY = "AscendedPower";

    public ItemAscendedArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
    }

    public int getPower(ItemStack stack) {
        ensureTag(stack);
        if (!stack.getTagCompound().hasKey(POWER_KEY)) {
            stack.getTagCompound().setInteger(POWER_KEY, MAX_POWER);
        }
        return stack.getTagCompound().getInteger(POWER_KEY);
    }

    public void setPower(ItemStack stack, int power) {
        ensureTag(stack);
        if (power < 0) {
            power = 0;
        }
        if (power > MAX_POWER) {
            power = MAX_POWER;
        }
        stack.getTagCompound().setInteger(POWER_KEY, power);
    }

    public void consumePower(ItemStack stack, int amount) {
        setPower(stack, getPower(stack) - amount);
    }

    public void recharge(ItemStack stack) {
        int power = getPower(stack);
        if (power < MAX_POWER) {
            setPower(stack, power + 2);
        }
    }

    private void ensureTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.LIGHT_PURPLE + "Power: " + getPower(stack) + "/" + MAX_POWER);
        tooltip.add(TextFormatting.GOLD + "Full set: Strength, Absorption, Dash");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
