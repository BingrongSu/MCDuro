package net.robert.mcduro.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.item.custom.HunLiAdding;
import net.robert.mcduro.item.custom.MaxHunLiAdding;
import net.robert.mcduro.item.custom.RingAdding;
import net.robert.mcduro.item.custom.WuHunAdding;

public class ModItems {
    public static final Item RAW_SHEN_SILVER = registerItems("raw_shen_silver", new Item(new FabricItemSettings()));
    public static final Item SHEN_SILVER_INGOT = registerItems("shen_silver_ingot", new Item(new FabricItemSettings()));
    public static final Item SHEN_SILVER_NUGGET = registerItems("shen_silver_nugget", new Item(new FabricItemSettings()));

    public static final Item SOUL_RING_TEN = registerItems("soul_ring_ten", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 1));           // 十年
    public static final Item SOUL_RING_HUD = registerItems("soul_ring_hud", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 2));           // 百年
    public static final Item SOUL_RING_THD = registerItems("soul_ring_thd", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 3));           // 千年
    public static final Item SOUL_RING_TTD = registerItems("soul_ring_ttd", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 4));           // 万年
    public static final Item SOUL_RING_HTD = registerItems("soul_ring_htd", new RingAdding(new FabricItemSettings().food(ModFoodComponents.RING_ADDING), 5));           // 十万年

    public static final Item WU_HUN_FENG_HUANG = registerItems("wu_hun_feng_huang", new WuHunAdding(new FabricItemSettings().food(ModFoodComponents.WUHUN_ADDING), "fengHuang"));
//    public static final Item WU_HUN_XIANG_CHANG = registerItems("wu_hun_xiang_chang", new WuHunAdding(new FabricItemSettings().food(ModFoodComponents.WUHUN_ADDING), "xiangCHang"));
    public static final Item WU_HUN_LIU_LI = registerItems("wu_hun_liu_li", new WuHunAdding(new FabricItemSettings().food(ModFoodComponents.WUHUN_ADDING), "liuLi"));

    public static final Item HUNLI_PILL_L1 = registerItems("hunli_pill_l1", new HunLiAdding(new FabricItemSettings().food(ModFoodComponents.HUNLI_ADDING), 10));
    public static final Item HUNLI_PILL_L2 = registerItems("hunli_pill_l2", new HunLiAdding(new FabricItemSettings().food(ModFoodComponents.HUNLI_ADDING), 100));
    public static final Item HUNLI_PILL_L3 = registerItems("hunli_pill_l3", new HunLiAdding(new FabricItemSettings().food(ModFoodComponents.HUNLI_ADDING), 1000));
    public static final Item HUNLI_PILL_L4 = registerItems("hunli_pill_l4", new HunLiAdding(new FabricItemSettings().food(ModFoodComponents.HUNLI_ADDING), 10000));
    public static final Item HUNLI_PILL_L5 = registerItems("hunli_pill_l5", new HunLiAdding(new FabricItemSettings().food(ModFoodComponents.HUNLI_ADDING), 100000));
    public static final Item HUNLI_PILL_L6 = registerItems("hunli_pill_l6", new HunLiAdding(new FabricItemSettings().food(ModFoodComponents.HUNLI_ADDING), 1000000));
    public static final Item HUNLI_PILL_L7 = registerItems("hunli_pill_l7", new HunLiAdding(new FabricItemSettings().food(ModFoodComponents.HUNLI_ADDING), 10000000));

    public static final Item MAX_HUNLI_PILL_L1 = registerItems("max_hunli_pill_l1", new MaxHunLiAdding(new FabricItemSettings().food(ModFoodComponents.MAX_HUNLI_ADDING), 10));
    public static final Item MAX_HUNLI_PILL_L2 = registerItems("max_hunli_pill_l2", new MaxHunLiAdding(new FabricItemSettings().food(ModFoodComponents.MAX_HUNLI_ADDING), 100));
    public static final Item MAX_HUNLI_PILL_L3 = registerItems("max_hunli_pill_l3", new MaxHunLiAdding(new FabricItemSettings().food(ModFoodComponents.MAX_HUNLI_ADDING), 1000));
    public static final Item MAX_HUNLI_PILL_L4 = registerItems("max_hunli_pill_l4", new MaxHunLiAdding(new FabricItemSettings().food(ModFoodComponents.MAX_HUNLI_ADDING), 10000));
    public static final Item MAX_HUNLI_PILL_L5 = registerItems("max_hunli_pill_l5", new MaxHunLiAdding(new FabricItemSettings().food(ModFoodComponents.MAX_HUNLI_ADDING), 100000));
    public static final Item MAX_HUNLI_PILL_L6 = registerItems("max_hunli_pill_l6", new MaxHunLiAdding(new FabricItemSettings().food(ModFoodComponents.MAX_HUNLI_ADDING), 1000000));
    public static final Item MAX_HUNLI_PILL_L7 = registerItems("max_hunli_pill_l7", new MaxHunLiAdding(new FabricItemSettings().food(ModFoodComponents.MAX_HUNLI_ADDING), 10000000));

    private static Item registerItems(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(MCDuro.MOD_ID, name), item);
    }

    public static void registerModItems(){
        MCDuro.LOGGER.info("Registering Mod Items");
    }
}
