package com.heiligg.legends.hero;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;

public class HeroHUD {

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT || mc.player == null) {
            return;
        }

        HeroType set = ItemHeroArmor.getWornHeroSet(mc.player);
        if (set == null) {
            return;
        }

        int total = 0;
        int max = 0;
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) {
                continue;
            }
            ItemStack stack = mc.player.getItemStackFromSlot(slot);
            if (stack.getItem() instanceof ItemHeroArmor) {
                ItemHeroArmor armor = (ItemHeroArmor) stack.getItem();
                total += armor.getEnergy(stack);
                max += armor.getHeroType().maxEnergy;
            }
        }
        if (max <= 0) {
            return;
        }

        ScaledResolution res = event.getResolution();
        int barWidth = 120;
        int barHeight = 8;
        int x = res.getScaledWidth() / 2 - barWidth / 2;
        int y = res.getScaledHeight() - 66;
        int filled = (int) (barWidth * ((float) total / (float) max));

        Color fill;
        String abilities;
        switch (set) {
            case IRON_MAN:
                fill = new Color(220, 50, 40, 220);
                abilities = HeroAbilityHandler.isIronFlight(mc.player)
                        ? "G Repulsor | F Flight ON | V Unibeam"
                        : "G Repulsor | F Flight | V Unibeam";
                break;
            case SPIDER_MAN:
                fill = new Color(200, 30, 50, 220);
                abilities = "G Zip | F Web Shot | V Leap | Sneak=Glide/Cling";
                break;
            case FLASH:
                fill = new Color(255, 190, 20, 220);
                abilities = HeroAbilityHandler.isSpeedForce(mc.player)
                        ? "G Burst | F Speed Force ON | V Blink"
                        : "G Burst | F Speed Force | V Blink";
                break;
            case CAPTAIN_AMERICA:
                fill = new Color(40, 80, 200, 220);
                abilities = "G Throw | F Bash | V Rally | Shield block";
                break;
            default:
                fill = new Color(80, 180, 255, 220);
                abilities = "";
                break;
        }

        mc.ingameGUI.drawRect(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, new Color(0, 0, 0, 160).getRGB());
        mc.ingameGUI.drawRect(x, y, x + barWidth, y + barHeight, new Color(20, 20, 30, 180).getRGB());
        mc.ingameGUI.drawRect(x, y, x + filled, y + barHeight, fill.getRGB());
        mc.fontRenderer.drawStringWithShadow(set.displayName + " " + total + "/" + max, x, y - 20, 0xFFFFFF);
        mc.fontRenderer.drawStringWithShadow(abilities, x, y - 10, 0xCCCCCC);
    }
}
