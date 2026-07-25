package com.heiligg.legends.entity;

import com.heiligg.legends.LegendsMod;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;

public class EntityLegendGuardian extends EntityMob {

    private final BossInfoServer bossInfo = new BossInfoServer(
            new TextComponentString("Legend Guardian"),
            BossInfo.Color.BLUE,
            BossInfo.Overlay.PROGRESS
    );

    private int boltCooldown;

    public EntityLegendGuardian(World worldIn) {
        super(worldIn);
        setSize(0.9F, 2.4F);
        experienceValue = 80;
        isImmuneToFire = true;
        bossInfo.setName(getDisplayName());
    }

    @Override
    protected void initEntityAI() {
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(2, new EntityAIAttackMelee(this, 1.2D, true));
        tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.85D));
        tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
        tasks.addTask(7, new EntityAILookIdle(this));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(180.0D);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.7D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        bossInfo.setPercent(getHealth() / getMaxHealth());

        if (!world.isRemote && boltCooldown > 0) {
            boltCooldown--;
        }

        EntityLivingBase target = getAttackTarget();
        if (!world.isRemote && target != null && boltCooldown <= 0 && getDistance(target) > 4.0F && getDistance(target) < 18.0F) {
            EntityArcaneBolt bolt = new EntityArcaneBolt(world, this);
            double dx = target.posX - posX;
            double dy = target.posY + target.getEyeHeight() * 0.5D - (posY + getEyeHeight());
            double dz = target.posZ - posZ;
            bolt.shoot(dx, dy, dz, 1.4F, 0.5F);
            world.spawnEntity(bolt);
            playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.8F);
            boltCooldown = 45;
        }

        if (world.isRemote && ticksExisted % 4 == 0) {
            world.spawnParticle(
                    net.minecraft.util.EnumParticleTypes.CRIT_MAGIC,
                    posX + (rand.nextDouble() - 0.5D) * width,
                    posY + rand.nextDouble() * height,
                    posZ + (rand.nextDouble() - 0.5D) * width,
                    0.0D,
                    0.05D,
                    0.0D
            );
        }
    }

    @Override
    public boolean attackEntityAsMob(net.minecraft.entity.Entity entityIn) {
        boolean hit = super.attackEntityAsMob(entityIn);
        if (hit && entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 0));
        }
        return hit;
    }

    @Override
    public void addTrackingPlayer(EntityPlayerMP player) {
        super.addTrackingPlayer(player);
        bossInfo.addPlayer(player);
    }

    @Override
    public void removeTrackingPlayer(EntityPlayerMP player) {
        super.removeTrackingPlayer(player);
        bossInfo.removePlayer(player);
    }

    @Override
    public void setCustomNameTag(String name) {
        super.setCustomNameTag(name);
        bossInfo.setName(getDisplayName());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH;
    }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        if (!wasRecentlyHit) {
            return;
        }
        if (LegendsMod.ascendedCore != null) {
            dropItem(LegendsMod.ascendedCore, 1 + (rand.nextFloat() < lootingModifier * 0.15F ? 1 : 0));
        }
        if (LegendsMod.legendEssence != null) {
            dropItem(LegendsMod.legendEssence, 2 + rand.nextInt(3 + lootingModifier));
        }
    }

    @Override
    public boolean isNonBoss() {
        return false;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }
}
