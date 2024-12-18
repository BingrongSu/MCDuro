package net.robert.mcduro.block.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.robert.mcduro.block.entity.SmithSmeltingBlockEntity;

import java.util.List;
import java.util.Objects;

public class SmithSmeltingBlockEntityRenderer implements BlockEntityRenderer<SmithSmeltingBlockEntity> {
    public SmithSmeltingBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }


    @Override
    public void render(SmithSmeltingBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        List<ItemStack> stacks = entity.getRendererStack();

        if (stacks.size() == 1) {
            matrices.push();
            matrices.translate(0.5f, 9 / 16f, 0.7f);
            matrices.translate(0, 0, -0.2);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getFacing().asRotation()));
            matrices.translate(0, 0, 0.2);
            matrices.scale(0.3f, .3f, 0.3f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
            itemRenderer.renderItem(stacks.get(0), ModelTransformationMode.GUI, getLightLevel(Objects.requireNonNull(entity.getWorld()),
                    entity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);
            matrices.pop();
        } else {
            MatrixStack matrices1 = matrices;
            MatrixStack matrices2 = matrices;
            matrices1.push();
            matrices1.translate(6 / 16f, 9 / 16f, 0.7);
            matrices1.translate(2 / 16f, 0, -0.2);
            matrices1.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getFacing().asRotation()));
            matrices1.translate(-2 / 16f, 0, 0.2);
            matrices1.scale(0.3f, .3f, 0.3f);
            matrices1.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
            itemRenderer.renderItem(stacks.get(0), ModelTransformationMode.GUI, getLightLevel(Objects.requireNonNull(entity.getWorld()),
                    entity.getPos()), OverlayTexture.DEFAULT_UV, matrices1, vertexConsumers, entity.getWorld(), 1);
            matrices1.pop();

            matrices2.push();
            matrices2.translate(10 / 16f, 9 / 16f, 0.7);
            matrices1.translate(-2 / 16f, 0, -0.2);
            matrices1.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getFacing().asRotation()));
            matrices1.translate(2 / 16f, 0, 0.2);
            matrices2.scale(0.3f, .3f, 0.3f);
            matrices2.multiply(RotationAxis.POSITIVE_X.rotationDegrees(270));
            itemRenderer.renderItem(stacks.get(1), ModelTransformationMode.GUI, getLightLevel(Objects.requireNonNull(entity.getWorld()),
                    entity.getPos()), OverlayTexture.DEFAULT_UV, matrices2, vertexConsumers, entity.getWorld(), 1);
            matrices2.pop();
        }
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}
