package net.robert.mcduro.block.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.block.entity.JXBallBlockEntity;

public class JXBallBlockEntityRenderer implements BlockEntityRenderer<JXBallBlockEntity> {
    public JXBallBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }


    @Override
    public void render(JXBallBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        assert world != null;
        matrices.push();
        // 计算当前y值的偏移
        double offset = Math.sin((world.getTime() + tickDelta) / 8.0) / 16d * 2d;
        // 移动物品
        matrices.translate(0.5, 0.5 + offset, 0.5);
        // 中心点调整并旋转
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((world.getTime() + tickDelta) * 4)); // 绕 Y 轴旋转

        // 渲染方块模型
        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(ModBlocks.JX_BALL, 1), ModelTransformationMode.HEAD, getLightLevel(world, entity.getPos()), overlay, matrices, vertexConsumers, world, 0);
        matrices.pop();
        if (world.getBlockState(entity.getPos().add(0, -1, 0)).isOf(ModBlocks.JUE_XING_TAI))
            spawnParticles(entity.getWorld(), entity.getPos());
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }

    private static void spawnParticles(World world, BlockPos pos) {
        assert world != null;
        if (world.getTickOrder() % 50 == 0) {
            for (int i = 0; i < 5; i++) {
                double random = Math.random()*360;
                double x = pos.getX() + 0.5 + Math.sin(random) * (0.5 - 1/16d);
                double y = pos.getY() + Math.random();
                double z = pos.getZ() + 0.5 + Math.cos(random) * (0.5 - 1/16d);
                double v = 0.4;
                double vx = Math.cos(random) * v;
                double vy = Math.random() * v * 0.2;
                double vz = Math.sin(random) * v;
                world.addParticle(ParticleTypes.ENCHANT, x, y, z, vx, vy, vz);
            }
        }
    }
}
