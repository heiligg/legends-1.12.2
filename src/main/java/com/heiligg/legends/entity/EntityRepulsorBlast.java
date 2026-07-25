package com.heiligg.legends.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityRepulsorBlast extends EntityThrowable implements IEntityAdditionalSpawnData {

    private float damage = 8.0F;
    private boolean unibeam;

    public EntityRepulsorBlast(World worldIn) {
        super(worldIn);
    }

    public EntityRepulsorBlast(World worldIn, EntityLivingBase throwerIn, float damage, boolean unibeam) {
        super(worldIn, throwerIn);
        this.damage = damage;
        this.unibeam = unibeam;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (!world.isRemote) {
            if (result.entityHit != null && result.entityHit != getThrower()) {
                result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), damage);
                if (unibeam) {
                    result.entityHit.setFire(4);
                }
                double dx = result.entityHit.posX - posX;
                double dz = result.entityHit.posZ - posZ;
                result.entityHit.addVelocity(dx * 0.35D, 0.25D, dz * 0.35D);
                result.entityHit.velocityChanged = true;
            }
            if (world instanceof WorldServer) {
                ((WorldServer) world).spawnParticle(
                        unibeam ? EnumParticleTypes.FLAME : EnumParticleTypes.CRIT_MAGIC,
                        posX, posY, posZ, unibeam ? 24 : 12, 0.15D, 0.15D, 0.15D, 0.02D
                );
            }
            world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE,
                    SoundCategory.PLAYERS, 0.6F, unibeam ? 0.7F : 1.4F);
            setDead();
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            world.spawnParticle(unibeam ? EnumParticleTypes.FLAME : EnumParticleTypes.SMOKE_NORMAL,
                    posX, posY, posZ, 0, 0, 0);
        }
        if (ticksExisted > (unibeam ? 40 : 30)) {
            setDead();
        }
    }

    @Override
    protected float getGravityVelocity() {
        return unibeam ? 0.0F : 0.01F;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeFloat(damage);
        buffer.writeBoolean(unibeam);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        damage = additionalData.readFloat();
        unibeam = additionalData.readBoolean();
    }
}
