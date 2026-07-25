package com.heiligg.legends;

import com.heiligg.legends.block.BlockLegendAltar;
import com.heiligg.legends.block.BlockLegendBrick;
import com.heiligg.legends.block.BlockLegendOre;
import com.heiligg.legends.block.BlockNetherLegendOre;
import com.heiligg.legends.config.LegendsConfig;
import com.heiligg.legends.enchantment.EnchantmentLegendStrike;
import com.heiligg.legends.entity.EntityArcaneBolt;
import com.heiligg.legends.entity.EntityLegendGuardian;
import com.heiligg.legends.entity.EntityLegendKnight;
import com.heiligg.legends.entity.EntityLegendWraith;
import com.heiligg.legends.handler.ArmorAbilityHandler;
import com.heiligg.legends.handler.CombatHandler;
import com.heiligg.legends.handler.LootHandler;
import com.heiligg.legends.handler.TotemHandler;
import com.heiligg.legends.hero.ModHeroes;
import com.heiligg.legends.init.ModRecipes;
import com.heiligg.legends.item.ItemAscendedArmor;
import com.heiligg.legends.item.ItemAscendedBlade;
import com.heiligg.legends.item.ItemAscendedBow;
import com.heiligg.legends.item.ItemAscendedCore;
import com.heiligg.legends.item.ItemAscendedStaff;
import com.heiligg.legends.item.ItemLegendAmulet;
import com.heiligg.legends.item.ItemLegendElixir;
import com.heiligg.legends.item.ItemLegendEssence;
import com.heiligg.legends.item.ItemLegendFragment;
import com.heiligg.legends.item.ItemLegendTotem;
import com.heiligg.legends.item.ItemLegendaryArmor;
import com.heiligg.legends.item.ItemLegendaryAxe;
import com.heiligg.legends.item.ItemLegendaryBlade;
import com.heiligg.legends.item.ItemLegendaryBow;
import com.heiligg.legends.item.ItemLegendaryPickaxe;
import com.heiligg.legends.item.ItemLegendaryShield;
import com.heiligg.legends.item.ItemLegendaryShovel;
import com.heiligg.legends.item.ItemLegendaryStaff;
import com.heiligg.legends.network.DashPacket;
import com.heiligg.legends.network.ShockwavePacket;
import com.heiligg.legends.network.StaffBoltPacket;
import com.heiligg.legends.world.LegendsWorldGen;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = LegendsMod.MODID, name = LegendsMod.NAME, version = LegendsMod.VERSION)
@Mod.EventBusSubscriber(modid = LegendsMod.MODID)
public class LegendsMod {

    public static final String MODID = "legends";
    public static final String NAME = "Legends";
    public static final String VERSION = "3.0.0";

    public static Logger logger;
    public static SimpleNetworkWrapper network;

    public static Block legendOre;
    public static Block netherLegendOre;
    public static Block legendBrick;
    public static Block legendAltar;
    public static Enchantment legendStrike;

    public static Item legendEssence;
    public static Item legendFragment;
    public static Item ascendedCore;
    public static Item legendaryBlade;
    public static Item ascendedBlade;
    public static Item legendaryBow;
    public static Item ascendedBow;
    public static Item legendaryStaff;
    public static Item ascendedStaff;
    public static Item legendaryPickaxe;
    public static Item legendaryAxe;
    public static Item legendaryShovel;
    public static Item legendaryShield;
    public static Item legendAmulet;
    public static Item legendTotem;
    public static Item legendElixir;
    public static Item legendaryHelmet;
    public static Item legendaryChest;
    public static Item legendaryLegs;
    public static Item legendaryBoots;
    public static Item ascendedHelmet;
    public static Item ascendedChest;
    public static Item ascendedLegs;
    public static Item ascendedBoots;
    public static Item legendOreItem;
    public static Item netherLegendOreItem;
    public static Item legendBrickItem;
    public static Item legendAltarItem;

