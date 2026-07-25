package com.heiligg.legends.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLegendaryBow extends ItemBow {

    public ItemLegendaryBow() {
        setMaxDamage(1200);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (!(entityLiving instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entityLiving;
        boolean infinity = player.capabilities.isCreativeMode
                || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
        ItemStack ammo = findAmmo(player);

        int charge = getMaxItemUseDuration(stack) - timeLeft;
        charge = ForgeEventFactory.onArrowLoose(stack, worldIn, player, charge, !ammo.isEmpty() || infinity);
        if (charge < 0) {
            return;
        }

        if (ammo.isEmpty() && !infinity) {
            return;
        }

        if (ammo.isEmpty()) {
            ammo = new ItemStack(Items.ARROW);
        }

        float velocity = getArrowVelocity(charge);
        if ((double) velocity < 0.1D) {
            return;
        }

        boolean creativeAmmo = player.capabilities.isCreativeMode
                || (ammo.getItem() instanceof ItemArrow && ((ItemArrow) ammo.getItem()).isInfinite(ammo, stack, player));

        if (!worldIn.isRemote) {
            // Fire a central arrow plus two angled side shots
            float[] yaws = new float[]{0.0F, -8.0F, 8.0F};
            for (float yawOffset : yaws) {
                ItemArrow itemArrow = (ItemArrow) (ammo.getItem() instanceof ItemArrow ? ammo.getItem() : Items.ARROW);
                EntityArrow entityArrow = itemArrow.createArrow(worldIn, ammo, player);
                entityArrow.shoot(player, player.rotationPitch, player.rotationYaw + yawOffset, 0.0F, velocity * 3.15F, 0.5F);

                if (velocity == 1.0F) {
                    entityArrow.setIsCritical(true);
                }

                int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                if (power > 0) {
                    entityArrow.setDamage(entityArrow.getDamage() + (double) power * 0.5D + 0.5D);
                }
                // Legendary baseline damage bonus
                entityArrow.setDamage(entityArrow.getDamage() + 1.5D);

                int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                if (punch > 0) {
                    entityArrow.setKnockbackStrength(punch);
                }

                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                    entityArrow.setFire(100);
                }

                if (yawOffset != 0.0F || creativeAmmo || (infinity && ammo.getItem() == Items.ARROW)) {
                    entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                }

                worldIn.spawnEntity(entityArrow);
            }

            stack.damageItem(1, player);
        }

        worldIn.playSound(
                null,
                player.posX,
                player.posY,
                player.posZ,
                SoundEvents.ENTITY_ARROW_SHOOT,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + velocity * 0.5F
        );

        if (!creativeAmmo && !player.capabilities.isCreativeMode) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.inventory.deleteStack(ammo);
            }
        }

        player.addStat(StatList.getObjectUseStats(this));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.GOLD + "Fires a legendary triple volley.");
        tooltip.add(TextFormatting.GRAY + "Bonus damage on every shot.");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
