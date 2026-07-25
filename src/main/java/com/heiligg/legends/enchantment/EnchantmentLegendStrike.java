package com.heiligg.legends.enchantment;

import com.heiligg.legends.LegendsMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class EnchantmentLegendStrike extends Enchantment {

    public EnchantmentLegendStrike() {
        super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        setName(LegendsMod.MODID + ".legend_strike");
        setRegistryName(LegendsMod.MODID, "legend_strike");
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 15 + (enchantmentLevel - 1) * 12;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
        if (target instanceof EntityLivingBase && user.world.rand.nextFloat() < 0.15F * level) {
            target.attackEntityFrom(DamageSource.MAGIC, 2.0F * level);
            if (user.world instanceof net.minecraft.world.WorldServer) {
                ((net.minecraft.world.WorldServer) user.world).spawnParticle(
                        net.minecraft.util.EnumParticleTypes.CRIT_MAGIC,
                        target.posX,
                        target.posY + target.height * 0.5D,
                        target.posZ,
                        8,
                        0.2D,
                        0.2D,
                        0.2D,
                        0.05D
                );
            }
        }
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return super.canApply(stack);
    }
}
