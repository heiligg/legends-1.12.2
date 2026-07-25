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

public class EntityArcaneBolt extends EntityThrowable {

    public EntityArcaneBolt(World worldIn) {
        super(worldIn);
    }

    public EntityArcaneBolt(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.entityHit != null && result.entityHit != getThrower()) {
                result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 8.0F);
            }

            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(
                        EnumParticleTypes.END_ROD,
                        posX,
                        posY,
                        posZ,
                        18,
                        0.2D,
                        0.2D,
                        0.2D,
                        0.02D
                );
            }

            world.playSound(
                    null,
                    posX,
                    posY,
                    posZ,
                    SoundEvents.ENTITY_ENDEREYE_DEATH,
                    SoundCategory.PLAYERS,
                    0.8F,
                    1.3F
            );
            setDead();
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected float getGravityVelocity() {
        return 0.02F;
    }
}
