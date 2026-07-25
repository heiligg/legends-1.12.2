package com.heiligg.legends.block;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.entity.EntityLegendGuardian;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.List;

public class BlockLegendAltar extends Block {

    public BlockLegendAltar() {
        super(Material.ROCK);
        setHardness(4.0F);
        setResistance(25.0F);
        setLightLevel(0.6F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(LegendsMod.TAB);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack held = playerIn.getHeldItem(hand);
        if (held.isEmpty() || held.getItem() != LegendsMod.legendEssence) {
            if (!worldIn.isRemote) {
                playerIn.sendMessage(new TextComponentString("Offer Legend Essence to awaken the Guardian."));
            }
            return true;
        }

        if (worldIn.isRemote) {
            return true;
        }

        AxisAlignedBB area = new AxisAlignedBB(pos).grow(16.0D, 8.0D, 16.0D);
        List<EntityLegendGuardian> nearby = worldIn.getEntitiesWithinAABB(EntityLegendGuardian.class, area);
        if (!nearby.isEmpty()) {
            playerIn.sendMessage(new TextComponentString("A Legend Guardian is already awake nearby."));
            return true;
        }

        if (!playerIn.capabilities.isCreativeMode) {
            held.shrink(1);
        }

        EntityLegendGuardian guardian = new EntityLegendGuardian(worldIn);
        guardian.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0F, 0.0F);
        worldIn.spawnEntity(guardian);

        worldIn.playSound(null, pos, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 0.8F, 1.1F);
        if (worldIn instanceof net.minecraft.world.WorldServer) {
            ((net.minecraft.world.WorldServer) worldIn).spawnParticle(
                    EnumParticleTypes.CRIT_MAGIC,
                    pos.getX() + 0.5D,
                    pos.getY() + 1.2D,
                    pos.getZ() + 0.5D,
                    40,
                    0.6D,
                    0.4D,
                    0.6D,
                    0.05D
            );
        }

        playerIn.sendMessage(new TextComponentString("The Legend Guardian answers your challenge!"));
        return true;
    }
}
