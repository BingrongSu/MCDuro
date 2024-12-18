package net.robert.mcduro.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.robert.mcduro.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.SHEN_SILVER_BLOCK)
                .add(ModBlocks.SHEN_SILVER_ORE)
                .add(ModBlocks.NETHER_SHEN_SILVER_ORE)
                .add(ModBlocks.DEEPSLATE_SHEN_SILVER_ORE)
                .add(ModBlocks.JUE_XING_TAI)
                .add(ModBlocks.SMITH_SMELTING)
                .add(ModBlocks.SMITH_SMELTING_CHIMNEY);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SHEN_SILVER_BLOCK)
                .add(ModBlocks.SHEN_SILVER_ORE)
                .add(ModBlocks.DEEPSLATE_SHEN_SILVER_ORE)
                .add(ModBlocks.NETHER_SHEN_SILVER_ORE);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL);
    }
}
