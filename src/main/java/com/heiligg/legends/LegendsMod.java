package com.heiligg.legends;

import com.heiligg.legends.block.BlockLegendOre;
import com.heiligg.legends.config.LegendsConfig;
import com.heiligg.legends.entity.EntityArcaneBolt;
import com.heiligg.legends.handler.ArmorAbilityHandler;
import com.heiligg.legends.handler.LootHandler;
import com.heiligg.legends.item.ItemLegendAmulet;
import com.heiligg.legends.item.ItemLegendEssence;
import com.heiligg.legends.item.ItemLegendFragment;
import com.heiligg.legends.item.ItemLegendaryArmor;
import com.heiligg.legends.item.ItemLegendaryAxe;
import com.heiligg.legends.item.ItemLegendaryBlade;
import com.heiligg.legends.item.ItemLegendaryBow;
import com.heiligg.legends.item.ItemLegendaryPickaxe;
import com.heiligg.legends.item.ItemLegendaryStaff;
import com.heiligg.legends.network.DashPacket;
import com.heiligg.legends.network.ShockwavePacket;
import com.heiligg.legends.network.StaffBoltPacket;
import com.heiligg.legends.world.LegendsWorldGen;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = LegendsMod.MODID, name = LegendsMod.NAME, version = LegendsMod.VERSION)
@Mod.EventBusSubscriber(modid = LegendsMod.MODID)
public class LegendsMod {

    public static final String MODID = "legends";
    public static final String NAME = "Legends";
    public static final String VERSION = "1.3.0";

    public static Logger logger;
    public static SimpleNetworkWrapper network;

    public static Block legendOre;

    public static Item legendEssence;
    public static Item legendFragment;
    public static Item legendaryBlade;
    public static Item legendaryBow;
    public static Item legendaryStaff;
    public static Item legendaryPickaxe;
    public static Item legendaryAxe;
    public static Item legendAmulet;
    public static Item legendaryHelmet;
    public static Item legendaryChest;
    public static Item legendaryLegs;
    public static Item legendaryBoots;
    public static Item legendOreItem;

    public static final CreativeTabs TAB = new CreativeTabs("legends") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(legendaryBlade != null ? legendaryBlade : Item.getItemById(276));
        }
    };

    public static final ItemArmor.ArmorMaterial LEGENDARY_ARMOR =
            EnumHelper.addArmorMaterial(
                    "LEGENDARY",
                    MODID + ":legendary",
                    45,
                    new int[]{4, 9, 7, 4},
                    30,
                    net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
                    3.0F
            );

    public static final Item.ToolMaterial LEGENDARY_TOOL =
            EnumHelper.addToolMaterial("LEGENDARY_TOOL", 4, 2500, 10.0F, 8.0F, 22);

    @SidedProxy(
            clientSide = "com.heiligg.legends.ClientProxy",
            serverSide = "com.heiligg.legends.CommonProxy"
    )
    public static CommonProxy proxy;

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

        EntityRegistry.registerModEntity(
                new ResourceLocation(MODID, "arcane_bolt"),
                EntityArcaneBolt.class,
                "arcane_bolt",
                entityId++,
                this,
                64,
                1,
                true
        );

        MinecraftForge.EVENT_BUS.register(new ArmorAbilityHandler());
        MinecraftForge.EVENT_BUS.register(new LootHandler());
        proxy.preInit();
        logger.info("Legends {} pre-initialized", VERSION);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new LegendsWorldGen(), 0);
        proxy.init();
        logger.info("Legends {} initialized", VERSION);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        legendOre = new BlockLegendOre()
                .setRegistryName(MODID, "legend_ore")
                .setUnlocalizedName(MODID + ".legend_ore");
        event.getRegistry().register(legendOre);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        legendEssence = new ItemLegendEssence()
                .setRegistryName(MODID, "legend_essence")
                .setUnlocalizedName(MODID + ".legend_essence")
                .setCreativeTab(TAB);

        legendFragment = new ItemLegendFragment()
                .setRegistryName(MODID, "legend_fragment")
                .setUnlocalizedName(MODID + ".legend_fragment")
                .setCreativeTab(TAB);

        legendaryBlade = new ItemLegendaryBlade(LEGENDARY_TOOL)
                .setRegistryName(MODID, "legendary_blade")
                .setUnlocalizedName(MODID + ".legendary_blade")
                .setCreativeTab(TAB);

        legendaryBow = new ItemLegendaryBow()
                .setRegistryName(MODID, "legendary_bow")
                .setUnlocalizedName(MODID + ".legendary_bow")
                .setCreativeTab(TAB);

        legendaryStaff = new ItemLegendaryStaff()
                .setRegistryName(MODID, "legendary_staff")
                .setUnlocalizedName(MODID + ".legendary_staff")
                .setCreativeTab(TAB);

        legendaryPickaxe = new ItemLegendaryPickaxe(LEGENDARY_TOOL)
                .setRegistryName(MODID, "legendary_pickaxe")
                .setUnlocalizedName(MODID + ".legendary_pickaxe")
                .setCreativeTab(TAB);

        legendaryAxe = new ItemLegendaryAxe(LEGENDARY_TOOL)
                .setRegistryName(MODID, "legendary_axe")
                .setUnlocalizedName(MODID + ".legendary_axe")
                .setCreativeTab(TAB);

        legendAmulet = new ItemLegendAmulet()
                .setRegistryName(MODID, "legend_amulet")
                .setUnlocalizedName(MODID + ".legend_amulet")
                .setCreativeTab(TAB);

        legendaryHelmet = new ItemLegendaryArmor(LEGENDARY_ARMOR, 1, EntityEquipmentSlot.HEAD)
                .setRegistryName(MODID, "legendary_helmet")
                .setUnlocalizedName(MODID + ".legendary_helmet")
                .setCreativeTab(TAB);

        legendaryChest = new ItemLegendaryArmor(LEGENDARY_ARMOR, 1, EntityEquipmentSlot.CHEST)
                .setRegistryName(MODID, "legendary_chest")
                .setUnlocalizedName(MODID + ".legendary_chest")
                .setCreativeTab(TAB);

        legendaryLegs = new ItemLegendaryArmor(LEGENDARY_ARMOR, 2, EntityEquipmentSlot.LEGS)
                .setRegistryName(MODID, "legendary_legs")
                .setUnlocalizedName(MODID + ".legendary_legs")
                .setCreativeTab(TAB);

        legendaryBoots = new ItemLegendaryArmor(LEGENDARY_ARMOR, 1, EntityEquipmentSlot.FEET)
                .setRegistryName(MODID, "legendary_boots")
                .setUnlocalizedName(MODID + ".legendary_boots")
                .setCreativeTab(TAB);

        legendOreItem = new ItemBlock(legendOre)
                .setRegistryName(legendOre.getRegistryName())
                .setCreativeTab(TAB);

        event.getRegistry().registerAll(
                legendEssence,
                legendFragment,
                legendaryBlade,
                legendaryBow,
                legendaryStaff,
                legendaryPickaxe,
                legendaryAxe,
                legendAmulet,
                legendaryHelmet,
                legendaryChest,
                legendaryLegs,
                legendaryBoots,
                legendOreItem
        );
    }
}
