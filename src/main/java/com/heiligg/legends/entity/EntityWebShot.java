package com.heiligg.legends.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityWebShot extends EntityThrowable {

    public EntityWebShot(World worldIn) {
        super(worldIn);
    }

    public EntityWebShot(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.entityHit instanceof EntityLivingBase && result.entityHit != getThrower()) {
                EntityLivingBase living = (EntityLivingBase) result.entityHit;
                living.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 4.0F);
                living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 * 5, 3));
                living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 20 * 4, 0));
                living.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 20 * 4, 1));
            }
            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, 10, 0.3D, 0.2D, 0.3D, 0.01D);
            }
            world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_SNOW_PLACE, SoundCategory.PLAYERS, 0.8F, 1.6F);
            setDead();
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            world.spawnParticle(EnumParticleTypes.CLOUD, posX, posY, posZ, 0, 0, 0);
        }
        if (ticksExisted > 35) {
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.03F;
    }
}
