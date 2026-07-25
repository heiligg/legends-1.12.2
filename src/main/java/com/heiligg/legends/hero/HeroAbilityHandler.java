package com.heiligg.legends.hero;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeroAbilityHandler {

    private static final Map<UUID, Boolean> IRON_FLIGHT = new HashMap<UUID, Boolean>();

    public static void setIronFlight(EntityPlayer player, boolean flying) {
        IRON_FLIGHT.put(player.getUniqueID(), flying);
    }

    public static boolean isIronFlight(EntityPlayer player) {
        return IRON_FLIGHT.getOrDefault(player.getUniqueID(), false);
    }

    public static void toggleIronFlight(EntityPlayer player) {
        setIronFlight(player, !isIronFlight(player));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        EntityPlayer player = event.player;
        HeroType set = ItemHeroArmor.getWornHeroSet(player);

        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemHeroArmor) {
                ((ItemHeroArmor) stack.getItem()).recharge(stack);
            }
        }

        if (set == null) {
            if (IRON_FLIGHT.getOrDefault(player.getUniqueID(), false)) {
                IRON_FLIGHT.put(player.getUniqueID(), false);
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                }
            }
            return;
        }

        ItemStack chest = ItemHeroArmor.getChest(player);
        ItemHeroArmor armor = (ItemHeroArmor) chest.getItem();

        if (player.ticksExisted % 20 == 0) {
            applySetBonus(player, set);
        }

        if (set == HeroType.IRON_MAN) {
            boolean flying = isIronFlight(player);
            if (flying && armor.getEnergy(chest) > 0) {
                player.capabilities.allowFlying = true;
                player.capabilities.isFlying = true;
                if (!player.world.isRemote && player.ticksExisted % 10 == 0) {
                    armor.consumeEnergy(chest, 1);
                    if (armor.getEnergy(chest) <= 0) {
                        setIronFlight(player, false);
                    }
                }
            } else if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
                if (!flying) {
                    player.capabilities.isFlying = false;
                }
            }
        } else if (!player.capabilities.isCreativeMode) {
            // Other suits shouldn't keep iron-man flight
            if (IRON_FLIGHT.containsKey(player.getUniqueID())) {
                IRON_FLIGHT.put(player.getUniqueID(), false);
            }
        }

        if (set == HeroType.SPIDER_MAN && !player.onGround && player.isSneaking()) {
            if (player.motionY < -0.1D) {
                player.motionY *= 0.7D;
                player.fallDistance = 0.0F;
            }
        }
    }

    private void applySetBonus(EntityPlayer player, HeroType set) {
        switch (set) {
            case IRON_MAN:
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 0, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
                break;
            case SPIDER_MAN:
                player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, 1, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 0, true, false));
                break;
            case FLASH:
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 2, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 40, 1, true, false));
                break;
            default:
                break;
        }
    }
}
