package com.heiligg.legends.network;

import com.heiligg.legends.hero.HeroAbilityHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

/**
 * Server → client sync for sustained hero toggles (flight, Speed Force, cloak).
 */
public class HeroStatePacket implements IMessage {

    private long uuidMsb;
    private long uuidLsb;
    private boolean ironFlight;
    private boolean speedForce;
    private int cloakTicks;

    public HeroStatePacket() {
    }

    public HeroStatePacket(UUID id, boolean ironFlight, boolean speedForce, int cloakTicks) {
        this.uuidMsb = id.getMostSignificantBits();
        this.uuidLsb = id.getLeastSignificantBits();
        this.ironFlight = ironFlight;
        this.speedForce = speedForce;
        this.cloakTicks = cloakTicks;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuidMsb = buf.readLong();
        uuidLsb = buf.readLong();
        byte flags = buf.readByte();
        ironFlight = (flags & 1) != 0;
        speedForce = (flags & 2) != 0;
        cloakTicks = buf.readShort() & 0xFFFF;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(uuidMsb);
        buf.writeLong(uuidLsb);
        byte flags = 0;
        if (ironFlight) flags |= 1;
        if (speedForce) flags |= 2;
        buf.writeByte(flags);
        buf.writeShort(cloakTicks);
    }

    public static class Handler implements IMessageHandler<HeroStatePacket, IMessage> {
        @Override
        public IMessage onMessage(HeroStatePacket message, MessageContext ctx) {
            if (ctx.side != Side.CLIENT) {
                return null;
            }
            final UUID id = new UUID(message.uuidMsb, message.uuidLsb);
            final boolean flight = message.ironFlight;
            final boolean force = message.speedForce;
            final int cloak = message.cloakTicks;
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                @SideOnly(Side.CLIENT)
                public void run() {
                    HeroAbilityHandler.applyClientState(id, flight, force, cloak);
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (player != null && player.getUniqueID().equals(id)) {
                        // keep client capabilities in sync for local player
                        if (flight) {
                            player.capabilities.allowFlying = true;
                        }
                    }
                }
            });
            return null;
        }
    }
}
