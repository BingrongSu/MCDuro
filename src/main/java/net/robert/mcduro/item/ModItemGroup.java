package net.robert.mcduro.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.block.ModBlocks;

public class ModItemGroup {
    public static final ItemGroup TEMPLATE_GROUP1 = Registry.register(Registries.ITEM_GROUP,
            new Identifier(MCDuro.MOD_ID, "mcduro_group1"),
            FabricItemGroup.builder().displayName(Text.translatable("itemGroup.mcduro_group1"))
                    .icon(() -> new ItemStack(ModBlocks.DEEPSLATE_SHEN_SILVER_ORE)).entries(((displayContext, entries) -> {

                        entries.add(ModBlocks.SHEN_SILVER_ORE);
                        entries.add(ModBlocks.DEEPSLATE_SHEN_SILVER_ORE);
                        entries.add(ModBlocks.NETHER_SHEN_SILVER_ORE);
                        entries.add(ModBlocks.JUE_XING_TAI);
                        entries.add(ModBlocks.JX_BALL);
                        entries.add(ModBlocks.SMITH_SMELTING);
                        entries.add(ModBlocks.SMITH_SMELTING_CHIMNEY);
                        entries.add(ModBlocks.ALCHEMY_FURNACE);

                        entries.add(ModBlocks.SHEN_SILVER_BLOCK);
                        entries.add(ModItems.SHEN_SILVER_INGOT);
                        entries.add(ModItems.SHEN_SILVER_NUGGET);
                        entries.add(ModItems.RAW_SHEN_SILVER);

                        entries.add(ModItems.SOUL_RING_TEN);
                        entries.add(ModItems.SOUL_RING_HUD);
                        entries.add(ModItems.SOUL_RING_THD);
                        entries.add(ModItems.SOUL_RING_TTD);
                        entries.add(ModItems.SOUL_RING_HTD);
                        entries.add(ModItems.WU_HUN_FENG_HUANG);
                        entries.add(ModItems.WU_HUN_LIU_LI);

                        entries.add(ModItems.HUNLI_PILL_L1);
                        entries.add(ModItems.HUNLI_PILL_L2);
                        entries.add(ModItems.HUNLI_PILL_L3);
                        entries.add(ModItems.HUNLI_PILL_L4);
                        entries.add(ModItems.HUNLI_PILL_L5);
                        entries.add(ModItems.HUNLI_PILL_L6);
                        entries.add(ModItems.HUNLI_PILL_L7);
                        entries.add(ModItems.MAX_HUNLI_PILL_L1);
                        entries.add(ModItems.MAX_HUNLI_PILL_L2);
                        entries.add(ModItems.MAX_HUNLI_PILL_L3);
                        entries.add(ModItems.MAX_HUNLI_PILL_L4);
                        entries.add(ModItems.MAX_HUNLI_PILL_L5);
                        entries.add(ModItems.MAX_HUNLI_PILL_L6);
                        entries.add(ModItems.MAX_HUNLI_PILL_L7);
                        // 添加物品到物品栏
                    })).build());

    public static void registerModItemGroup(){
        MCDuro.LOGGER.info("Registering Mod Item Group");
    }
}

