package com.heiligg.legends.hero;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HeroAbilityHandler {

    private static final Map<UUID, Boolean> IRON_FLIGHT = new HashMap<UUID, Boolean>();
    private static final Map<UUID, Boolean> FLASH_SPEED_FORCE = new HashMap<UUID, Boolean>();
    private static final Map<UUID, Long> COOLDOWN_PRIMARY = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> COOLDOWN_SECONDARY = new HashMap<UUID, Long>();
    private static final Map<UUID, Long> COOLDOWN_SPECIAL = new HashMap<UUID, Long>();
    private static final Map<UUID, Integer> SPIDER_SENSE_CD = new HashMap<UUID, Integer>();
    private static final Map<UUID, Integer> CLOAK_TICKS = new HashMap<UUID, Integer>();

    public static void setIronFlight(EntityPlayer player, boolean flying) {
        IRON_FLIGHT.put(player.getUniqueID(), flying);
    }

    public static boolean isIronFlight(EntityPlayer player) {
        return IRON_FLIGHT.getOrDefault(player.getUniqueID(), false);
    }

    public static void toggleIronFlight(EntityPlayer player) {
        setIronFlight(player, !isIronFlight(player));
    }

    public static boolean isSpeedForce(EntityPlayer player) {
        return FLASH_SPEED_FORCE.getOrDefault(player.getUniqueID(), false);
    }

    public static void toggleSpeedForce(EntityPlayer player) {
        FLASH_SPEED_FORCE.put(player.getUniqueID(), !isSpeedForce(player));
    }

    public static void setCloakTicks(EntityPlayer player, int ticks) {
        CLOAK_TICKS.put(player.getUniqueID(), ticks);
    }

    public static boolean isCloaked(EntityPlayer player) {
        return CLOAK_TICKS.getOrDefault(player.getUniqueID(), 0) > 0;
    }

    public static boolean ready(EntityPlayer player, Map<UUID, Long> map, int cooldownTicks) {
        long now = player.world.getTotalWorldTime();
        Long next = map.get(player.getUniqueID());
        if (next != null && next > now) {
            return false;
        }
        map.put(player.getUniqueID(), now + cooldownTicks);
        return true;
    }

    public static boolean readyPrimary(EntityPlayer player, int cd) {
        return ready(player, COOLDOWN_PRIMARY, cd);
    }

    public static boolean readySecondary(EntityPlayer player, int cd) {
        return ready(player, COOLDOWN_SECONDARY, cd);
    }

    public static boolean readySpecial(EntityPlayer player, int cd) {
        return ready(player, COOLDOWN_SPECIAL, cd);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        EntityPlayer player = event.player;
        IronSuitType ironSuit = ItemIronSuitArmor.getWornSuit(player);
        HeroType set = ItemHeroArmor.getWornHeroSet(player);

        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ItemHeroArmor) {
                ItemHeroArmor armor = (ItemHeroArmor) stack.getItem();
                int energy = armor.getEnergy(stack);
                if (energy < armor.getHeroType().maxEnergy) {
                    armor.setEnergy(stack, energy + armor.getHeroType().rechargeRate);
                }
            } else if (stack.getItem() instanceof ItemIronSuitArmor) {
                ItemIronSuitArmor armor = (ItemIronSuitArmor) stack.getItem();
                int energy = armor.getEnergy(stack);
                if (energy < armor.getSuitType().maxEnergy) {
                    armor.setEnergy(stack, energy + armor.getSuitType().rechargeRate);
                }
            }
        }

        int cloak = CLOAK_TICKS.getOrDefault(player.getUniqueID(), 0);
        if (cloak > 0) {
            CLOAK_TICKS.put(player.getUniqueID(), cloak - 1);
            player.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 25, 0, true, false));
        }

        if (ironSuit != null) {
            ItemStack chest = ItemIronSuitArmor.getChest(player);
            if (!(chest.getItem() instanceof ItemIronSuitArmor)) {
                return;
            }
            ItemIronSuitArmor armor = (ItemIronSuitArmor) chest.getItem();
            if (player.ticksExisted % 20 == 0) {
                applyIronSetBonus(player, ironSuit);
            }
            tickIronSuit(player, ironSuit, armor, chest);
            return;
        }

        if (set == null) {
            clearHeroState(player);
            return;
        }

        ItemStack chest = ItemHeroArmor.getChest(player);
        if (!(chest.getItem() instanceof ItemHeroArmor)) {
            return;
        }
        ItemHeroArmor armor = (ItemHeroArmor) chest.getItem();

        if (player.ticksExisted % 20 == 0) {
            applySetBonus(player, set);
        }

        switch (set) {
            case SPIDER_MAN:
                tickSpiderMan(player);
                break;
            case FLASH:
                tickFlash(player, armor, chest);
                break;
            case CAPTAIN_AMERICA:
                tickCaptain(player);
                break;
            default:
                break;
        }
    }

    private void clearHeroState(EntityPlayer player) {
        if (IRON_FLIGHT.getOrDefault(player.getUniqueID(), false)) {
            IRON_FLIGHT.put(player.getUniqueID(), false);
            if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
            }
        }
        FLASH_SPEED_FORCE.put(player.getUniqueID(), false);
        CLOAK_TICKS.put(player.getUniqueID(), 0);
    }

    private void tickIronSuit(EntityPlayer player, IronSuitType suit, ItemIronSuitArmor armor, ItemStack chest) {
        boolean flying = isIronFlight(player);

        if (suit == IronSuitType.MARK_XLII && !player.world.isRemote && player.ticksExisted % 40 == 0) {
            for (ItemStack piece : player.getArmorInventoryList()) {
                if (!piece.isEmpty() && piece.isItemDamaged()) {
                    piece.setItemDamage(Math.max(0, piece.getItemDamage() - 1));
                }
            }
        }

        if (suit == IronSuitType.HULKBUSTER && player.ticksExisted % 40 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 50, 1, true, false));
            player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 50, 0, true, false));
        }

        if (flying && suit.canFly && armor.getEnergy(chest) > 0) {
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.fallDistance = 0.0F;

            float speed = suit.flightSpeed;
            if (player.isSprinting()) {
                Vec3d look = player.getLookVec();
                player.motionX += look.x * 0.045D * speed;
                player.motionY += look.y * 0.03D * speed;
                player.motionZ += look.z * 0.045D * speed;
                if (!player.world.isRemote && player.ticksExisted % 8 == 0) {
                    armor.consumeEnergy(chest, suit == IronSuitType.STEALTH ? 1 : 2);
                }
            }

            if (!player.world.isRemote && player.ticksExisted % 8 == 0) {
                int cost = suit == IronSuitType.HULKBUSTER ? 2 : 1;
                armor.consumeEnergy(chest, cost);
                if (armor.getEnergy(chest) <= 0) {
                    setIronFlight(player, false);
                    player.sendMessage(new TextComponentString("Jarvis: Power depleted. Landing."));
                }
            }

            if (player.world.isRemote && player.ticksExisted % 2 == 0 && suit != IronSuitType.STEALTH) {
                player.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                        player.posX, player.posY, player.posZ, 0, -0.05D, 0);
            }
        } else if (!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = false;
            if (!flying) {
                player.capabilities.isFlying = false;
            }
        }

        if (flying && suit.canFly) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 220, 0, true, false));
        }
    }

    private void tickSpiderMan(EntityPlayer player) {
        player.fallDistance = Math.min(player.fallDistance, 3.0F);

        if (!player.onGround && player.isSneaking() && player.motionY < -0.08D) {
            player.motionY *= 0.65D;
            player.fallDistance = 0.0F;
        }

        if (!player.onGround && player.isSneaking() && player.collidedHorizontally) {
            player.motionY = Math.max(player.motionY, -0.05D);
            player.motionX *= 0.2D;
            player.motionZ *= 0.2D;
            player.fallDistance = 0.0F;
        }

        if (!player.world.isRemote) {
            int cd = SPIDER_SENSE_CD.getOrDefault(player.getUniqueID(), 0);
            if (cd > 0) {
                SPIDER_SENSE_CD.put(player.getUniqueID(), cd - 1);
            } else {
                AxisAlignedBB box = player.getEntityBoundingBox().grow(12.0D);
                List<EntityMob> threats = player.world.getEntitiesWithinAABB(EntityMob.class, box);
                if (!threats.isEmpty()) {
                    player.sendStatusMessage(new TextComponentString("§cSpider-Sense: danger nearby!"), true);
                    player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_NOTE_BELL,
                            SoundCategory.PLAYERS, 0.4F, 1.8F);
                    SPIDER_SENSE_CD.put(player.getUniqueID(), 60);
                    if (player.world instanceof WorldServer) {
                        ((WorldServer) player.world).spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                                player.posX, player.posY + 1.5D, player.posZ, 8, 0.4D, 0.3D, 0.4D, 0.01D);
                    }
                }
            }
        }
    }

    private void tickFlash(EntityPlayer player, ItemHeroArmor armor, ItemStack chest) {
        boolean force = isSpeedForce(player);
        if (force) {
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 30, 4, true, false));
            player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 30, 2, true, false));
            if (!player.world.isRemote && player.ticksExisted % 10 == 0) {
                armor.consumeEnergy(chest, 2);
                if (armor.getEnergy(chest) <= 0) {
                    FLASH_SPEED_FORCE.put(player.getUniqueID(), false);
                    player.sendMessage(new TextComponentString("Speed Force collapsed — energy empty."));
                }
            }
        }

        if (player.isSprinting() && player.isInWater()) {
            BlockPos under = new BlockPos(player.posX, player.posY - 0.2D, player.posZ);
            if (player.world.getBlockState(under).getMaterial().isLiquid() || player.isInWater()) {
                player.motionY = Math.max(player.motionY, 0.02D);
                Vec3d look = player.getLookVec();
                player.motionX += look.x * 0.08D;
                player.motionZ += look.z * 0.08D;
                player.fallDistance = 0.0F;
            }
        }

        if ((force || player.isSprinting()) && player.world.isRemote && player.ticksExisted % 2 == 0) {
            player.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC,
                    player.posX, player.posY + 0.2D, player.posZ, 0, 0.05D, 0);
        }
    }

    private void tickCaptain(EntityPlayer player) {
        if (player.ticksExisted % 40 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 50, 1, true, false));
        }
    }

    private void applyIronSetBonus(EntityPlayer player, IronSuitType suit) {
        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 40, 0, true, false));
        switch (suit) {
            case MARK_I:
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 0, true, false));
                break;
            case MARK_III:
            case MARK_VII:
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 1, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 0, true, false));
                break;
            case MARK_V:
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 1, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, 0, true, false));
                break;
            case MARK_XLII:
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 1, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 0, true, false));
                break;
            case WAR_MACHINE:
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 1, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 1, true, false));
                break;
            case HULKBUSTER:
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 2, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 2, true, false));
                break;
            case MARK_L:
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 2, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 1, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 0, true, false));
                break;
            case STEALTH:
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 1, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 220, 0, true, false));
                break;
            default:
                break;
        }
    }

    private void applySetBonus(EntityPlayer player, HeroType set) {
        switch (set) {
            case SPIDER_MAN:
                player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 40, 2, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 1, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 220, 0, true, false));
                break;
            case FLASH:
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 2, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 40, 1, true, false));
                break;
            case CAPTAIN_AMERICA:
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 0, true, false));
                player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 60, 1, true, false));
                break;
            default:
                break;
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (ItemIronSuitArmor.isWearingAnyIronSuit(player)) {
            event.setCanceled(true);
            return;
        }
        HeroType set = ItemHeroArmor.getWornHeroSet(player);
        if (set == HeroType.SPIDER_MAN || set == HeroType.FLASH) {
            event.setCanceled(true);
        } else if (set == HeroType.CAPTAIN_AMERICA) {
            event.setDamageMultiplier(0.3F);
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        IronSuitType suit = ItemIronSuitArmor.getWornSuit(player);
        if (suit != null) {
            float mult = 1.25F;
            if (suit == IronSuitType.HULKBUSTER) {
                mult = 1.8F;
            } else if (suit == IronSuitType.MARK_L) {
                mult = 1.5F;
            }
            event.setNewSpeed(event.getNewSpeed() * mult);
        }
        if (ItemHeroArmor.getWornHeroSet(player) == HeroType.FLASH && (player.isSprinting() || isSpeedForce(player))) {
            event.setNewSpeed(event.getNewSpeed() * (isSpeedForce(player) ? 2.5F : 1.6F));
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            if (event.getSource().getTrueSource() instanceof EntityPlayer) {
                EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
                if (ItemHeroArmor.getWornHeroSet(attacker) == HeroType.FLASH && attacker.isSprinting()) {
                    event.setAmount(event.getAmount() + (isSpeedForce(attacker) ? 6.0F : 3.0F));
                    event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 1));
                }
                IronSuitType suit = ItemIronSuitArmor.getWornSuit(attacker);
                if (suit == IronSuitType.HULKBUSTER) {
                    event.setAmount(event.getAmount() + 4.0F);
                } else if (suit == IronSuitType.WAR_MACHINE) {
                    event.setAmount(event.getAmount() + 2.0F);
                }
            }
            return;
        }

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (ItemIronSuitArmor.isWearingAnyIronSuit(player) && event.getSource().isFireDamage()) {
            event.setCanceled(true);
        }
        if (isCloaked(player) && event.getAmount() > 0.0F) {
            CLOAK_TICKS.put(player.getUniqueID(), 0);
            player.removePotionEffect(MobEffects.INVISIBILITY);
        }
        HeroType set = ItemHeroArmor.getWornHeroSet(player);
        if (set == HeroType.CAPTAIN_AMERICA && player.isActiveItemStackBlocking()) {
            event.setAmount(event.getAmount() * 0.45F);
        }
        if (set == HeroType.SPIDER_MAN && event.getSource().getTrueSource() instanceof EntityLivingBase) {
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 30, 2, true, false));
        }
    }
}
