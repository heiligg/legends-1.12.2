package com.heiligg.legends.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * Cap's thrown shield: hits, then returns toward thrower.
 */
public class EntityVibraniumShield extends EntityThrowable {

    private boolean returning;
    private int hitCount;

    public EntityVibraniumShield(World worldIn) {
        super(worldIn);
    }

    public EntityVibraniumShield(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (world.isRemote) {
            return;
        }
        if (result.entityHit != null && result.entityHit != getThrower()) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 9.0F);
            hitCount++;
            world.playSound(null, posX, posY, posZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.9F, 1.2F);
            if (hitCount >= 3) {
                returning = true;
            }
            // ricochet nudge
            motionX = -motionX * 0.6D;
            motionY = 0.15D;
            motionZ = -motionZ * 0.6D;
            returning = true;
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            returning = true;
            world.playSound(null, posX, posY, posZ, SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.7F, 0.9F);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            world.spawnParticle(EnumParticleTypes.CRIT, posX, posY, posZ, 0, 0, 0);
        }

        EntityLivingBase thrower = getThrower();
        if (!world.isRemote && (returning || ticksExisted > 20) && thrower != null) {
            double dx = thrower.posX - posX;
            double dy = thrower.posY + thrower.getEyeHeight() * 0.5D - posY;
            double dz = thrower.posZ - posZ;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 1.4D) {
                setDead();
                return;
            }
            motionX = dx / dist * 1.4D;
            motionY = dy / dist * 1.4D;
            motionZ = dz / dist * 1.4D;
        }

        if (ticksExisted > 80) {
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return returning ? 0.0F : 0.02F;
    }
}
