package com.heiligg.legends.entity;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.config.LegendsConfig;
import com.heiligg.legends.init.ModSounds;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityLegendGuardian extends EntityMob {

    private final BossInfoServer bossInfo = new BossInfoServer(
            new TextComponentString("Legend Guardian"),
            BossInfo.Color.BLUE,
            BossInfo.Overlay.PROGRESS
    );

    private int boltCooldown;
    private int phaseAnnounce = 1;

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
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LegendsConfig.guardianHealth);
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(LegendsConfig.guardianDamage);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.7D);
    }

    private int getPhase() {
        float pct = getHealth() / getMaxHealth();
        if (pct > 0.66F) {
            return 1;
        }
        if (pct > 0.33F) {
            return 2;
        }
        return 3;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        bossInfo.setPercent(getHealth() / getMaxHealth());

        int phase = getPhase();
        if (!world.isRemote && phase != phaseAnnounce) {
            phaseAnnounce = phase;
            if (phase == 2) {
                world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(24.0D))
                        .forEach(p -> p.sendMessage(new TextComponentString("§bGuardian Phase II — Arcane Barrage")));
                boltCooldown = 10;
            } else if (phase == 3) {
                world.getEntitiesWithinAABB(EntityPlayer.class, getEntityBoundingBox().grow(24.0D))
                        .forEach(p -> p.sendMessage(new TextComponentString("§cGuardian Phase III — Wellshock")));
                addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 60, 0));
                addPotionEffect(new PotionEffect(MobEffects.SPEED, 20 * 60, 0));
            }
        }

        if (!world.isRemote && boltCooldown > 0) {
            boltCooldown--;
        }

        EntityLivingBase target = getAttackTarget();
        if (!world.isRemote && target != null && boltCooldown <= 0) {
            float dist = getDistance(target);
            int cd = LegendsConfig.guardianBoltCooldown;
            if (phase == 2) {
                cd = Math.max(15, cd - 15);
            } else if (phase == 3) {
                cd = Math.max(12, cd - 20);
            }

            if (phase >= 2 && dist < 6.0F && ticksExisted % 80 < 5) {
                // Wellshock knockback pulse
                AxisAlignedBB box = getEntityBoundingBox().grow(5.0D);
                for (EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
                    if (e == this) continue;
                    e.attackEntityFrom(DamageSource.causeMobDamage(this), 6.0F + phase);
                    double dx = e.posX - posX;
                    double dz = e.posZ - posZ;
                    double d = Math.sqrt(dx * dx + dz * dz);
                    if (d > 0.001D) {
                        e.addVelocity(dx / d * 1.3D, 0.45D, dz / d * 1.3D);
                        e.velocityChanged = true;
                    }
                }
                playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 0.7F, 1.2F);
                if (world instanceof WorldServer) {
                    ((WorldServer) world).spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                            posX, posY + 1.0D, posZ, 30, 1.5D, 0.5D, 1.5D, 0.05D);
                }
                boltCooldown = cd;
            } else if (dist > 3.5F && dist < 20.0F) {
                int bolts = phase >= 3 ? 3 : (phase == 2 ? 2 : 1);
                for (int i = 0; i < bolts; i++) {
                    EntityArcaneBolt bolt = new EntityArcaneBolt(world, this);
                    double dx = target.posX - posX + (i - bolts / 2) * 0.8D;
                    double dy = target.posY + target.getEyeHeight() * 0.5D - (posY + getEyeHeight());
                    double dz = target.posZ - posZ + (i - bolts / 2) * 0.4D;
                    bolt.shoot(dx, dy, dz, 1.35F + phase * 0.1F, 0.4F + i * 0.3F);
                    world.spawnEntity(bolt);
                }
                playSound(SoundEvents.ENTITY_BLAZE_SHOOT, 1.0F, 0.8F);
                boltCooldown = cd;
            }
        }

        if (world.isRemote && ticksExisted % 4 == 0) {
            world.spawnParticle(
                    EnumParticleTypes.CRIT_MAGIC,
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
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, getPhase() >= 3 ? 1 : 0));
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
        return ModSounds.WRAITH_AMBIENT != null ? ModSounds.WRAITH_AMBIENT : SoundEvents.ENTITY_ELDER_GUARDIAN_AMBIENT;
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
            dropItem(LegendsMod.legendEssence, 3 + rand.nextInt(3 + lootingModifier));
        }
        if (LegendsMod.legendCodex != null && rand.nextFloat() < 0.5F) {
            dropItem(LegendsMod.legendCodex, 1);
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
