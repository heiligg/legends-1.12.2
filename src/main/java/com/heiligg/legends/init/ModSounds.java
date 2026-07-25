package com.heiligg.legends.init;

import com.heiligg.legends.LegendsMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = LegendsMod.MODID)
public final class ModSounds {

    public static SoundEvent GUARDIAN_AWAKEN;
    public static SoundEvent WRAITH_AMBIENT;
    public static SoundEvent DASH;
    public static SoundEvent SHRINE_CHIME;

    private ModSounds() {
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<SoundEvent> event) {
        GUARDIAN_AWAKEN = sound("guardian.awaken");
        WRAITH_AMBIENT = sound("wraith.ambient");
        DASH = sound("dash");
        SHRINE_CHIME = sound("shrine.chime");
        event.getRegistry().registerAll(GUARDIAN_AWAKEN, WRAITH_AMBIENT, DASH, SHRINE_CHIME);
    }

    private static SoundEvent sound(String name) {
        ResourceLocation id = new ResourceLocation(LegendsMod.MODID, name);
        return new SoundEvent(id).setRegistryName(id);
    }
}
