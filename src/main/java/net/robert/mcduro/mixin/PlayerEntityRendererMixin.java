package net.robert.mcduro.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.robert.mcduro.events.ModClientEvents;
import net.robert.mcduro.item.ModItems;
import net.robert.mcduro.player.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    public void render(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int lightLevel, CallbackInfo ci) {
        PlayerData playerData = ModClientEvents.playerData;
        long openWuHunTick = playerData.openWuHunTicks.getOrDefault(player.getUuid(), 0L);
//        player.sendMessage(Text.of(player.getName().getString() + "-> " + openWuHunTick));
        List<Double> currentWuHun = ModClientEvents.showedYears.getOrDefault(player.getUuid(), new ArrayList<>());
        int n = currentWuHun.size();
        int startSlot = (9 - n) / 2;                                    // 按0～8九个槽位计算，第一个魂环的槽位
        float rotateV = 1 / 60f;                                        // 魂环转速，单位：degree/tick
        float sepMultiplier = 1.32f;                                    // 魂环间距乘数

        List<MatrixStack> matricesList = Collections.nCopies(n, matrices);
        MatrixStack wuHunMatrices = matrices;

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        for (int j = 0; j < n; j++) {
            double pos, target = 0.5;
            long currentTick = player.getWorld().getTime();
            long timeTick = (long) (0.8 * 20L);                         // 单个魂环显示的时间，单位：tick
            double advTick = timeTick * 0.44d;                          // 下一个魂环显示的提前量，单位：tick
            double dTickTotal = currentTick - openWuHunTick + tickDelta;
            double dTick = dTickTotal - (timeTick - advTick) * j;

            double leftBound = j * (timeTick - advTick);
            double rightBound = leftBound + timeTick;

            float targetRadius = (float) Math.pow(sepMultiplier, startSlot + j + 1);
            float radius;
            if (dTickTotal < leftBound) {
                pos = 0;
                radius = 0;
            } else if (dTickTotal < rightBound) {
                pos = target * Math.sin(0.5 * Math.PI * (dTick / timeTick));
                radius = (float) (Math.sin(0.5 * Math.PI * (dTick / timeTick)) * targetRadius);
            } else {
                pos = target;
                radius = targetRadius;
            }

            matricesList.get(j).push();
            matricesList.get(j).translate(0, 0.5, 0);
            matricesList.get(j).scale(1.3f, 1f, 1.3f);
            matricesList.get(j).scale(radius, 1f, radius);
            matricesList.get(j).translate(0, pos + 0.001 * j, 0);
            RotationAxis axis = j % 2 == 0 ? RotationAxis.POSITIVE_Y : RotationAxis.NEGATIVE_Y;
            matricesList.get(j).multiply(axis.rotationDegrees(player.getWorld().getTickOrder() * rotateV % 360));
            matricesList.get(j).multiply(axis.rotationDegrees(player.getWorld().getTickOrder() * rotateV % 360));
//            matrices.translate(0, Math.sin(Math.sin(player.getWorld().getTickOrder() / 200.0) / 16d * 2d), 0); // 上下浮动效果
            itemRenderer.renderItem(suitableStack(currentWuHun.get(j)), ModelTransformationMode.HEAD, lightLevel, OverlayTexture.DEFAULT_UV, matricesList.get(j), vertexConsumerProvider, player.getWorld(), 1);
            matricesList.get(j).pop();
        }
        if (playerData.openedWuHun.equals("fengHuang")) {
            wuHunMatrices.push();
            wuHunMatrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(player.bodyYaw));
            wuHunMatrices.translate(0, 0.2f, -0.7);
            wuHunMatrices.scale(2f, 2f, 0.01f);
            Vec3d yawVec = getPlayerBodyFacingVector(player.bodyYaw);
//            wuHunMatrices.translate(0, 0, -4);
            itemRenderer.renderItem(new ItemStack(ModItems.WU_HUN_FENG_HUANG), ModelTransformationMode.HEAD, lightLevel, OverlayTexture.DEFAULT_UV, matrices, vertexConsumerProvider, player.getWorld(), 1);
            wuHunMatrices.pop();
        }
    }
    // TODO 12/08/2024 添加不同颜色魂环（缺带金纹的十万年和百万年）

    @Unique
    private ItemStack suitableStack(double year) {
        if (year < 100) {
            return new ItemStack(ModItems.SOUL_RING_TEN, 1);
        } else if (year < 1000) {
            return new ItemStack(ModItems.SOUL_RING_HUD, 1);
        } else if (year < 10000) {
            return new ItemStack(ModItems.SOUL_RING_THD, 1);
        } else if (year < 100000) {
            return new ItemStack(ModItems.SOUL_RING_TTD, 1);
        } else if (year < 1000000) {
            return new ItemStack(ModItems.SOUL_RING_HTD, 1);
        } else {
            return new ItemStack(ModItems.SOUL_RING_TEN, 1);
        }
    }

    @Unique
    private static Vec3d getPlayerBodyFacingVector(float bodyYaw) {
        // 获取身体朝向（Yaw 角度）
        double radians = Math.toRadians(bodyYaw); // 转换为弧度

        // 计算方向向量 (忽略Y轴的影响)
        double x = -Math.sin(radians); // X 轴方向
        double z = Math.cos(radians);  // Z 轴方向

        return new Vec3d(x, 0, z);
    }
}
