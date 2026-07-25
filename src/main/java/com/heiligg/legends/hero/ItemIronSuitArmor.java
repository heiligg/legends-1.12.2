package com.heiligg.legends.hero;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
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

public class ItemIronSuitArmor extends ItemArmor {

    public static final String ENERGY_KEY = "HeroEnergy";

    private final IronSuitType suitType;

    public ItemIronSuitArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot, IronSuitType suitType) {
        super(material, renderIndex, slot);
        this.suitType = suitType;
    }

    public IronSuitType getSuitType() {
        return suitType;
    }

    public int getEnergy(ItemStack stack) {
        ensureTag(stack);
        if (!stack.getTagCompound().hasKey(ENERGY_KEY)) {
            stack.getTagCompound().setInteger(ENERGY_KEY, suitType.maxEnergy);
        }
        return stack.getTagCompound().getInteger(ENERGY_KEY);
    }

    public void setEnergy(ItemStack stack, int energy) {
        ensureTag(stack);
        if (energy < 0) {
            energy = 0;
        }
        if (energy > suitType.maxEnergy) {
            energy = suitType.maxEnergy;
        }
        stack.getTagCompound().setInteger(ENERGY_KEY, energy);
    }

    public void consumeEnergy(ItemStack stack, int amount) {
        setEnergy(stack, getEnergy(stack) - amount);
    }

    private void ensureTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
    }

    public static boolean isWearingFullSuit(EntityPlayer player, IronSuitType type) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) {
                continue;
            }
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemIronSuitArmor)) {
                return false;
            }
            if (((ItemIronSuitArmor) stack.getItem()).getSuitType() != type) {
                return false;
            }
        }
        return true;
    }

    public static IronSuitType getWornSuit(EntityPlayer player) {
        for (IronSuitType type : IronSuitType.values()) {
            if (isWearingFullSuit(player, type)) {
                return type;
            }
        }
        return null;
    }

    public static ItemStack getChest(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
    }

    public static boolean isWearingAnyIronSuit(EntityPlayer player) {
        return getWornSuit(player) != null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GOLD + "Iron Man — " + suitType.displayName);
        tooltip.add(TextFormatting.AQUA + "Energy: " + getEnergy(stack) + "/" + suitType.maxEnergy);
        tooltip.add(TextFormatting.GRAY + suitType.lore);
        tooltip.add(TextFormatting.YELLOW + suitType.controls);
        tooltip.add(TextFormatting.DARK_GRAY + "Full matching set required.");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
