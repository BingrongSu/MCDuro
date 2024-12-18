package net.robert.mcduro.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.item.ModItems;

public class ModLootTablesProvider extends FabricBlockLootTableProvider {
    public ModLootTablesProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.SHEN_SILVER_ORE,
                customOreDrops(ModBlocks.SHEN_SILVER_ORE, ModItems.RAW_SHEN_SILVER, 1f,  1f));
        addDrop(ModBlocks.DEEPSLATE_SHEN_SILVER_ORE,
                customOreDrops(ModBlocks.DEEPSLATE_SHEN_SILVER_ORE, ModItems.RAW_SHEN_SILVER, 1f,  1f));
        addDrop(ModBlocks.NETHER_SHEN_SILVER_ORE,
                customOreDrops(ModBlocks.NETHER_SHEN_SILVER_ORE, ModItems.SHEN_SILVER_NUGGET, 2f,  5f));

        addDrop(ModBlocks.SHEN_SILVER_BLOCK);
        addDrop(ModBlocks.JUE_XING_TAI);
        addDrop(ModBlocks.JX_BALL);
        addDrop(ModBlocks.SMITH_SMELTING_CHIMNEY);
        addDrop(ModBlocks.SMITH_SMELTING);
      }

    public LootTable.Builder customOreDrops(Block drop, Item item, float min, float max) {
        return dropsWithSilkTouch(
                drop,
                (LootPoolEntry.Builder<?>)this.applyExplosionDecay(
                        drop,
                        ItemEntry.builder(item)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(min, max)))
                                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
                )
        );
    }
}
