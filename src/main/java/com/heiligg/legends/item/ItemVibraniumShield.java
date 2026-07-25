package com.heiligg.legends.item;

import com.heiligg.legends.entity.EntityVibraniumShield;
import com.heiligg.legends.hero.HeroType;
import com.heiligg.legends.hero.ItemHeroArmor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemVibraniumShield extends Item {

    public ItemVibraniumShield() {
        setMaxStackSize(1);
        setMaxDamage(1200);
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        // Sneak + right click throws; otherwise block
        if (player.isSneaking()) {
            if (ItemHeroArmor.getWornHeroSet(player) != HeroType.CAPTAIN_AMERICA) {
                if (!world.isRemote) {
                    player.sendMessage(new net.minecraft.util.text.TextComponentString("Need the full Captain America suit to throw."));
                }
                return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
            }
            if (!world.isRemote) {
                EntityVibraniumShield shield = new EntityVibraniumShield(world, player);
                shield.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.8F, 0.5F);
                world.spawnEntity(shield);
                stack.damageItem(1, player);
                player.getCooldownTracker().setCooldown(this, 25);
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        player.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.RED + "Hold RMB to block");
        tooltip.add(TextFormatting.GOLD + "Sneak + RMB: Throw (returns)");
        tooltip.add(TextFormatting.GRAY + "Requires Captain America suit to throw");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
