package com.heiligg.legends.network;

import com.heiligg.legends.entity.EntityArcaneBolt;
import com.heiligg.legends.item.ItemAscendedStaff;
import com.heiligg.legends.item.ItemLegendaryStaff;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
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

    private static boolean isStaff(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof ItemLegendaryStaff || stack.getItem() instanceof ItemAscendedStaff);
    }

    public static class Handler implements IMessageHandler<StaffBoltPacket, IMessage> {

        @Override
        public IMessage onMessage(StaffBoltPacket message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    ItemStack held = player.getHeldItemMainhand();
                    if (!isStaff(held)) {
                        held = player.getHeldItemOffhand();
                    }
                    if (!isStaff(held)) {
                        return;
                    }

                    boolean ascended = held.getItem() instanceof ItemAscendedStaff;
                    int bolts = ascended ? 3 : 1;
                    for (int i = 0; i < bolts; i++) {
                        EntityArcaneBolt bolt = new EntityArcaneBolt(player.world, player);
                        float yawSpread = ascended ? (i - 1) * 6.0F : 0.0F;
                        bolt.shoot(player, player.rotationPitch, player.rotationYaw + yawSpread, 0.0F, ascended ? 2.1F : 1.8F, 0.4F);
                        player.world.spawnEntity(bolt);
                    }
                    held.damageItem(ascended ? 1 : 1, player);
                    player.world.playSound(
                            null,
                            player.posX,
                            player.posY,
                            player.posZ,
                            SoundEvents.ENTITY_BLAZE_SHOOT,
                            SoundCategory.PLAYERS,
                            0.7F,
                            ascended ? 1.1F : 1.4F
                    );
                }
            });
            return null;
        }
    }
}
