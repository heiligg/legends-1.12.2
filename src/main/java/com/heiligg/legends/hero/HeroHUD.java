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

        IronSuitType iron = ItemIronSuitArmor.getWornSuit(mc.player);
        HeroType set = ItemHeroArmor.getWornHeroSet(mc.player);
        if (iron == null && set == null) {
            return;
        }

        int total = 0;
        int max = 0;
        String title;
        String abilities;
        Color fill;

        if (iron != null) {
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) {
                    continue;
                }
                ItemStack stack = mc.player.getItemStackFromSlot(slot);
                if (stack.getItem() instanceof ItemIronSuitArmor) {
                    ItemIronSuitArmor armor = (ItemIronSuitArmor) stack.getItem();
                    total += armor.getEnergy(stack);
                    max += armor.getSuitType().maxEnergy;
                }
            }
            title = "Iron Man " + iron.displayName;
            if (iron == IronSuitType.MARK_I) {
                abilities = iron.controls;
            } else {
                abilities = iron.controls.replace("F Flight", HeroAbilityHandler.isIronFlight(mc.player) ? "F Flight ON" : "F Flight");
                abilities = abilities.replace("F Hover Flight", HeroAbilityHandler.isIronFlight(mc.player) ? "F Hover Flight ON" : "F Hover Flight");
                abilities = abilities.replace("F Heavy Hover", HeroAbilityHandler.isIronFlight(mc.player) ? "F Heavy Hover ON" : "F Heavy Hover");
                abilities = abilities.replace("F Silent Flight", HeroAbilityHandler.isIronFlight(mc.player) ? "F Silent Flight ON" : "F Silent Flight");
            }
            if (HeroAbilityHandler.isCloaked(mc.player)) {
                abilities = abilities + " | CLOAKED";
            }
            fill = suitColor(iron);
        } else {
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
            title = set.displayName;
            switch (set) {
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

        mc.ingameGUI.drawRect(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, new Color(0, 0, 0, 160).getRGB());
        mc.ingameGUI.drawRect(x, y, x + barWidth, y + barHeight, new Color(20, 20, 30, 180).getRGB());
        mc.ingameGUI.drawRect(x, y, x + filled, y + barHeight, fill.getRGB());
        mc.fontRenderer.drawStringWithShadow(title + " " + total + "/" + max, x, y - 20, 0xFFFFFF);
        mc.fontRenderer.drawStringWithShadow(abilities, x, y - 10, 0xCCCCCC);
    }

    private static Color suitColor(IronSuitType suit) {
        switch (suit) {
            case MARK_I:
                return new Color(120, 90, 50, 220);
            case MARK_III:
                return new Color(220, 50, 40, 220);
            case MARK_V:
                return new Color(200, 40, 160, 220);
            case MARK_VII:
                return new Color(230, 60, 50, 220);
            case MARK_XLII:
                return new Color(210, 140, 40, 220);
            case WAR_MACHINE:
                return new Color(80, 90, 100, 220);
            case HULKBUSTER:
                return new Color(180, 40, 30, 220);
            case MARK_L:
                return new Color(230, 70, 40, 220);
            case STEALTH:
                return new Color(40, 50, 60, 220);
            default:
                return new Color(220, 50, 40, 220);
        }
    }
}
