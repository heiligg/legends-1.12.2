package com.heiligg.legends.client;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.entity.EntityLegendKnight;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLegendKnight extends RenderBiped<EntityLegendKnight> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LegendsMod.MODID, "textures/entity/legend_knight.png");

    public RenderLegendKnight(RenderManager manager) {
        super(manager, new ModelBiped(), 0.5F);
        addLayer(new LayerBipedArmor(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLegendKnight entity) {
        return TEXTURE;
    }
}
