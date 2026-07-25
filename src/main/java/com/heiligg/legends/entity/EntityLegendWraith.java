package com.heiligg.legends.entity;

import com.heiligg.legends.LegendsMod;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityLegendWraith extends EntityMob {

    public EntityLegendWraith(World worldIn) {
        super(worldIn);
        setSize(0.6F, 1.8F);
        experienceValue = 12;
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackMelee(this, 1.15D, false));
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.9D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(28.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(28.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (world.isRemote && ticksExisted % 8 == 0) {
            world.spawnParticle(
                    net.minecraft.util.EnumParticleTypes.PORTAL,
                    posX + (rand.nextDouble() - 0.5D) * width,
                    posY + rand.nextDouble() * height,
                    posZ + (rand.nextDouble() - 0.5D) * width,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (wasRecentlyHit && LegendsMod.legendFragment != null) {
            int count = 1 + rand.nextInt(2 + lootingModifier);
            dropItem(LegendsMod.legendFragment, count);
        }
        if (wasRecentlyHit && rand.nextFloat() < 0.15F + lootingModifier * 0.05F && LegendsMod.legendEssence != null) {
            dropItem(LegendsMod.legendEssence, 1);
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        return world.getLightFromNeighbors(getPosition()) <= 7 && super.getCanSpawnHere();
    }
}
