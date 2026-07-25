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

    public static KeyBinding ABILITY_KEY;
    public static KeyBinding FLIGHT_KEY;

    public static void registerKeyBindings() {
        ABILITY_KEY = new KeyBinding("key.legends.hero_ability", Keyboard.KEY_G, "key.categories.legends");
        FLIGHT_KEY = new KeyBinding("key.legends.hero_flight", Keyboard.KEY_F, "key.categories.legends");
        ClientRegistry.registerKeyBinding(ABILITY_KEY);
        ClientRegistry.registerKeyBinding(FLIGHT_KEY);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }
        HeroType set = ItemHeroArmor.getWornHeroSet(player);
        if (set == null) {
            return;
        }

        if (ABILITY_KEY != null && ABILITY_KEY.isPressed()) {
            LegendsMod.network.sendToServer(new HeroAbilityPacket(0));
        }
        if (FLIGHT_KEY != null && FLIGHT_KEY.isPressed() && set == HeroType.IRON_MAN) {
            LegendsMod.network.sendToServer(new HeroAbilityPacket(1));
        }
    }
}
