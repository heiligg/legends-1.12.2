package com.heiligg.legends.handler;

import com.heiligg.legends.config.LegendsConfig;
import com.heiligg.legends.item.ItemAscendedArmor;
import com.heiligg.legends.item.ItemLegendaryArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ArmorAbilityHandler {

    public static boolean isWearingFullLegendary(EntityPlayer player) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) {
                continue;
            }
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemLegendaryArmor)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWearingFullAscended(EntityPlayer player) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) {
                continue;
            }
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemAscendedArmor)) {
                return false;
            }
        }
        return true;
    }

    /** Either full legendary or full ascended grants dash/fall perks. */
    public static boolean isWearingFullSet(EntityPlayer player) {
        return isWearingFullLegendary(player) || isWearingFullAscended(player);
    }

    public static int getChestPower(ItemStack chest) {
        if (chest.getItem() instanceof ItemLegendaryArmor) {
            return ((ItemLegendaryArmor) chest.getItem()).getPower(chest);
        }
        if (chest.getItem() instanceof ItemAscendedArmor) {
            return ((ItemAscendedArmor) chest.getItem()).getPower(chest);
        }
        return 0;
    }

    public static void consumeChestPower(ItemStack chest, int amount) {
        if (chest.getItem() instanceof ItemLegendaryArmor) {
            ((ItemLegendaryArmor) chest.getItem()).consumePower(chest, amount);
        } else if (chest.getItem() instanceof ItemAscendedArmor) {
            ((ItemAscendedArmor) chest.getItem()).consumePower(chest, amount);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        EntityPlayer player = event.player;

        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemLegendaryArmor) {
                ((ItemLegendaryArmor) stack.getItem()).recharge(stack);
            } else if (stack.getItem() instanceof ItemAscendedArmor) {
                ((ItemAscendedArmor) stack.getItem()).recharge(stack);
            }
        }

        if (player.ticksExisted % 20 != 0) {
            return;
        }

        if (isWearingFullAscended(player)) {
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 1, true, false));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 1, true, false));
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 60, 1, true, false));
        } else if (isWearingFullLegendary(player)) {
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 0, true, false));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 0, true, false));
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!LegendsConfig.fullSetCancelsFallDamage) {
            return;
        }
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        if (event.getSource() != DamageSource.FALL) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (isWearingFullSet(player)) {
            event.setCanceled(true);
        }
    }
}
