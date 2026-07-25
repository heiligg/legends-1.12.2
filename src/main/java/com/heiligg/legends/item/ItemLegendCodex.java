package com.heiligg.legends.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Lightweight lore guidebook — the Legends identity spine in item form.
 */
public class ItemLegendCodex extends Item {

    private static final String[] PAGES = new String[]{
            "§6The Well of Legends§r — Power of fallen heroes pools in the earth as ore and essence.",
            "§ePath§r: Fragments → Forged kit → Essence → Legendary → Shrine trial → Ascended.",
            "§bShrines§r mark old trial grounds. Offer Essence at a shrine altar to awaken the Guardian.",
            "§cHero relics§r (Iron Man, Spider-Man, Flash, Cap) drink the same Well — forge them with Essence.",
            "§aAscension§r: defeat the Guardian for an Ascended Core, then remake Legendary gear."
    };

    public ItemLegendCodex() {
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            int page = stack.hasTagCompound() ? stack.getTagCompound().getInteger("Page") : 0;
            player.sendMessage(new TextComponentString(TextFormatting.DARK_AQUA + "— Legend Codex —"));
            player.sendMessage(new TextComponentString(PAGES[page % PAGES.length]));
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
            }
            stack.getTagCompound().setInteger("Page", (page + 1) % PAGES.length);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.AQUA + "Right-click to read the Path of Legends");
        tooltip.add(TextFormatting.DARK_GRAY + "Found in shrines, or crafted with Essence + book");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
