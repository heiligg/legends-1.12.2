package com.heiligg.legends.hero;

import com.heiligg.legends.LegendsMod;
import com.heiligg.legends.item.ItemVibraniumShield;
import com.heiligg.legends.network.HeroAbilityPacket;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class ModHeroes {

    public static final Map<IronSuitType, ItemArmor.ArmorMaterial> IRON_MATERIALS =
            new EnumMap<IronSuitType, ItemArmor.ArmorMaterial>(IronSuitType.class);
    public static final Map<IronSuitType, Item> IRON_HELMETS = new EnumMap<IronSuitType, Item>(IronSuitType.class);
    public static final Map<IronSuitType, Item> IRON_CHESTS = new EnumMap<IronSuitType, Item>(IronSuitType.class);
    public static final Map<IronSuitType, Item> IRON_LEGS = new EnumMap<IronSuitType, Item>(IronSuitType.class);
    public static final Map<IronSuitType, Item> IRON_BOOTS = new EnumMap<IronSuitType, Item>(IronSuitType.class);

    public static ItemArmor.ArmorMaterial SPIDER_MAN_MATERIAL;
    public static ItemArmor.ArmorMaterial FLASH_MATERIAL;
    public static ItemArmor.ArmorMaterial CAP_MATERIAL;

    /** Legacy Mark III aliases. */
    public static Item ironManHelmet, ironManChest, ironManLegs, ironManBoots;
    public static Item spiderManHelmet, spiderManChest, spiderManLegs, spiderManBoots;
    public static Item flashHelmet, flashChest, flashLegs, flashBoots;
    public static Item capHelmet, capChest, capLegs, capBoots;
    public static Item vibraniumShield;

    private static final List<Item> ALL = new ArrayList<Item>();

    private ModHeroes() {
    }

    public static void preInit(int packetId) {
        LegendsMod.network.registerMessage(HeroAbilityPacket.Handler.class, HeroAbilityPacket.class, packetId, Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new HeroAbilityHandler());
    }

    public static void registerItems(IForgeRegistry<Item> registry) {
        if (SPIDER_MAN_MATERIAL == null) {
            for (IronSuitType suit : IronSuitType.values()) {
                float toughness = 1.5F;
                int durability = 40;
                int[] defense = new int[]{3, 8, 6, 3};
                if (suit == IronSuitType.MARK_I) {
                    durability = 28;
                    defense = new int[]{2, 6, 5, 2};
                    toughness = 0.5F;
                } else if (suit == IronSuitType.MARK_V || suit == IronSuitType.STEALTH) {
                    durability = 34;
                    defense = new int[]{2, 7, 5, 2};
                    toughness = 1.0F;
                } else if (suit == IronSuitType.WAR_MACHINE || suit == IronSuitType.HULKBUSTER) {
                    durability = 56;
                    defense = new int[]{3, 9, 7, 3};
                    toughness = 3.0F;
                } else if (suit == IronSuitType.MARK_L) {
                    durability = 60;
                    defense = new int[]{3, 9, 7, 3};
                    toughness = 3.5F;
                }
                IRON_MATERIALS.put(suit, EnumHelper.addArmorMaterial(
                        "IRON_SUIT_" + suit.name(),
                        LegendsMod.MODID + ":" + suit.textureName,
                        durability,
                        defense,
                        20,
                        net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                        toughness
                ));
            }

            SPIDER_MAN_MATERIAL = EnumHelper.addArmorMaterial("SPIDER_MAN_HERO", LegendsMod.MODID + ":spider_man",
                    40, new int[]{2, 7, 6, 2}, 20, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.5F);
            FLASH_MATERIAL = EnumHelper.addArmorMaterial("FLASH_HERO", LegendsMod.MODID + ":flash",
                    38, new int[]{2, 6, 5, 2}, 24, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F);
            CAP_MATERIAL = EnumHelper.addArmorMaterial("CAPTAIN_AMERICA_HERO", LegendsMod.MODID + ":captain_america",
                    50, new int[]{3, 8, 6, 3}, 18, net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F);
        }

        ALL.clear();

        for (IronSuitType suit : IronSuitType.values()) {
            ItemArmor.ArmorMaterial mat = IRON_MATERIALS.get(suit);
            Item helmet = hero(new ItemIronSuitArmor(mat, 1, EntityEquipmentSlot.HEAD, suit), suit.pieceName("helmet"));
            Item chest = hero(new ItemIronSuitArmor(mat, 1, EntityEquipmentSlot.CHEST, suit), suit.pieceName("chest"));
            Item legs = hero(new ItemIronSuitArmor(mat, 2, EntityEquipmentSlot.LEGS, suit), suit.pieceName("legs"));
            Item boots = hero(new ItemIronSuitArmor(mat, 1, EntityEquipmentSlot.FEET, suit), suit.pieceName("boots"));
            IRON_HELMETS.put(suit, helmet);
            IRON_CHESTS.put(suit, chest);
            IRON_LEGS.put(suit, legs);
            IRON_BOOTS.put(suit, boots);
        }

        ironManHelmet = IRON_HELMETS.get(IronSuitType.MARK_III);
        ironManChest = IRON_CHESTS.get(IronSuitType.MARK_III);
        ironManLegs = IRON_LEGS.get(IronSuitType.MARK_III);
        ironManBoots = IRON_BOOTS.get(IronSuitType.MARK_III);

        spiderManHelmet = hero(new ItemHeroArmor(SPIDER_MAN_MATERIAL, 1, EntityEquipmentSlot.HEAD, HeroType.SPIDER_MAN), "spider_man_helmet");
        spiderManChest = hero(new ItemHeroArmor(SPIDER_MAN_MATERIAL, 1, EntityEquipmentSlot.CHEST, HeroType.SPIDER_MAN), "spider_man_chest");
        spiderManLegs = hero(new ItemHeroArmor(SPIDER_MAN_MATERIAL, 2, EntityEquipmentSlot.LEGS, HeroType.SPIDER_MAN), "spider_man_legs");
        spiderManBoots = hero(new ItemHeroArmor(SPIDER_MAN_MATERIAL, 1, EntityEquipmentSlot.FEET, HeroType.SPIDER_MAN), "spider_man_boots");

        flashHelmet = hero(new ItemHeroArmor(FLASH_MATERIAL, 1, EntityEquipmentSlot.HEAD, HeroType.FLASH), "flash_helmet");
        flashChest = hero(new ItemHeroArmor(FLASH_MATERIAL, 1, EntityEquipmentSlot.CHEST, HeroType.FLASH), "flash_chest");
        flashLegs = hero(new ItemHeroArmor(FLASH_MATERIAL, 2, EntityEquipmentSlot.LEGS, HeroType.FLASH), "flash_legs");
        flashBoots = hero(new ItemHeroArmor(FLASH_MATERIAL, 1, EntityEquipmentSlot.FEET, HeroType.FLASH), "flash_boots");

        capHelmet = hero(new ItemHeroArmor(CAP_MATERIAL, 1, EntityEquipmentSlot.HEAD, HeroType.CAPTAIN_AMERICA), "captain_america_helmet");
        capChest = hero(new ItemHeroArmor(CAP_MATERIAL, 1, EntityEquipmentSlot.CHEST, HeroType.CAPTAIN_AMERICA), "captain_america_chest");
        capLegs = hero(new ItemHeroArmor(CAP_MATERIAL, 2, EntityEquipmentSlot.LEGS, HeroType.CAPTAIN_AMERICA), "captain_america_legs");
        capBoots = hero(new ItemHeroArmor(CAP_MATERIAL, 1, EntityEquipmentSlot.FEET, HeroType.CAPTAIN_AMERICA), "captain_america_boots");

        vibraniumShield = hero(new ItemVibraniumShield(), "vibranium_shield");

        registry.registerAll(ALL.toArray(new Item[0]));
    }

    public static Item[] allItems() {
        return ALL.toArray(new Item[0]);
    }

    private static Item hero(Item item, String name) {
        item.setRegistryName(LegendsMod.MODID, name)
                .setUnlocalizedName(LegendsMod.MODID + "." + name)
                .setCreativeTab(LegendsMod.TAB);
        ALL.add(item);
        return item;
    }
}
