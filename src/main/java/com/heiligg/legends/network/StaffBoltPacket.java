package com.heiligg.legends.network;

import com.heiligg.legends.entity.EntityArcaneBolt;
import com.heiligg.legends.item.ItemLegendaryStaff;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StaffBoltPacket implements IMessage {

    public StaffBoltPacket() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<StaffBoltPacket, IMessage> {

        @Override
        public IMessage onMessage(StaffBoltPacket message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    ItemStack held = player.getHeldItemMainhand();
                    if (held.isEmpty() || !(held.getItem() instanceof ItemLegendaryStaff)) {
                        held = player.getHeldItemOffhand();
                    }
                    if (held.isEmpty() || !(held.getItem() instanceof ItemLegendaryStaff)) {
                        return;
                    }

                    EntityArcaneBolt bolt = new EntityArcaneBolt(player.world, player);
                    bolt.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.8F, 0.5F);
                    player.world.spawnEntity(bolt);
                    held.damageItem(1, player);
                    player.world.playSound(
                            null,
                            player.posX,
                            player.posY,
                            player.posZ,
                            SoundEvents.ENTITY_BLAZE_SHOOT,
                            SoundCategory.PLAYERS,
                            0.7F,
                            1.4F
                    );
                }
            });
            return null;
        }
    }
}
