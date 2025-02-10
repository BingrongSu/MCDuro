package net.robert.mcduro.events;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

public class ModEvents {
    public static Identifier SYNC_SHOWED_YEARS =  new Identifier(MCDuro.MOD_ID, "events.sync_showed_years");
    public static Identifier INIT_SYNC = new Identifier(MCDuro.MOD_ID, "events.initial_sync");
    public static Identifier SET_HUN_LI = new Identifier(MCDuro.MOD_ID, "events.set_hun_li");
    public static Identifier SET_HUN_LI_LEVEL = new Identifier(MCDuro.MOD_ID, "events.set_hun_li_level");
    public static Identifier SET_MAX_HUN_LI = new Identifier(MCDuro.MOD_ID, "events.set_max_hun_li");
    public static Identifier SET_WU_HUN = new Identifier(MCDuro.MOD_ID, "events.set_wu_hun");
    public static Identifier SET_OPENED_WU_HUN = new Identifier(MCDuro.MOD_ID, "events.set_opened_wu_hun");
    public static Identifier OPEN_WU_HUN = new Identifier(MCDuro.MOD_ID, "events.open_wu_hun");
    public static Identifier GET_MOB_YEAR = new Identifier(MCDuro.MOD_ID, "events.get_mob_year");
    public static Identifier USE_SOUL_SKILL = new Identifier(MCDuro.MOD_ID, "events.use_soul_skill");

    private static final Identifier COAL_ORE_LOOT_TABLE_ID = Blocks.COAL_ORE.getLootTableId();
    private static final Identifier ZOMBIE_LOOT_TABLE_ID = new Identifier("minecraft", "entities/zombie");

    public static void registerModEvents() {
        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            if (!world.isClient) {
                if (world.getBlockState(blockPos).isOf(Blocks.GRASS_BLOCK)) {
                    PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                }
            }
            return ActionResult.PASS;
        });

        LootTableEvents.MODIFY.register(((resourceManager, lootManager, identifier, builder, lootTableSource) -> {
            if (COAL_ORE_LOOT_TABLE_ID.equals(identifier)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(2))
                        .with(ItemEntry.builder(Items.EGG));
                builder.pool(poolBuilder);
            }
            if (ZOMBIE_LOOT_TABLE_ID.equals(identifier)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(BinomialLootNumberProvider.create(3, 0.6f))
                        .with(ItemEntry.builder(Items.DIAMOND));
                builder.pool(poolBuilder);
            }
        }));


    }
}
