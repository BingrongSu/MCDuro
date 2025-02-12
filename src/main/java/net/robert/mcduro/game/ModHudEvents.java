package net.robert.mcduro.game;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.events.ModClientEvents;
import net.robert.mcduro.events.ModEvents;
import net.robert.mcduro.math.Helper;
import net.robert.mcduro.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModHudEvents {
    private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = new Identifier("hud/experience_bar_background");
    private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = new Identifier("hud/experience_bar_progress");
    private static final Identifier JUMP_BAR_BACKGROUND_TEXTURE = new Identifier("hud/jump_bar_background");
    private static final Identifier JUMP_BAR_COOLDOWN_TEXTURE = new Identifier("hud/jump_bar_cooldown");
    private static final Identifier JUMP_BAR_PROGRESS_TEXTURE = new Identifier("hud/jump_bar_progress");

    public static void registerModHudEvents() {
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            InGameHud hud = client.inGameHud;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            Text textL1 = Text.literal("Level: " + Helper.hunLi2level(ModClientEvents.playerData.maxHunLi) + ", " + ModClientEvents.playerData.hunLiLevel);
            Text textL2 = Text.literal("Power: " + ModClientEvents.playerData.hunLi);
            Text textL3 = Text.literal("Ability: " + ModClientEvents.playerData.maxHunLi);
            int x = 5;
            int y = context.getScaledWindowHeight() - 40;
            context.drawText(textRenderer, textL1, x, y, 0xff1111, true);
            y += 10;
            context.drawText(textRenderer, textL2, x, y, 0xff1111, true);
            y += 10;
            context.drawText(textRenderer, textL3, x, y, 0xff1111, true);


            HitResult hitResult = client.crosshairTarget;
            assert hitResult != null;
            if (hitResult.getType().equals(HitResult.Type.ENTITY)) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                    float health = livingEntity.getHealth();
                    UUID uuid = livingEntity.getUuid();
                    Text text01 = Text.of("Name: " + livingEntity.getName().getString() + "   UUID: " + uuid);
                    Text text02 = Text.of("Health: " + health);
                    y = 5;
                    context.drawText(textRenderer, text01, x, y, 0xff1111, true);
                    y += 10;
                    context.drawText(textRenderer, text02, x, y, 0xff1111, true);
                    if (livingEntity instanceof HostileEntity) {
                        int year = -1;
                        if (ModClientEvents.mobsYear.containsKey(uuid)) {
                            year = ModClientEvents.mobsYear.get(uuid);
                        } else {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeUuid(uuid);
                            ClientPlayNetworking.send(ModEvents.GET_MOB_YEAR, buf);
                            MCDuro.LOGGER.info("Client -> Sent request to Server for this mob's year: {}", uuid.toString());
                        }
                        Text text03 = Text.of("Year: " + year);
                        y += 10;
                        context.drawText(textRenderer, text03, x, y, 0xff1111, true);
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            PlayerData playerData = ModClientEvents.playerData;
            if (!playerData.openedWuHun.equals("null")) {
                List<List<Double>> wuHunData = playerData.wuHun.getOrDefault(playerData.openedWuHun, new ArrayList<>());
                int n = 0;
                for (List<Double> skill : wuHunData) {
                    double power = skill.get(1) - ModClientEvents.thresholdVal;
                    if (power >= 0) {
                        renderProgressBar(context, power, n++);
                    }
                }
            }
        });

    }

    private static void renderProgressBar(DrawContext context, double percent, int n) {
        MinecraftClient client = MinecraftClient.getInstance();
        InGameHud hud = client.inGameHud;

        // 屏幕宽度和高度
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // 进度条的宽度和高度
        int barWidth = 182;  // 进度条的总宽度
        int barHeight = 5;  // 进度条的高度

        // 进度条起始位置
        int x = (screenWidth - barWidth) / 2;  // 居中显示
        int y = (screenHeight - barHeight) / 2 + 70 - (barHeight + 2) * n;  // 进度条距离屏幕底部的高度

        // 计算进度条填充的宽度
        int fillWidth = (int) (barWidth * percent);

        // 绘制进度条的背景（灰色背景）
        client.getProfiler().push("skillPower" + n);
        context.drawGuiTexture(JUMP_BAR_BACKGROUND_TEXTURE, x, y, barWidth, barHeight);

        // 绘制进度条的填充部分
        context.drawGuiTexture(JUMP_BAR_PROGRESS_TEXTURE, barWidth, barHeight, 0, 0, x, y, fillWidth, barHeight);
        client.getProfiler().pop();
    }
}
