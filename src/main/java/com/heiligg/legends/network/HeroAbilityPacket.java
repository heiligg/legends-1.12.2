package com.heiligg.legends.network;

import com.heiligg.legends.entity.EntityArcaneBolt;
import com.heiligg.legends.hero.HeroAbilityHandler;
import com.heiligg.legends.hero.HeroType;
import com.heiligg.legends.hero.ItemHeroArmor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * mode: 0 = primary ability (G), 1 = secondary/flight toggle (F)
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

                    if (mode == 1) {
                        if (set == HeroType.IRON_MAN) {
                            if (armor.getEnergy(chest) < 5 && !HeroAbilityHandler.isIronFlight(player)) {
                                player.sendMessage(new TextComponentString("Not enough suit energy to fly!"));
                                return;
                            }
                            HeroAbilityHandler.toggleIronFlight(player);
                            boolean on = HeroAbilityHandler.isIronFlight(player);
                            player.sendMessage(new TextComponentString(on ? "Flight systems online." : "Flight systems offline."));
                            player.world.playSound(null, player.getPosition(),
                                    SoundEvents.BLOCK_NOTE_PLING, SoundCategory.PLAYERS, 0.5F, on ? 1.4F : 0.8F);
                        }
                        return;
                    }

                    // Primary ability
                    switch (set) {
                        case IRON_MAN:
                            if (armor.getEnergy(chest) < 12) {
                                player.sendMessage(new TextComponentString("Not enough energy for repulsor!"));
                                return;
                            }
                            EntityArcaneBolt bolt = new EntityArcaneBolt(player.world, player);
                            bolt.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.2F, 0.3F);
                            player.world.spawnEntity(bolt);
                            armor.consumeEnergy(chest, 12);
                            player.world.playSound(null, player.getPosition(),
                                    SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.8F, 1.6F);
                            break;
                        case SPIDER_MAN:
                            if (armor.getEnergy(chest) < 10) {
                                player.sendMessage(new TextComponentString("Not enough energy to web-zip!"));
                                return;
                            }
                            Vec3d look = player.getLookVec();
                            player.addVelocity(look.x * 1.6D, look.y * 1.1D + 0.35D, look.z * 1.6D);
                            player.velocityChanged = true;
                            player.fallDistance = 0.0F;
                            armor.consumeEnergy(chest, 10);
                            player.world.playSound(null, player.getPosition(),
                                    SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.PLAYERS, 0.8F, 1.8F);
                            break;
                        case FLASH:
                            if (armor.getEnergy(chest) < 15) {
                                player.sendMessage(new TextComponentString("Not enough energy for speed burst!"));
                                return;
                            }
                            Vec3d dir = player.getLookVec();
                            player.addVelocity(dir.x * 2.8D, 0.05D, dir.z * 2.8D);
                            player.velocityChanged = true;
                            armor.consumeEnergy(chest, 15);
                            player.world.playSound(null, player.getPosition(),
                                    SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 0.7F, 1.8F);
                            break;
                        default:
                            break;
                    }
                }
            });
            return null;
        }
    }
}
