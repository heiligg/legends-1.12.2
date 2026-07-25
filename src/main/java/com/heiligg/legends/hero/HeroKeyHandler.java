package com.heiligg.legends.hero;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.network.HeroAbilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class HeroKeyHandler {

    public static KeyBinding PRIMARY_KEY;
    public static KeyBinding SECONDARY_KEY;
    public static KeyBinding SPECIAL_KEY;

    public static void registerKeyBindings() {
        PRIMARY_KEY = new KeyBinding("key.legends.hero_primary", Keyboard.KEY_G, "key.categories.legends");
        SECONDARY_KEY = new KeyBinding("key.legends.hero_secondary", Keyboard.KEY_F, "key.categories.legends");
        SPECIAL_KEY = new KeyBinding("key.legends.hero_special", Keyboard.KEY_V, "key.categories.legends");
        ClientRegistry.registerKeyBinding(PRIMARY_KEY);
        ClientRegistry.registerKeyBinding(SECONDARY_KEY);
        ClientRegistry.registerKeyBinding(SPECIAL_KEY);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || ItemHeroArmor.getWornHeroSet(player) == null) {
            return;
        }
        if (PRIMARY_KEY != null && PRIMARY_KEY.isPressed()) {
            LegendsMod.network.sendToServer(new HeroAbilityPacket(0));
        }
        if (SECONDARY_KEY != null && SECONDARY_KEY.isPressed()) {
            LegendsMod.network.sendToServer(new HeroAbilityPacket(1));
        }
        if (SPECIAL_KEY != null && SPECIAL_KEY.isPressed()) {
            LegendsMod.network.sendToServer(new HeroAbilityPacket(2));
        }
    }
}
