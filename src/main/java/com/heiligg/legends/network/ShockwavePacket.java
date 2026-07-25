package com.heiligg.legends.network;

import com.heiligg.legends.item.ItemLegendaryBlade;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class ShockwavePacket implements IMessage {

    public ShockwavePacket() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<ShockwavePacket, IMessage> {

        @Override
        public IMessage onMessage(ShockwavePacket message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    ItemStack held = player.getHeldItemMainhand();
                    if (held.isEmpty() || !(held.getItem() instanceof ItemLegendaryBlade)) {
                        held = player.getHeldItemOffhand();
                    }
                    if (held.isEmpty() || !(held.getItem() instanceof ItemLegendaryBlade)) {
                        return;
                    }

                    WorldServer world = player.getServerWorld();
                    double radius = 4.0D;
                    AxisAlignedBB box = player.getEntityBoundingBox().grow(radius, 1.5D, radius);
                    List<EntityLivingBase> targets = world.getEntitiesWithinAABB(
                            EntityLivingBase.class,
                            box,
                            entity -> entity != null && entity.isEntityAlive() && entity != player
                    );

                    for (EntityLivingBase target : targets) {
                        target.attackEntityFrom(DamageSource.causePlayerDamage(player), 6.0F);
                        double dx = target.posX - player.posX;
                        double dz = target.posZ - player.posZ;
                        double dist = Math.sqrt(dx * dx + dz * dz);
                        if (dist > 0.001D) {
                            target.addVelocity(dx / dist * 1.2D, 0.35D, dz / dist * 1.2D);
                            target.velocityChanged = true;
                        }
                    }

                    world.spawnParticle(
                            EnumParticleTypes.CRIT_MAGIC,
                            player.posX,
                            player.posY + 1.0D,
                            player.posZ,
                            40,
                            1.5D,
                            0.4D,
                            1.5D,
                            0.08D
                    );
                    world.playSound(
                            null,
                            player.posX,
                            player.posY,
                            player.posZ,
                            SoundEvents.ENTITY_GENERIC_EXPLODE,
                            SoundCategory.PLAYERS,
                            0.5F,
                            1.6F
                    );

                    held.damageItem(1, player);
                }
            });
            return null;
        }
    }
}
