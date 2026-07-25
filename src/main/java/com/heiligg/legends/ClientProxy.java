package com.heiligg.legends;

import com.heiligg.legends.handler.KeyInputHandler;
import com.heiligg.legends.handler.LegendHUD;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = LegendsMod.MODID, value = Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        KeyInputHandler.registerKeyBindings();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        MinecraftForge.EVENT_BUS.register(new LegendHUD());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerItemModel(LegendsMod.legendEssence);
        registerItemModel(LegendsMod.legendaryBlade);
        registerItemModel(LegendsMod.legendaryBow);
        registerItemModel(LegendsMod.legendAmulet);
        registerItemModel(LegendsMod.legendaryHelmet);
        registerItemModel(LegendsMod.legendaryChest);
        registerItemModel(LegendsMod.legendaryLegs);
        registerItemModel(LegendsMod.legendaryBoots);
    }

    private static void registerItemModel(Item item) {
        if (item != null && item.getRegistryName() != null) {
            ModelLoader.setCustomModelResourceLocation(
                    item,
                    0,
                    new ModelResourceLocation(item.getRegistryName(), "inventory")
            );
        }
    }
}
