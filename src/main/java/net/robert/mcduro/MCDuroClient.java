package net.robert.mcduro;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.block.entity.ModBlockEntities;
import net.robert.mcduro.block.entity.renderer.JXBallBlockEntityRenderer;
import net.robert.mcduro.block.entity.renderer.SmithSmeltingBlockEntityRenderer;
import net.robert.mcduro.events.ModClientEvents;
import net.robert.mcduro.key.ModKeyBinds;
import net.robert.mcduro.screen.ModScreenHandlers;
import net.robert.mcduro.screen.SmithSmeltingScreen;

public class MCDuroClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MCDuro.LOGGER.info("Initialize Client");
        ModClientEvents.registerModClientEvents();

        ModKeyBinds.registerKeyBinds();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.JX_BALL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.JUE_XING_TAI, RenderLayer.getTranslucent());
        BlockEntityRendererFactories.register(ModBlockEntities.JX_BALL_BLOCK_ENTITY, JXBallBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SMITH_SMELTING_BLOCK_ENTITY, SmithSmeltingBlockEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.SMITH_SMELTING_SCREEN_HANDLER, SmithSmeltingScreen::new);
    }
}
