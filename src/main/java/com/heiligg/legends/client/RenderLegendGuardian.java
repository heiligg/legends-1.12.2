package com.heiligg.legends.client;

import com.heiligg.legends.entity.EntityLegendGuardian;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLegendGuardian extends RenderLiving<EntityLegendGuardian> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/zombie/zombie.png");

    public RenderLegendGuardian(RenderManager manager) {
        super(manager, new ModelZombie(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLegendGuardian entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityLegendGuardian entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale(1.35F, 1.45F, 1.35F);
        GlStateManager.color(0.35F, 0.55F, 1.0F, 1.0F);
    }

    @Override
    public void doRender(EntityLegendGuardian entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
