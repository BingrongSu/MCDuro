package net.robert.mcduro.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.item.custom.RingAdding;
import net.robert.mcduro.item.custom.WuHunAdding;

public class ModItems {
    public static final Item RAW_SHEN_SILVER = registerItems("raw_shen_silver", new Item(new FabricItemSettings()));
    public static final Item SHEN_SILVER_INGOT = registerItems("shen_silver_ingot", new Item(new FabricItemSettings()));
    public static final Item SHEN_SILVER_NUGGET = registerItems("shen_silver_nugget", new Item(new FabricItemSettings()));

    public static final Item SOUL_RING_TEN = registerItems("soul_ring_ten", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 50));          // 十年
    public static final Item SOUL_RING_HUD = registerItems("soul_ring_hud", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 500));         // 百年
    public static final Item SOUL_RING_THD = registerItems("soul_ring_thd", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 5000));        // 千年
    public static final Item SOUL_RING_TTD = registerItems("soul_ring_ttd", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 50000));       // 万年
    public static final Item SOUL_RING_HTD = registerItems("soul_ring_htd", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 500000));      // 十万年

    public static final Item WU_HUN_FENG_HUANG = registerItems("wu_hun_feng_huang", new WuHunAdding(new FabricItemSettings().food(ModFoodComponents.WUHUN_ADDING), "fengHuang"));


    private static Item registerItems(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(MCDuro.MOD_ID, name), item);
    }

    public static void registerModItems(){
        MCDuro.LOGGER.info("Registering Mod Items");
    }
}
