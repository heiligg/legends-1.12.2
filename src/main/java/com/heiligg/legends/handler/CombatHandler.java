package com.heiligg.legends.handler;

import com.heiligg.legends.item.ItemAscendedArmor;
import com.heiligg.legends.item.ItemLegendaryArmor;
import com.heiligg.legends.item.ItemLegendaryShield;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Extra combat rules: shield block bonus and ascended damage resist.
 */
public class CombatHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        DamageSource source = event.getSource();

        if (player.isActiveItemStackBlocking()) {
            ItemStack active = player.getActiveItemStack();
            if (active.getItem() instanceof ItemLegendaryShield && !source.isUnblockable()) {
                event.setAmount(event.getAmount() * 0.65F);
            }
        }

        if (ArmorAbilityHandler.isWearingFullAscended(player) && !source.canHarmInCreative()) {
            event.setAmount(event.getAmount() * 0.85F);
        } else if (isWearingAnyLegendaryPiece(player) && source.isExplosion()) {
            event.setAmount(event.getAmount() * 0.9F);
        }
    }

    private boolean isWearingAnyLegendaryPiece(EntityPlayer player) {
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemLegendaryArmor || stack.getItem() instanceof ItemAscendedArmor) {
                return true;
            }
        }
        return false;
    }
}