    public static final CreativeTabs TAB = new CreativeTabs("legends") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(ascendedBlade != null ? ascendedBlade : (legendaryBlade != null ? legendaryBlade : Item.getItemById(276)));
        }
    };

    public static final ItemArmor.ArmorMaterial LEGENDARY_ARMOR =
            EnumHelper.addArmorMaterial("LEGENDARY", MODID + ":legendary", 45, new int[]{4, 9, 7, 4}, 30,
                    net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 3.0F);

    public static final ItemArmor.ArmorMaterial ASCENDED_ARMOR =
            EnumHelper.addArmorMaterial("ASCENDED", MODID + ":ascended", 60, new int[]{5, 10, 8, 5}, 35,
                    net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 4.0F);

    public static final Item.ToolMaterial LEGENDARY_TOOL =
            EnumHelper.addToolMaterial("LEGENDARY_TOOL", 4, 2500, 10.0F, 8.0F, 22);

    public static final Item.ToolMaterial ASCENDED_TOOL =
            EnumHelper.addToolMaterial("ASCENDED_TOOL", 4, 3500, 12.0F, 11.0F, 28);

    @SidedProxy(clientSide = "com.heiligg.legends.ClientProxy", serverSide = "com.heiligg.legends.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static LegendsMod instance;

    private static int packetId = 0;
    private static int entityId = 0;

    private static int nextPacketId() {
        return packetId++;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        LegendsConfig.load(event.getSuggestedConfigurationFile());

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        network.registerMessage(DashPacket.Handler.class, DashPacket.class, nextPacketId(), Side.SERVER);
        network.registerMessage(ShockwavePacket.Handler.class, ShockwavePacket.class, nextPacketId(), Side.SERVER);
        network.registerMessage(StaffBoltPacket.Handler.class, StaffBoltPacket.class, nextPacketId(), Side.SERVER);
        ModHeroes.preInit(nextPacketId());

        registerEntity("arcane_bolt", EntityArcaneBolt.class, 64, 1, true, -1, -1);
        registerEntity("legend_wraith", EntityLegendWraith.class, 80, 3, true, 0x2A6F8F, 0x7FDFFF);
        registerEntity("legend_guardian", EntityLegendGuardian.class, 96, 3, true, 0x1A2A6A, 0xC080FF);
        registerEntity("legend_knight", EntityLegendKnight.class, 80, 3, true, 0x4A5568, 0x9BD1FF);

        MinecraftForge.EVENT_BUS.register(new ArmorAbilityHandler());
        MinecraftForge.EVENT_BUS.register(new CombatHandler());
        MinecraftForge.EVENT_BUS.register(new LootHandler());
        MinecraftForge.EVENT_BUS.register(new TotemHandler());
        proxy.preInit();
        logger.info("Legends {} pre-initialized", VERSION);
    }

    private void registerEntity(String name, Class<? extends net.minecraft.entity.Entity> clazz,
                                int range, int update, boolean velocity, int eggPrimary, int eggSecondary) {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, name), clazz, name, entityId++, this, range, update, velocity);
        if (eggPrimary >= 0) {
            EntityRegistry.registerEgg(new ResourceLocation(MODID, name), eggPrimary, eggSecondary);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new LegendsWorldGen(), 0);

        for (Biome biome : ForgeRegistries.BIOMES) {
            if (biome != null && !biome.getSpawnableList(EnumCreatureType.MONSTER).isEmpty()) {
                EntityRegistry.addSpawn(EntityLegendWraith.class, LegendsConfig.wraithSpawnWeight, 1, 2, EnumCreatureType.MONSTER, biome);
            }
        }

        ModRecipes.register();
        proxy.init();
        logger.info("Legends {} initialized", VERSION);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        legendOre = new BlockLegendOre().setRegistryName(MODID, "legend_ore").setUnlocalizedName(MODID + ".legend_ore");
        netherLegendOre = new BlockNetherLegendOre().setRegistryName(MODID, "nether_legend_ore").setUnlocalizedName(MODID + ".nether_legend_ore");
        legendBrick = new BlockLegendBrick().setRegistryName(MODID, "legend_brick").setUnlocalizedName(MODID + ".legend_brick");
        legendAltar = new BlockLegendAltar().setRegistryName(MODID, "legend_altar").setUnlocalizedName(MODID + ".legend_altar");
        event.getRegistry().registerAll(legendOre, netherLegendOre, legendBrick, legendAltar);
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        legendStrike = new EnchantmentLegendStrike();
        event.getRegistry().register(legendStrike);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        legendEssence = item(new ItemLegendEssence(), "legend_essence");
        legendFragment = item(new ItemLegendFragment(), "legend_fragment");
        ascendedCore = item(new ItemAscendedCore(), "ascended_core");
        legendaryBlade = item(new ItemLegendaryBlade(LEGENDARY_TOOL), "legendary_blade");
        ascendedBlade = item(new ItemAscendedBlade(ASCENDED_TOOL), "ascended_blade");
        legendaryBow = item(new ItemLegendaryBow(), "legendary_bow");
        ascendedBow = item(new ItemAscendedBow(), "ascended_bow");
        legendaryStaff = item(new ItemLegendaryStaff(), "legendary_staff");
        ascendedStaff = item(new ItemAscendedStaff(), "ascended_staff");
        legendaryPickaxe = item(new ItemLegendaryPickaxe(LEGENDARY_TOOL), "legendary_pickaxe");
        legendaryAxe = item(new ItemLegendaryAxe(LEGENDARY_TOOL), "legendary_axe");
        legendaryShovel = item(new ItemLegendaryShovel(LEGENDARY_TOOL), "legendary_shovel");
        legendaryShield = item(new ItemLegendaryShield(), "legendary_shield");
        legendAmulet = item(new ItemLegendAmulet(), "legend_amulet");
        legendTotem = item(new ItemLegendTotem(), "legend_totem");
        legendElixir = item(new ItemLegendElixir(), "legend_elixir");

        legendaryHelmet = item(new ItemLegendaryArmor(LEGENDARY_ARMOR, 1, EntityEquipmentSlot.HEAD), "legendary_helmet");
        legendaryChest = item(new ItemLegendaryArmor(LEGENDARY_ARMOR, 1, EntityEquipmentSlot.CHEST), "legendary_chest");
        legendaryLegs = item(new ItemLegendaryArmor(LEGENDARY_ARMOR, 2, EntityEquipmentSlot.LEGS), "legendary_legs");
        legendaryBoots = item(new ItemLegendaryArmor(LEGENDARY_ARMOR, 1, EntityEquipmentSlot.FEET), "legendary_boots");

        ascendedHelmet = item(new ItemAscendedArmor(ASCENDED_ARMOR, 1, EntityEquipmentSlot.HEAD), "ascended_helmet");
        ascendedChest = item(new ItemAscendedArmor(ASCENDED_ARMOR, 1, EntityEquipmentSlot.CHEST), "ascended_chest");
        ascendedLegs = item(new ItemAscendedArmor(ASCENDED_ARMOR, 2, EntityEquipmentSlot.LEGS), "ascended_legs");
        ascendedBoots = item(new ItemAscendedArmor(ASCENDED_ARMOR, 1, EntityEquipmentSlot.FEET), "ascended_boots");

        legendOreItem = new ItemBlock(legendOre).setRegistryName(legendOre.getRegistryName()).setCreativeTab(TAB);
        netherLegendOreItem = new ItemBlock(netherLegendOre).setRegistryName(netherLegendOre.getRegistryName()).setCreativeTab(TAB);
        legendBrickItem = new ItemBlock(legendBrick).setRegistryName(legendBrick.getRegistryName()).setCreativeTab(TAB);
        legendAltarItem = new ItemBlock(legendAltar).setRegistryName(legendAltar.getRegistryName()).setCreativeTab(TAB);

        event.getRegistry().registerAll(
                legendEssence, legendFragment, ascendedCore,
                legendaryBlade, ascendedBlade, legendaryBow, ascendedBow, legendaryStaff, ascendedStaff,
                legendaryPickaxe, legendaryAxe, legendaryShovel, legendaryShield,
                legendAmulet, legendTotem, legendElixir,
                legendaryHelmet, legendaryChest, legendaryLegs, legendaryBoots,
                ascendedHelmet, ascendedChest, ascendedLegs, ascendedBoots,
                legendOreItem, netherLegendOreItem, legendBrickItem, legendAltarItem
        );
        ModHeroes.registerItems(event.getRegistry());
    }

    private static Item item(Item item, String name) {
        return item.setRegistryName(MODID, name).setUnlocalizedName(MODID + "." + name).setCreativeTab(TAB);
    }
}
