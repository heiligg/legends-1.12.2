package com.heiligg.legends.network;

import com.heiligg.legends.handler.ArmorAbilityHandler;
import com.heiligg.legends.item.ItemLegendaryArmor;
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
                    if (!(chest.getItem() instanceof ItemLegendaryArmor)) {
                        return;
                    }

                    ItemLegendaryArmor armor = (ItemLegendaryArmor) chest.getItem();
                    if (armor.getPower(chest) < 20) {
                        player.sendMessage(new TextComponentString("Not enough legend power to dash!"));
                        return;
                    }

                    Vec3d look = player.getLookVec();
                    player.addVelocity(look.x * 1.8D, 0.25D, look.z * 1.8D);
                    player.velocityChanged = true;
                    armor.consumePower(chest, 20);
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
