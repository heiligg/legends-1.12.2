package com.heiligg.legends.handler;

import com.heiligg.legends.LegendsMod;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LootHandler {

    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName() == null) {
            return;
        }

        boolean dungeonLike = event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)
                || event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE)
                || event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)
                || event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR)
                || event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CROSSING)
                || event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT);

        if (!dungeonLike || LegendsMod.legendEssence == null) {
            return;
        }

        LootPool pool = event.getTable().getPool("main");
        if (pool == null) {
            return;
        }

        pool.addEntry(new LootEntryItem(
                LegendsMod.legendEssence,
                8,
                1,
                new LootFunction[]{new SetCount(new LootCondition[0], new RandomValueRange(1, 2))},
                new LootCondition[0],
                LegendsMod.MODID + ":legend_essence_loot"
        ));
    }
}
