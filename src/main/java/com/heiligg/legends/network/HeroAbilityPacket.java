package com.heiligg.legends.network;

import com.heiligg.legends.entity.EntityRepulsorBlast;
import com.heiligg.legends.entity.EntityVibraniumShield;
import com.heiligg.legends.entity.EntityWebShot;
import com.heiligg.legends.hero.HeroAbilityHandler;
import com.heiligg.legends.hero.HeroType;
import com.heiligg.legends.hero.IronSuitType;
import com.heiligg.legends.hero.ItemHeroArmor;
import com.heiligg.legends.hero.ItemIronSuitArmor;
import com.heiligg.legends.item.ItemVibraniumShield;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
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
                    IronSuitType iron = ItemIronSuitArmor.getWornSuit(player);
                    if (iron != null) {
                        ItemStack chest = ItemIronSuitArmor.getChest(player);
                        if (chest.getItem() instanceof ItemIronSuitArmor) {
                            handleIronSuit(player, iron, (ItemIronSuitArmor) chest.getItem(), chest, mode);
                        }
                        return;
                    }

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

        private void handleIronSuit(EntityPlayerMP player, IronSuitType suit, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            switch (suit) {
                case MARK_I:
                    handleMarkI(player, armor, chest, mode);
                    break;
                case MARK_III:
                    handleClassicFlightSuit(player, armor, chest, mode, 10, 9.0F, 35, 14.0F, true);
                    break;
                case MARK_V:
                    handleMarkV(player, armor, chest, mode);
                    break;
                case MARK_VII:
                    handleMarkVII(player, armor, chest, mode);
                    break;
                case MARK_XLII:
                    handleMarkXLII(player, armor, chest, mode);
                    break;
                case WAR_MACHINE:
                    handleWarMachine(player, armor, chest, mode);
                    break;
                case HULKBUSTER:
                    handleHulkbuster(player, armor, chest, mode);
                    break;
                case MARK_L:
                    handleMarkL(player, armor, chest, mode);
                    break;
                case STEALTH:
                    handleStealth(player, armor, chest, mode);
                    break;
                default:
                    break;
            }
        }

        private void toggleFlight(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, IronSuitType suit) {
            if (!suit.canFly) {
                player.sendMessage(new TextComponentString("Jarvis: This suit has no sustained flight systems."));
                return;
            }
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
            float pitch = suit == IronSuitType.STEALTH ? 0.5F : (on ? 1.4F : 0.8F);
            float volume = suit == IronSuitType.STEALTH ? 0.15F : 0.5F;
            player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, volume, pitch);
        }

        private void fireRepulsor(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest,
                                  int cost, float damage, int cooldown, float speed, boolean unibeamStyle) {
            if (armor.getEnergy(chest) < cost || !HeroAbilityHandler.readyPrimary(player, cooldown)) {
                if (armor.getEnergy(chest) < cost) {
                    player.sendMessage(new TextComponentString("Jarvis: Weapon capacitors low."));
                }
                return;
            }
            EntityRepulsorBlast blast = new EntityRepulsorBlast(player.world, player, damage, unibeamStyle);
            blast.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, speed, 0.2F);
            player.world.spawnEntity(blast);
            armor.consumeEnergy(chest, cost);
            player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.9F, 1.7F);
        }

        private void handleClassicFlightSuit(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode,
                                             int primaryCost, float primaryDmg, int specialCost, float specialDmg, boolean tripleBeam) {
            if (mode == 1) {
                toggleFlight(player, armor, chest, IronSuitType.MARK_III);
                return;
            }
            if (mode == 0) {
                fireRepulsor(player, armor, chest, primaryCost, primaryDmg, 12, 2.4F, false);
                return;
            }
            if (mode == 2) {
                if (armor.getEnergy(chest) < specialCost || !HeroAbilityHandler.readySpecial(player, 45)) {
                    if (armor.getEnergy(chest) < specialCost) {
                        player.sendMessage(new TextComponentString("Jarvis: Unibeam requires more power."));
                    }
                    return;
                }
                int spread = tripleBeam ? 1 : 0;
                for (int i = -spread; i <= spread; i++) {
                    EntityRepulsorBlast beam = new EntityRepulsorBlast(player.world, player, specialDmg, true);
                    beam.shoot(player, player.rotationPitch, player.rotationYaw + i * 3.0F, 0.0F, 2.8F, 0.0F);
                    player.world.spawnEntity(beam);
                }
                armor.consumeEnergy(chest, specialCost);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.8F, 0.6F);
                if (player.world instanceof WorldServer) {
                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.FLAME,
                            player.posX, player.posY + player.getEyeHeight(), player.posZ, 20, 0.2D, 0.2D, 0.2D, 0.05D);
                }
            }
        }

        private void handleMarkI(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            if (mode == 0) { // Flamethrower cone
                if (armor.getEnergy(chest) < 12 || !HeroAbilityHandler.readyPrimary(player, 16)) {
                    return;
                }
                Vec3d look = player.getLookVec();
                AxisAlignedBB box = player.getEntityBoundingBox().grow(4.0D).offset(look.x * 3, look.y * 2, look.z * 3);
                for (EntityLivingBase t : player.world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
                    if (t == player) continue;
                    t.setFire(4);
                    t.attackEntityFrom(DamageSource.causePlayerDamage(player), 5.0F);
                }
                armor.consumeEnergy(chest, 12);
                player.world.playSound(null, player.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0F, 0.8F);
                if (player.world instanceof WorldServer) {
                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.FLAME,
                            player.posX + look.x, player.posY + player.getEyeHeight(), player.posZ + look.z,
                            40, 0.6D, 0.3D, 0.6D, 0.08D);
                }
                return;
            }
            if (mode == 1) { // Rocket jump
                if (armor.getEnergy(chest) < 15 || !HeroAbilityHandler.readySecondary(player, 30)) {
                    return;
                }
                Vec3d look = player.getLookVec();
                player.addVelocity(look.x * 0.6D, 1.35D, look.z * 0.6D);
                player.velocityChanged = true;
                player.fallDistance = 0.0F;
                armor.consumeEnergy(chest, 15);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5F, 1.6F);
                return;
            }
            if (mode == 2) { // Smoke screen
                if (armor.getEnergy(chest) < 18 || !HeroAbilityHandler.readySpecial(player, 50)) {
                    return;
                }
                player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 80, 0));
                AxisAlignedBB box = player.getEntityBoundingBox().grow(5.0D);
                for (EntityLivingBase t : player.world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
                    if (t != player) {
                        t.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 0));
                    }
                }
                armor.consumeEnergy(chest, 18);
                player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, 0.5F);
                if (player.world instanceof WorldServer) {
                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                            player.posX, player.posY + 1.0D, player.posZ, 60, 1.5D, 0.8D, 1.5D, 0.02D);
                }
            }
        }

        private void handleMarkV(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            if (mode == 1) {
                toggleFlight(player, armor, chest, IronSuitType.MARK_V);
                return;
            }
            if (mode == 0) {
                fireRepulsor(player, armor, chest, 6, 6.5F, 6, 2.8F, false);
                return;
            }
            if (mode == 2) { // Pulse wave
                if (armor.getEnergy(chest) < 22 || !HeroAbilityHandler.readySpecial(player, 35)) {
                    return;
                }
                AxisAlignedBB box = player.getEntityBoundingBox().grow(5.0D);
                for (EntityLivingBase t : player.world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
                    if (t == player) continue;
                    t.attackEntityFrom(DamageSource.causePlayerDamage(player), 8.0F);
                    double dx = t.posX - player.posX;
                    double dz = t.posZ - player.posZ;
                    double d = Math.sqrt(dx * dx + dz * dz);
                    if (d > 0.001D) {
                        t.addVelocity(dx / d * 1.2D, 0.45D, dz / d * 1.2D);
                        t.velocityChanged = true;
                    }
                }
                armor.consumeEnergy(chest, 22);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.PLAYERS, 0.7F, 1.5F);
            }
        }

        private void handleMarkVII(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            if (mode == 1) {
                toggleFlight(player, armor, chest, IronSuitType.MARK_VII);
                return;
            }
            if (mode == 0) {
                fireRepulsor(player, armor, chest, 10, 10.0F, 10, 2.5F, false);
                return;
            }
            if (mode == 2) { // Flare burst — knockback + blindness
                if (armor.getEnergy(chest) < 28 || !HeroAbilityHandler.readySpecial(player, 40)) {
                    return;
                }
                AxisAlignedBB box = player.getEntityBoundingBox().grow(7.0D);
                for (EntityLivingBase t : player.world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
                    if (t == player) continue;
                    t.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 50, 0));
                    t.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 100, 0));
                    double dx = t.posX - player.posX;
                    double dz = t.posZ - player.posZ;
                    double d = Math.sqrt(dx * dx + dz * dz);
                    if (d > 0.001D) {
                        t.addVelocity(dx / d * 1.6D, 0.5D, dz / d * 1.6D);
                        t.velocityChanged = true;
                    }
                }
                armor.consumeEnergy(chest, 28);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.PLAYERS, 1.0F, 0.7F);
                if (player.world instanceof WorldServer) {
                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
                            player.posX, player.posY + 1.0D, player.posZ, 50, 2.0D, 1.0D, 2.0D, 0.1D);
                }
            }
        }

        private void handleMarkXLII(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            if (mode == 1) {
                toggleFlight(player, armor, chest, IronSuitType.MARK_XLII);
                return;
            }
            if (mode == 0) {
                fireRepulsor(player, armor, chest, 10, 9.5F, 12, 2.4F, false);
                return;
            }
            if (mode == 2) { // Auto-repair burst
                if (armor.getEnergy(chest) < 30 || !HeroAbilityHandler.readySpecial(player, 60)) {
                    return;
                }
                for (ItemStack piece : player.getArmorInventoryList()) {
                    if (!piece.isEmpty() && piece.isItemDamaged()) {
                        piece.setItemDamage(Math.max(0, piece.getItemDamage() - 25));
                    }
                }
                player.heal(4.0F);
                armor.consumeEnergy(chest, 30);
                player.sendStatusMessage(new TextComponentString("§aAuto-repair engaged"), true);
                player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.6F, 1.4F);
            }
        }

        private void handleWarMachine(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            if (mode == 1) {
                toggleFlight(player, armor, chest, IronSuitType.WAR_MACHINE);
                return;
            }
            if (mode == 0) { // Missile barrage
                if (armor.getEnergy(chest) < 20 || !HeroAbilityHandler.readyPrimary(player, 22)) {
                    return;
                }
                for (int i = -2; i <= 2; i++) {
                    EntityRepulsorBlast missile = new EntityRepulsorBlast(player.world, player, 8.0F, true);
                    missile.shoot(player, player.rotationPitch - 5.0F, player.rotationYaw + i * 6.0F, 0.0F, 2.0F, 1.0F);
                    player.world.spawnEntity(missile);
                }
                armor.consumeEnergy(chest, 20);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 1.0F, 0.5F);
                return;
            }
            if (mode == 2) { // Minigun spin — rapid shots
                if (armor.getEnergy(chest) < 25 || !HeroAbilityHandler.readySpecial(player, 40)) {
                    return;
                }
                for (int i = 0; i < 6; i++) {
                    EntityRepulsorBlast shot = new EntityRepulsorBlast(player.world, player, 5.0F, false);
                    shot.shoot(player, player.rotationPitch + (player.world.rand.nextFloat() - 0.5F) * 4.0F,
                            player.rotationYaw + (player.world.rand.nextFloat() - 0.5F) * 8.0F, 0.0F, 3.0F, 2.0F);
                    player.world.spawnEntity(shot);
                }
                armor.consumeEnergy(chest, 25);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.4F, 2.0F);
            }
        }

        private void handleHulkbuster(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            if (mode == 1) {
                toggleFlight(player, armor, chest, IronSuitType.HULKBUSTER);
                return;
            }
            if (mode == 0) { // Power fist dash
                if (armor.getEnergy(chest) < 16 || !HeroAbilityHandler.readyPrimary(player, 18)) {
                    return;
                }
                Vec3d look = player.getLookVec();
                player.addVelocity(look.x * 1.4D, 0.25D, look.z * 1.4D);
                player.velocityChanged = true;
                AxisAlignedBB box = player.getEntityBoundingBox().grow(2.5D).offset(look.x * 2, 0, look.z * 2);
                for (EntityLivingBase t : player.world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
                    if (t == player) continue;
                    t.attackEntityFrom(DamageSource.causePlayerDamage(player), 14.0F);
                    t.addVelocity(look.x * 1.5D, 0.6D, look.z * 1.5D);
                    t.velocityChanged = true;
                }
                armor.consumeEnergy(chest, 16);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_IRONGOLEM_ATTACK, SoundCategory.PLAYERS, 1.0F, 0.7F);
                return;
            }
            if (mode == 2) { // Ground pound
                if (armor.getEnergy(chest) < 40 || !HeroAbilityHandler.readySpecial(player, 55)) {
                    return;
                }
                if (!player.onGround) {
                    player.motionY = -1.2D;
                    player.velocityChanged = true;
                }
                AxisAlignedBB box = player.getEntityBoundingBox().grow(6.0D, 2.0D, 6.0D);
                for (EntityLivingBase t : player.world.getEntitiesWithinAABB(EntityLivingBase.class, box)) {
                    if (t == player) continue;
                    t.attackEntityFrom(DamageSource.causePlayerDamage(player), 16.0F);
                    t.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, 2));
                    double dx = t.posX - player.posX;
                    double dz = t.posZ - player.posZ;
                    double d = Math.sqrt(dx * dx + dz * dz);
                    if (d > 0.001D) {
                        t.addVelocity(dx / d * 1.8D, 0.7D, dz / d * 1.8D);
                        t.velocityChanged = true;
                    }
                }
                armor.consumeEnergy(chest, 40);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.2F, 0.5F);
                if (player.world instanceof WorldServer) {
                    ((WorldServer) player.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE,
                            player.posX, player.posY, player.posZ, 8, 1.5D, 0.2D, 1.5D, 0.0D);
                }
            }
        }

        private void handleMarkL(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            if (mode == 1) {
                toggleFlight(player, armor, chest, IronSuitType.MARK_L);
                return;
            }
            if (mode == 0) {
                fireRepulsor(player, armor, chest, 8, 12.0F, 8, 2.9F, false);
                return;
            }
            if (mode == 2) { // Nanite surge
                if (armor.getEnergy(chest) < 40 || !HeroAbilityHandler.readySpecial(player, 50)) {
                    return;
                }
                for (int i = -1; i <= 1; i++) {
                    EntityRepulsorBlast beam = new EntityRepulsorBlast(player.world, player, 16.0F, true);
                    beam.shoot(player, player.rotationPitch, player.rotationYaw + i * 4.0F, 0.0F, 3.0F, 0.0F);
                    player.world.spawnEntity(beam);
                }
                player.heal(6.0F);
                player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 200, 1));
                for (ItemStack piece : player.getArmorInventoryList()) {
                    if (!piece.isEmpty() && piece.isItemDamaged()) {
                        piece.setItemDamage(Math.max(0, piece.getItemDamage() - 15));
                    }
                }
                armor.consumeEnergy(chest, 40);
                player.sendStatusMessage(new TextComponentString("§bNanite surge online"), true);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8F, 1.6F);
            }
        }

        private void handleStealth(EntityPlayerMP player, ItemIronSuitArmor armor, ItemStack chest, int mode) {
            if (mode == 1) {
                toggleFlight(player, armor, chest, IronSuitType.STEALTH);
                return;
            }
            if (mode == 0) { // Suppressed shot
                if (armor.getEnergy(chest) < 8 || !HeroAbilityHandler.readyPrimary(player, 10)) {
                    return;
                }
                EntityRepulsorBlast blast = new EntityRepulsorBlast(player.world, player, 8.0F, false);
                blast.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.6F, 0.05F);
                player.world.spawnEntity(blast);
                armor.consumeEnergy(chest, 8);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.25F, 1.8F);
                return;
            }
            if (mode == 2) { // Cloak
                if (armor.getEnergy(chest) < 25 || !HeroAbilityHandler.readySpecial(player, 70)) {
                    return;
                }
                HeroAbilityHandler.setCloakTicks(player, 160);
                player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 160, 0, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 160, 1, true, false));
                armor.consumeEnergy(chest, 25);
                player.sendStatusMessage(new TextComponentString("§8Cloak engaged"), true);
                player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.PLAYERS, 0.4F, 0.5F);
            }
        }

        private void handleSpider(EntityPlayerMP player, ItemHeroArmor armor, ItemStack chest, int mode) {
            if (mode == 0) {
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
            if (mode == 1) {
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
            if (mode == 2) {
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
            if (mode == 0) {
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
            if (mode == 1) {
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
            if (mode == 2) {
                if (armor.getEnergy(chest) < 20 || !HeroAbilityHandler.readySpecial(player, 35)) {
                    return;
                }
                Vec3d look = player.getLookVec();
                double dist = 8.0D;
                double tx = player.posX + look.x * dist;
                double ty = player.posY + look.y * dist;
                double tz = player.posZ + look.z * dist;
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
            if (mode == 0) {
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
            if (mode == 1) {
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
            if (mode == 2) {
                if (armor.getEnergy(chest) < 25 || !HeroAbilityHandler.readySpecial(player, 80)) {
                    return;
                }
                player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 20 * 20, 2));
                player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 20 * 15, 1));
                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20 * 10, 1));
                AxisAlignedBB box = player.getEntityBoundingBox().grow(8.0D);
                for (net.minecraft.entity.player.EntityPlayer ally : player.world.getEntitiesWithinAABB(net.minecraft.entity.player.EntityPlayer.class, box)) {
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
