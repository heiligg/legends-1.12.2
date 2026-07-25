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

public class ItemHeroArmor extends ItemArmor {

    public static final String ENERGY_KEY = "HeroEnergy";

    private final HeroType heroType;

    public ItemHeroArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot, HeroType heroType) {
        super(material, renderIndex, slot);
        this.heroType = heroType;
    }

    public HeroType getHeroType() {
        return heroType;
    }

    public int getEnergy(ItemStack stack) {
        ensureTag(stack);
        if (!stack.getTagCompound().hasKey(ENERGY_KEY)) {
            stack.getTagCompound().setInteger(ENERGY_KEY, heroType.maxEnergy);
        }
        return stack.getTagCompound().getInteger(ENERGY_KEY);
    }

    public void setEnergy(ItemStack stack, int energy) {
        ensureTag(stack);
        if (energy < 0) {
            energy = 0;
        }
        if (energy > heroType.maxEnergy) {
            energy = heroType.maxEnergy;
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

    public static boolean isWearingFullSet(EntityPlayer player, HeroType type) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) {
                continue;
            }
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemHeroArmor)) {
                return false;
            }
            if (((ItemHeroArmor) stack.getItem()).getHeroType() != type) {
                return false;
            }
        }
        return true;
    }

    public static HeroType getWornHeroSet(EntityPlayer player) {
        for (HeroType type : HeroType.values()) {
            if (isWearingFullSet(player, type)) {
                return type;
            }
        }
        return null;
    }

    public static ItemStack getChest(EntityPlayer player) {
        return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.YELLOW + heroType.displayName + " Suit Piece");
        tooltip.add(TextFormatting.AQUA + "Energy: " + getEnergy(stack) + "/" + heroType.maxEnergy);
        tooltip.add(TextFormatting.DARK_GRAY + "Wear the full set to unlock abilities.");
        switch (heroType) {
            case IRON_MAN:
                tooltip.add(TextFormatting.GOLD + "G Repulsor | F Flight | V Unibeam");
                tooltip.add(TextFormatting.GRAY + "Sprint while flying for thruster boost");
                break;
            case SPIDER_MAN:
                tooltip.add(TextFormatting.GOLD + "G Web Zip | F Web Shot | V Spider Leap");
                tooltip.add(TextFormatting.GRAY + "Sneak in air to glide / cling to walls");
                tooltip.add(TextFormatting.GRAY + "Spider-Sense warns of nearby mobs");
                break;
            case FLASH:
                tooltip.add(TextFormatting.GOLD + "G Burst | F Speed Force | V Blink");
                tooltip.add(TextFormatting.GRAY + "Sprint on water; momentum melee bonus");
                break;
            case CAPTAIN_AMERICA:
                tooltip.add(TextFormatting.GOLD + "G Shield Throw | F Bash | V Rally");
                tooltip.add(TextFormatting.GRAY + "Craft Vibranium Shield; sneak-RMB to throw");
                break;
            default:
                break;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
