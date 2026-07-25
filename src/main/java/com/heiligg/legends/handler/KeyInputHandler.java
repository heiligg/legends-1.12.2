package com.heiligg.legends.handler;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.config.LegendsConfig;
import com.heiligg.legends.network.DashPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyInputHandler {

    public static KeyBinding DASH_KEY;

    public static void registerKeyBindings() {
        DASH_KEY = new KeyBinding(
                "key.legends.dash",
                Keyboard.KEY_R,
                "key.categories.legends"
        );
        ClientRegistry.registerKeyBinding(DASH_KEY);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) {
            return;
        }

        if (DASH_KEY != null && DASH_KEY.isPressed() && ArmorAbilityHandler.isWearingFullSet(player)) {
            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (ArmorAbilityHandler.getChestPower(chest) >= LegendsConfig.dashPowerCost) {
                LegendsMod.network.sendToServer(new DashPacket());
            }
        }
    }
}
