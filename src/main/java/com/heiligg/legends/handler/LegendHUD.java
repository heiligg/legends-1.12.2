package com.heiligg.legends.handler;

import com.heiligg.legends.item.ItemLegendaryArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;

public class LegendHUD {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderHUD(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }
        if (mc.player == null) {
            return;
        }

        int totalPower = 0;
        int maxPower = 0;
        int pieces = 0;

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) {
                continue;
            }
            ItemStack stack = mc.player.getItemStackFromSlot(slot);
            if (stack.getItem() instanceof ItemLegendaryArmor) {
                ItemLegendaryArmor armor = (ItemLegendaryArmor) stack.getItem();
                totalPower += armor.getPower(stack);
                maxPower += ItemLegendaryArmor.MAX_POWER;
                pieces++;
            }
        }

        if (pieces == 0) {
            return;
        }

        ScaledResolution res = event.getResolution();
        int barWidth = 100;
        int barHeight = 8;
        int x = res.getScaledWidth() / 2 - barWidth / 2;
        int y = res.getScaledHeight() - 52;

        float percent = maxPower == 0 ? 0F : (float) totalPower / (float) maxPower;
        int filled = (int) (barWidth * percent);

        mc.ingameGUI.drawRect(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, new Color(0, 0, 0, 160).getRGB());
        mc.ingameGUI.drawRect(x, y, x + barWidth, y + barHeight, new Color(20, 20, 40, 180).getRGB());
        mc.ingameGUI.drawRect(x, y, x + filled, y + barHeight, new Color(40, 180, 220, 220).getRGB());
        mc.fontRenderer.drawStringWithShadow("Legend Power: " + totalPower + "/" + maxPower, x, y - 11, 0x7FDFFF);
    }
}
