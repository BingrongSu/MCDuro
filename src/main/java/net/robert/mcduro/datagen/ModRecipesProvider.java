package net.robert.mcduro.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.item.ModItems;

import java.util.List;

public class ModRecipesProvider extends FabricRecipeProvider {

    private static final List<ItemConvertible> shen_silver_list = List.of(ModItems.RAW_SHEN_SILVER,
                                                                          ModBlocks.SHEN_SILVER_ORE);

    public ModRecipesProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter consumer) {
        offerReversibleCompactingRecipes(consumer, RecipeCategory.BUILDING_BLOCKS, ModItems.SHEN_SILVER_INGOT,
                RecipeCategory.BUILDING_BLOCKS, ModBlocks.SHEN_SILVER_BLOCK);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SHEN_SILVER_INGOT, 1)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .input('#', ModItems.SHEN_SILVER_NUGGET)
                .criterion("has_item", conditionsFromItem(ModItems.SHEN_SILVER_INGOT))
                .offerTo(consumer, new Identifier("recipe.shen_silver_ingot_from_nugget"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SHEN_SILVER_NUGGET, 9)
                .input(ModItems.SHEN_SILVER_INGOT)
                .criterion("has_item", conditionsFromItem(ModItems.SHEN_SILVER_INGOT))
                .offerTo(consumer, new Identifier("recipe.shen_silver_nugget_from_ingot"));

        offerSmelting(consumer, shen_silver_list, RecipeCategory.MISC, ModItems.SHEN_SILVER_INGOT,
                0.4f, 600, "recipe.shen_silver_ingot_from_smelting");
        offerBlasting(consumer, shen_silver_list, RecipeCategory.MISC, ModItems.SHEN_SILVER_INGOT,
                0.4f, 300, "recipe.shen_silver_ingot_from_blasting");

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.JUE_XING_TAI, 1)
                .pattern("@#@")
                .pattern("#=#")
                .pattern("@#@")
                .input('#', Ingredient.fromTag(ItemTags.PLANKS))
                .input('@', Blocks.STONE)
                .input('=', Blocks.DEEPSLATE)
                .criterion("has_item", conditionsFromTag(ItemTags.PLANKS))
                .offerTo(consumer, new Identifier("recipe.jue_xing_tai"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.JX_BALL, 1)
                .pattern(" # ")
                .pattern("#=#")
                .pattern(" # ")
                .input('#', Blocks.GLASS)
                .input('=', Items.LAPIS_LAZULI)
                .criterion("has_item", conditionsFromItem(ModBlocks.JUE_XING_TAI))
                .offerTo(consumer, new Identifier("recipe.jx_ball"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMITH_SMELTING, 1)
                .pattern("###")
                .pattern("#=#")
                .pattern("#@#")
                .input('#', Blocks.DEEPSLATE)
                .input('=', Blocks.BLAST_FURNACE)
                .input('@', Blocks.IRON_BARS)
                .criterion("has_item", conditionsFromItem(Blocks.BLAST_FURNACE))
                .offerTo(consumer, new Identifier("recipe.smith_smelting"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.SMITH_SMELTING_CHIMNEY, 1)
                .pattern(" # ")
                .pattern("#=#")
                .pattern(" # ")
                .input('#', Blocks.DEEPSLATE)
                .input('=', Items.IRON_INGOT)
                .criterion("has_item", conditionsFromItem(ModBlocks.SMITH_SMELTING))
                .offerTo(consumer, new Identifier("recipe.smith_smelting_chimney"));
    }
}
