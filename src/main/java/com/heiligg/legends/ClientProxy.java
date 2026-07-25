package com.heiligg.legends;

import com.heiligg.legends.client.RenderLegendGuardian;
import com.heiligg.legends.client.RenderLegendKnight;
import com.heiligg.legends.client.RenderLegendWraith;
import com.heiligg.legends.entity.EntityArcaneBolt;
import com.heiligg.legends.entity.EntityLegendGuardian;
import com.heiligg.legends.entity.EntityLegendKnight;
import com.heiligg.legends.entity.EntityLegendWraith;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityLegendWraith.class, new IRenderFactory<EntityLegendWraith>() {
            @Override
            public Render<? super EntityLegendWraith> createRenderFor(RenderManager manager) {
                return new RenderLegendWraith(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityLegendGuardian.class, new IRenderFactory<EntityLegendGuardian>() {
            @Override
            public Render<? super EntityLegendGuardian> createRenderFor(RenderManager manager) {
                return new RenderLegendGuardian(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityLegendKnight.class, new IRenderFactory<EntityLegendKnight>() {
            @Override
            public Render<? super EntityLegendKnight> createRenderFor(RenderManager manager) {
                return new RenderLegendKnight(manager);
            }
        });
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Item[] items = new Item[]{
                LegendsMod.legendEssence, LegendsMod.legendFragment, LegendsMod.ascendedCore,
                LegendsMod.legendaryBlade, LegendsMod.ascendedBlade, LegendsMod.legendaryBow, LegendsMod.ascendedBow,
                LegendsMod.legendaryStaff, LegendsMod.ascendedStaff, LegendsMod.legendaryPickaxe, LegendsMod.legendaryAxe,
                LegendsMod.legendaryShovel, LegendsMod.legendaryShield, LegendsMod.legendAmulet,
                LegendsMod.legendTotem, LegendsMod.legendElixir,
                LegendsMod.legendaryHelmet, LegendsMod.legendaryChest, LegendsMod.legendaryLegs, LegendsMod.legendaryBoots,
                LegendsMod.ascendedHelmet, LegendsMod.ascendedChest, LegendsMod.ascendedLegs, LegendsMod.ascendedBoots,
                LegendsMod.legendOreItem, LegendsMod.legendBrickItem, LegendsMod.legendAltarItem
        };
        for (Item item : items) {
            registerItemModel(item);
        }
    }

    private static void registerItemModel(Item item) {
        if (item != null && item.getRegistryName() != null) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}
