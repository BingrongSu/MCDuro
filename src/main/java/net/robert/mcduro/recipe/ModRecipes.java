package net.robert.mcduro.recipe;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;

public class ModRecipes {
    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(MCDuro.MOD_ID, SmithSmeltingRecipe.Serializer.ID),
                SmithSmeltingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(MCDuro.MOD_ID, SmithSmeltingRecipe.Type.ID),
                SmithSmeltingRecipe.Type.INSTANCE);
    }
}
