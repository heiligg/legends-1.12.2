package com.heiligg.legends.block;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.config.LegendsConfig;
import com.heiligg.legends.entity.EntityLegendGuardian;
import com.heiligg.legends.init.ModSounds;
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
                playerIn.sendMessage(new TextComponentString(
                        "§6Well of Legends§r — Offer Legend Essence to awaken the Guardian."));
            }
            return true;
        }

        if (worldIn.isRemote) {
            return true;
        }

        if (LegendsConfig.altarRequiresShrine && !hasShrineFoundation(worldIn, pos)) {
            playerIn.sendMessage(new TextComponentString(
                    "The Well rejects a solitary altar. Build upon Legend Brick (or find a Shrine)."));
            return true;
        }

        AxisAlignedBB area = new AxisAlignedBB(pos).grow(16.0D, 8.0D, 16.0D);
        List<EntityLegendGuardian> nearby = worldIn.getEntitiesWithinAABB(EntityLegendGuardian.class, area);
        if (!nearby.isEmpty()) {
            playerIn.sendMessage(new TextComponentString("A Legend Guardian is already awake nearby."));
            return true;
        }

        int cost = Math.max(1, LegendsConfig.altarEssenceCost);
        if (!playerIn.capabilities.isCreativeMode) {
            if (held.getCount() < cost) {
                playerIn.sendMessage(new TextComponentString("Need " + cost + " Legend Essence to begin the trial."));
                return true;
            }
            held.shrink(cost);
        }

        EntityLegendGuardian guardian = new EntityLegendGuardian(worldIn);
        guardian.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0F, 0.0F);
        worldIn.spawnEntity(guardian);

        net.minecraft.util.SoundEvent awaken = ModSounds.GUARDIAN_AWAKEN != null
                ? ModSounds.GUARDIAN_AWAKEN : SoundEvents.ENTITY_WITHER_SPAWN;
        worldIn.playSound(null, pos, awaken, SoundCategory.HOSTILE, 0.9F, 1.0F);
        if (worldIn instanceof net.minecraft.world.WorldServer) {
            ((net.minecraft.world.WorldServer) worldIn).spawnParticle(
                    EnumParticleTypes.CRIT_MAGIC,
                    pos.getX() + 0.5D,
                    pos.getY() + 1.2D,
                    pos.getZ() + 0.5D,
                    50,
                    0.8D,
                    0.5D,
                    0.8D,
                    0.06D
            );
        }

        playerIn.sendMessage(new TextComponentString(
                "§cThe Legend Guardian rises from the Well — prove yourself!"));
        return true;
    }

    /** True if enough Legend Brick surrounds the altar (shrine / player arena). */
    private static boolean hasShrineFoundation(World world, BlockPos pos) {
        if (LegendsMod.legendBrick == null) {
            return true;
        }
        int bricks = 0;
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    if (world.getBlockState(pos.add(x, y, z)).getBlock() == LegendsMod.legendBrick) {
                        bricks++;
                    }
                }
            }
        }
        return bricks >= 12;
    }
}
