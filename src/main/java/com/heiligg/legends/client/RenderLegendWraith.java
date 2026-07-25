package com.heiligg.legends.client;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.entity.EntityLegendWraith;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLegendWraith extends RenderLiving<EntityLegendWraith> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LegendsMod.MODID, "textures/entity/legend_wraith.png");

    public RenderLegendWraith(RenderManager manager) {
        super(manager, new ModelZombie(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLegendWraith entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityLegendWraith entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(0.95F, 0.95F, 0.95F);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.85F);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void doRender(EntityLegendWraith entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
