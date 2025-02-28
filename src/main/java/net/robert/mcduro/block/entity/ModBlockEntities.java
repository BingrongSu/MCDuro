package net.robert.mcduro.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.block.ModBlocks;

public class ModBlockEntities {
    public static final BlockEntityType<JXBallBlockEntity> JX_BALL_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MCDuro.MOD_ID, "jx_ball_be"),
                    FabricBlockEntityTypeBuilder.create(JXBallBlockEntity::new,
                            ModBlocks.JX_BALL).build());
    public static final BlockEntityType<SmithSmeltingBlockEntity> SMITH_SMELTING_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MCDuro.MOD_ID, "smith_smelting_be"),
                    FabricBlockEntityTypeBuilder.create(SmithSmeltingBlockEntity::new,
                            ModBlocks.SMITH_SMELTING).build());
    public static final BlockEntityType<AlchemyFurnaceBlockEntity> ALCHEMY_FURNACE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MCDuro.MOD_ID, "alchemy_furnace_be"),
                    FabricBlockEntityTypeBuilder.create(AlchemyFurnaceBlockEntity::new,
                            ModBlocks.ALCHEMY_FURNACE).build());

    public static void registerBlockEntities(){
        MCDuro.LOGGER.info("Registering block entities for "+MCDuro.MOD_ID);
    }
}
