package net.robert.mcduro.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.item.ModItems;

public class ModModelsProvider extends FabricModelProvider {
    public ModModelsProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SHEN_SILVER_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_SHEN_SILVER_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.NETHER_SHEN_SILVER_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SHEN_SILVER_BLOCK);

        blockStateModelGenerator.registerSimpleState(ModBlocks.JX_BALL);
        blockStateModelGenerator.registerSimpleState(ModBlocks.JUE_XING_TAI);
        blockStateModelGenerator.registerSimpleState(ModBlocks.SMITH_SMELTING_CHIMNEY);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.RAW_SHEN_SILVER, Models.GENERATED);
        itemModelGenerator.register(ModItems.SHEN_SILVER_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SHEN_SILVER_NUGGET, Models.GENERATED);

        itemModelGenerator.register(ModItems.WU_HUN_FENG_HUANG, Models.GENERATED);
    }

}
