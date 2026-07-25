package com.heiligg.legends.handler;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.item.ItemLegendTotem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TotemHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().world.isRemote) {
            return;
        }
        if (event.getSource().canHarmInCreative()) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        int slot = findTotemSlot(player);
        if (slot < 0) {
            return;
        }

        ItemStack totem = slot == 40 ? player.getHeldItemOffhand() : player.inventory.getStackInSlot(slot);
        if (totem.isEmpty() || !(totem.getItem() instanceof ItemLegendTotem)) {
            return;
        }

        event.setCanceled(true);
        totem.shrink(1);
        if (slot == 40) {
            player.setHeldItem(net.minecraft.util.EnumHand.OFF_HAND, totem);
        }

        player.setHealth(8.0F);
        player.clearActivePotions();
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 20, 1));
        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 20 * 20, 0));
        player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 20 * 15, 1));

        player.world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

        if (player.world instanceof WorldServer) {
            ((WorldServer) player.world).spawnParticle(
                    EnumParticleTypes.TOTEM,
                    player.posX,
                    player.posY + 1.0D,
                    player.posZ,
                    30,
                    0.4D,
                    0.5D,
                    0.4D,
                    0.1D
            );
        }
    }

    private int findTotemSlot(EntityPlayer player) {
        ItemStack off = player.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() instanceof ItemLegendTotem) {
            return 40;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemLegendTotem) {
                return i;
            }
        }
        return -1;
    }
}
