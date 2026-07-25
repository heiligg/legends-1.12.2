package com.heiligg.legends.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Held or offhand trinket that grants Night Vision and a mild Speed boost.
 */
public class ItemLegendAmulet extends Item {

    public ItemLegendAmulet() {
        setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isRemote || !(entityIn instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entityIn;
        boolean equipped = isSelected
                || ItemStack.areItemsEqualIgnoreDurability(player.getHeldItemOffhand(), stack);

        if (!equipped) {
            // Also count anywhere in hotbar for a friendlier trinket feel
            for (int i = 0; i < 9; i++) {
                if (player.inventory.getStackInSlot(i) == stack) {
                    equipped = true;
                    break;
                }
            }
        }

        if (!equipped) {
            return;
        }

        if (player.ticksExisted % 40 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 260, 0, true, false));
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60, 0, true, false));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.AQUA + "Keep in hotbar or offhand:");
        tooltip.add(TextFormatting.GRAY + "Night Vision and Speed.");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
