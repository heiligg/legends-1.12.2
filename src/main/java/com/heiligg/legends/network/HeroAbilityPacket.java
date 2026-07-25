package com.heiligg.legends.network;

import com.heiligg.legends.entity.EntityRepulsorBlast;
import com.heiligg.legends.entity.EntityVibraniumShield;
import com.heiligg.legends.entity.EntityWebShot;
import com.heiligg.legends.hero.HeroAbilityHandler;
import com.heiligg.legends.hero.HeroType;
import com.heiligg.legends.hero.ItemHeroArmor;
import com.heiligg.legends.item.ItemVibraniumShield;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

/**
 * mode: 0 primary (G), 1 secondary (F), 2 special (V)
 */
public class HeroAbilityPacket implements IMessage {

    private int mode;

    public HeroAbilityPacket() {
    }

    public HeroAbilityPacket(int mode) {
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mode = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(mode);
    }

    public static class Handler implements IMessageHandler<HeroAbilityPacket, IMessage> {

        @Override
        public IMessage onMessage(HeroAbilityPacket message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            final int mode = message.mode;
            player.getServerWorld().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    HeroType set = ItemHeroArmor.getWornHeroSet(player);
                    if (set == null) {
                        return;
                    }
                    ItemStack chest = ItemHeroArmor.getChest(player);
                    if (!(chest.getItem() instanceof ItemHeroArmor)) {
                        return;
                    }
                    ItemHeroArmor armor = (ItemHeroArmor) chest.getItem();

                    switch (set) {
                        case IRON_MAN:
                            handleIronMan(player, armor, chest, mode);
                            break;
                        case SPIDER_MAN:
                            handleSpider(player, armor, chest, mode);
                            break;
                        case FLASH:
                            handleFlash(player, armor, chest, mode);
                            break;
                        case CAPTAIN_AMERICA:
                            handleCap(player, armor, chest, mode);
                            break;
                        default:
                            break;
                    }
                }
            });
            return null;
        }

        private void handleIronMan(EntityPlayerMP player, ItemHeroArmor armor, ItemStack chest, int mode) {
            if (mode == 1) { // Flight toggle
                if (armor.getEnergy(chest) < 5 && !HeroAbilityHandler.isIronFlight(player)) {
                    player.sendMessage(new TextComponentString("Jarvis: Insufficient power for flight."));
                    return;
                }
                if (!HeroAbilityHandler.readySecondary(player, 10)) {
                    return;
                }
                HeroAbilityHandler.toggleIronFlight(player);
                boolean on = HeroAbilityHandler.isIronFlight(player);
                player.sendStatusMessage(new TextComponentString(on ? "§aFlight systems online" : "§7Flight systems offline"), true);
                player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, 0.5F, on ? 1.4F : 0.8F);
                return;
            }
            if (mode == 0) { // Repulsor
                if (armor.getEnergy(chest) < 10 || !HeroAbilityHandler.readyPrimary(player, 12)) {
                    if (armor.getEnergy(chest) < 10) {
                        player.sendMessage(new TextComponentString("Jarvis: Repulsor capacitors low."));
                    }
                    return;
                }
                EntityRepulsorBlast blast = new EntityRepulsorBlast(player.world, player, 9.0F, false);
                blast.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.4F, 0.2F);
                player.world.spawnEntity(blast);
                armor.consumeEnergy(chest, 10);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.9F, 1.7F);
                return;
            }
            if (mode == 2) { // Unibeam
                if (armor.getEnergy(chest) < 35 || !HeroAbilityHandler.readySpecial(player, 45)) {
                    if (armor.getEnergy(chest) < 35) {
                        player.sendMessage(new TextComponentString("Jarvis: Unibeam requires more power."));
                    }
                    return;
                }
                for (int i = -1; i <= 1; i++) {
                    EntityRepulsorBlast beam = new EntityRepulsorBlast(player.world, player, 14.0F, true);
                    beam.shoot(player, player.rotationPitch, player.rotationYaw + i * 3.0F, 0.0F, 2.8F, 0.0F);
                    player.world.spawnEntity(beam);
                }
                armor.consumeEnergy(chest, 35);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.8F, 0.6F);
                if (player.world instanceof WorldServer) {
                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.FLAME,
                            player.posX, player.posY + player.getEyeHeight(), player.posZ, 20, 0.2D, 0.2D, 0.2D, 0.05D);
                }
            }
        }

        private void handleSpider(EntityPlayerMP player, ItemHeroArmor armor, ItemStack chest, int mode) {
            if (mode == 0) { // Web zip
                if (armor.getEnergy(chest) < 8 || !HeroAbilityHandler.readyPrimary(player, 15)) {
                    return;
                }
                Vec3d look = player.getLookVec();
                player.addVelocity(look.x * 1.85D, Math.max(0.4D, look.y * 1.25D + 0.35D), look.z * 1.85D);
                player.velocityChanged = true;
                player.fallDistance = 0.0F;
                armor.consumeEnergy(chest, 8);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 0.9F, 1.9F);
                return;
            }
            if (mode == 1) { // Web shot
                if (armor.getEnergy(chest) < 12 || !HeroAbilityHandler.readySecondary(player, 18)) {
                    return;
                }
                EntityWebShot web = new EntityWebShot(player.world, player);
                web.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.9F, 0.5F);
                player.world.spawnEntity(web);
                armor.consumeEnergy(chest, 12);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.8F, 0.6F);
                return;
            }
            if (mode == 2) { // Spider leap
                if (armor.getEnergy(chest) < 14 || !HeroAbilityHandler.readySpecial(player, 25)) {
                    return;
                }
                Vec3d look = player.getLookVec();
                player.addVelocity(look.x * 0.8D, 1.15D, look.z * 0.8D);
                player.velocityChanged = true;
                player.fallDistance = 0.0F;
                armor.consumeEnergy(chest, 14);
                player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, 3, true, false));
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_HORSE_JUMP, SoundCategory.PLAYERS, 0.7F, 1.5F);
            }
        }

        private void handleFlash(EntityPlayerMP player, ItemHeroArmor armor, ItemStack chest, int mode) {
            if (mode == 0) { // Speed burst dash
                if (armor.getEnergy(chest) < 12 || !HeroAbilityHandler.readyPrimary(player, 16)) {
                    return;
                }
                Vec3d dir = player.getLookVec();
                player.addVelocity(dir.x * 3.2D, 0.08D, dir.z * 3.2D);
                player.velocityChanged = true;
                armor.consumeEnergy(chest, 12);
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 3, true, false));
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 0.8F, 1.9F);
                return;
            }
            if (mode == 1) { // Speed Force toggle
                if (!HeroAbilityHandler.readySecondary(player, 20)) {
                    return;
                }
                if (!HeroAbilityHandler.isSpeedForce(player) && armor.getEnergy(chest) < 20) {
                    player.sendMessage(new TextComponentString("Not enough energy to enter the Speed Force."));
                    return;
                }
                HeroAbilityHandler.toggleSpeedForce(player);
                boolean on = HeroAbilityHandler.isSpeedForce(player);
                player.sendStatusMessage(new TextComponentString(on ? "§eSpeed Force engaged" : "§7Speed Force disengaged"), true);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.PLAYERS, 0.35F, 1.8F);
                return;
            }
            if (mode == 2) { // Blink / phase dash
                if (armor.getEnergy(chest) < 20 || !HeroAbilityHandler.readySpecial(player, 35)) {
                    return;
                }
                Vec3d look = player.getLookVec();
                double dist = 8.0D;
                double tx = player.posX + look.x * dist;
                double ty = player.posY + look.y * dist;
                double tz = player.posZ + look.z * dist;
                // simple blink: try destination, clamp to air
                if (!player.world.getBlockState(new net.minecraft.util.math.BlockPos(tx, ty, tz)).getMaterial().blocksMovement()
                        && !player.world.getBlockState(new net.minecraft.util.math.BlockPos(tx, ty + 1, tz)).getMaterial().blocksMovement()) {
                    player.setPositionAndUpdate(tx, ty, tz);
                    player.fallDistance = 0.0F;
                    armor.consumeEnergy(chest, 20);
                    player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 15, 0, true, false));
                    player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.7F, 1.4F);
                } else {
                    player.sendStatusMessage(new TextComponentString("§cBlink path blocked"), true);
                }
            }
        }

        private void handleCap(EntityPlayerMP player, ItemHeroArmor armor, ItemStack chest, int mode) {
            if (mode == 0) { // Shield throw from inventory/hand
                if (armor.getEnergy(chest) < 10 || !HeroAbilityHandler.readyPrimary(player, 20)) {
                    return;
                }
                ItemStack shield = findShield(player);
                if (shield.isEmpty()) {
                    player.sendMessage(new TextComponentString("Equip the Vibranium Shield to throw it."));
                    return;
                }
                EntityVibraniumShield proj = new EntityVibraniumShield(player.world, player);
                proj.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.85F, 0.4F);
                player.world.spawnEntity(proj);
                armor.consumeEnergy(chest, 10);
                shield.damageItem(1, player);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.9F, 0.8F);
                return;
            }
            if (mode == 1) { // Shield bash AOE
                if (armor.getEnergy(chest) < 15 || !HeroAbilityHandler.readySecondary(player, 30)) {
                    return;
                }
                AxisAlignedBB box = player.getEntityBoundingBox().grow(3.5D, 1.0D, 3.5D);
                List<EntityLivingBase> targets = player.world.getEntitiesWithinAABB(EntityLivingBase.class, box);
                for (EntityLivingBase t : targets) {
                    if (t == player) continue;
                    t.attackEntityFrom(DamageSource.causePlayerDamage(player), 7.0F);
                    double dx = t.posX - player.posX;
                    double dz = t.posZ - player.posZ;
                    double d = Math.sqrt(dx * dx + dz * dz);
                    if (d > 0.001D) {
                        t.addVelocity(dx / d * 1.4D, 0.35D, dz / d * 1.4D);
                        t.velocityChanged = true;
                    }
                }
                armor.consumeEnergy(chest, 15);
                player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 0.7F);
                return;
            }
            if (mode == 2) { // Rally
                if (armor.getEnergy(chest) < 25 || !HeroAbilityHandler.readySpecial(player, 80)) {
                    return;
                }
                player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 20 * 20, 2));
                player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 15, 1));
                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 10, 1));
                AxisAlignedBB box = player.getEntityBoundingBox().grow(8.0D);
                for (EntityPlayer ally : player.world.getEntitiesWithinAABB(EntityPlayer.class, box)) {
                    if (ally != player) {
                        ally.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 20 * 12, 1));
                        ally.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20 * 12, 0));
                    }
                }
                armor.consumeEnergy(chest, 25);
                player.sendStatusMessage(new TextComponentString("§bAssemble! Rally active"), true);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
        }

        private ItemStack findShield(EntityPlayerMP player) {
            if (player.getHeldItemMainhand().getItem() instanceof ItemVibraniumShield) {
                return player.getHeldItemMainhand();
            }
            if (player.getHeldItemOffhand().getItem() instanceof ItemVibraniumShield) {
                return player.getHeldItemOffhand();
            }
            return ItemStack.EMPTY;
        }
    }
}
