package com.heiligg.legends;

import com.heiligg.legends.entity.EntityArcaneBolt;
import com.heiligg.legends.handler.KeyInputHandler;
import com.heiligg.legends.handler.LegendHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityArcaneBolt.class, new IRenderFactory<EntityArcaneBolt>() {
            @Override
            public Render<? super EntityArcaneBolt> createRenderFor(RenderManager manager) {
                return new RenderSnowball<EntityArcaneBolt>(manager, Items.ENDER_PEARL, Minecraft.getMinecraft().getRenderItem());
            }
        });
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerItemModel(LegendsMod.legendEssence);
        registerItemModel(LegendsMod.legendFragment);
        registerItemModel(LegendsMod.legendaryBlade);
        registerItemModel(LegendsMod.legendaryBow);
        registerItemModel(LegendsMod.legendaryStaff);
        registerItemModel(LegendsMod.legendAmulet);
        registerItemModel(LegendsMod.legendaryHelmet);
        registerItemModel(LegendsMod.legendaryChest);
        registerItemModel(LegendsMod.legendaryLegs);
        registerItemModel(LegendsMod.legendaryBoots);
        registerItemModel(LegendsMod.legendOreItem);
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
