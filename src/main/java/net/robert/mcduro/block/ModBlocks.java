package net.robert.mcduro.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.block.custom.AlchemyFurnaceBlock;
import net.robert.mcduro.block.custom.JXBallBlock;
import net.robert.mcduro.block.custom.SmithSmeltingBlock;
import net.robert.mcduro.block.custom.SmithSmeltingChimneyBlock;

public class ModBlocks {
    public static final Block SHEN_SILVER_ORE = registerBlocks("shen_silver_ore",
            new Block(FabricBlockSettings.copyOf(Blocks.DIAMOND_ORE).strength(4f, 4f)));
    public static final Block DEEPSLATE_SHEN_SILVER_ORE = registerBlocks("deepslate_shen_silver_ore",
            new Block(FabricBlockSettings.copyOf(ModBlocks.SHEN_SILVER_ORE).strength(4f, 4f)));
    public static final Block NETHER_SHEN_SILVER_ORE = registerBlocks("nether_shen_silver_ore",
            new Block(FabricBlockSettings.copyOf(ModBlocks.SHEN_SILVER_ORE).strength(3f, 3f).sounds(BlockSoundGroup.NETHER_ORE)));
    public static final Block SHEN_SILVER_BLOCK = registerBlocks("shen_silver_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).strength(4f, 9f).sounds(BlockSoundGroup.METAL)));

    public static final Block JUE_XING_TAI = registerBlocks("jue_xing_tai",
            new Block(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).nonOpaque().strength(8f, 9999f)));
    public static final Block JX_BALL = registerBlocks("jx_ball",
            new JXBallBlock(FabricBlockSettings.copyOf(Blocks.GLASS)
                    .sounds(BlockSoundGroup.GLASS)
                    .luminance(state -> 10)
                    .nonOpaque()
                    .solidBlock((state, world, pos) -> false)
                    .blockVision((state, world, pos) -> false)
            ));
    public static final Block SMITH_SMELTING = registerBlocks("smith_smelting",
            new SmithSmeltingBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
                    .strength(Blocks.DEEPSLATE.getHardness(), Blocks.DEEPSLATE.getBlastResistance())
                    .nonOpaque()));
    public static final Block SMITH_SMELTING_CHIMNEY = registerBlocks("smith_smelting_chimney",
            new SmithSmeltingChimneyBlock(FabricBlockSettings.copyOf(Blocks.STONE)
                    .strength(Blocks.DEEPSLATE.getHardness(), Blocks.DEEPSLATE.getBlastResistance())
                    .nonOpaque()));
    public static final Block ALCHEMY_FURNACE = registerBlocks("alchemy_furnace",
            new AlchemyFurnaceBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK)
                    .strength(Blocks.COPPER_BLOCK.getHardness(), Blocks.COPPER_BLOCK.getBlastResistance())
                    .nonOpaque()));

    // 注册方法  注册方块与注册方块物品
    private static Block registerBlocks(String name, Block block){
        registerBlockItems(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(MCDuro.MOD_ID, name), block);
    }
    private static Item registerBlockItems(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(MCDuro.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }
    public static void registerModBlocks(){
        MCDuro.LOGGER.info("Registering Mod Blocks");
    }
}
