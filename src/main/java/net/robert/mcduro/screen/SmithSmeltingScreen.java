package net.robert.mcduro.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;

public class SmithSmeltingScreen extends HandledScreen<net.robert.mcduro.screen.SmithSmeltingScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(MCDuro.MOD_ID, "textures/gui/smith_smelting_gui.png");

    public SmithSmeltingScreen(SmithSmeltingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }


    @Override
    protected void init() {
        super.init();
        titleY = 1000;
        playerInventoryTitleY = 1000;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,0.8f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth)/2;
        int y = (height - backgroundHeight)/2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
        renderProgressArrow(context, x, y);
        renderFireIcon(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if (handler.isCrafting()) {
            context.drawTexture(TEXTURE, x+80, y+35, 178, 2 + 18 * handler.getProgressLevel(), handler.getScaledProgress(), 15);
        }
    }

    private void renderFireIcon(DrawContext context, int x, int y) {
        context.drawTexture(TEXTURE, x+42, y + 47 + (14 - handler.getScaledFire()), 178, 88 - handler.getScaledFire(), 14, handler.getScaledFire());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
