package com.heiligg.legends.network;

import com.heiligg.legends.config.LegendsConfig;
import com.heiligg.legends.handler.ArmorAbilityHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DashPacket implements IMessage {

    public DashPacket() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<DashPacket, IMessage> {

        @Override
        public IMessage onMessage(DashPacket message, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    if (!ArmorAbilityHandler.isWearingFullSet(player)) {
                        return;
                    }

                    ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                    int cost = LegendsConfig.dashPowerCost;
                    if (ArmorAbilityHandler.getChestPower(chest) < cost) {
                        player.sendMessage(new TextComponentString("Not enough legend power to dash!"));
                        return;
                    }

                    Vec3d look = player.getLookVec();
                    double boost = ArmorAbilityHandler.isWearingFullAscended(player) ? 2.2D : 1.8D;
                    player.addVelocity(look.x * boost, 0.25D, look.z * boost);
                    player.velocityChanged = true;
                    ArmorAbilityHandler.consumeChestPower(chest, cost);
                    player.world.playSound(
                            null,
                            player.posX,
                            player.posY,
                            player.posZ,
                            SoundEvents.ENTITY_ENDERDRAGON_FLAP,
                            SoundCategory.PLAYERS,
                            0.6F,
                            1.4F
                    );
                }
            });
            return null;
        }
    }
}
