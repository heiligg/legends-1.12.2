package com.heiligg.legends.item;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.network.ShockwavePacket;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAscendedBlade extends ItemSword {

    public ItemAscendedBlade(ToolMaterial material) {
        super(material);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            LegendsMod.network.sendToServer(new ShockwavePacket());
        }
        player.getCooldownTracker().setCooldown(this, 28);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 80, 1));
        target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 40, 0));
        return super.hitEntity(stack, target, attacker);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextFormatting.LIGHT_PURPLE + "Ascended shockwave");
        tooltip.add(TextFormatting.GRAY + "Hits inflict Weakness II and Wither.");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
