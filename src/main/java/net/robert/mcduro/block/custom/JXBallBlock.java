package net.robert.mcduro.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.block.entity.JXBallBlockEntity;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;
import org.jetbrains.annotations.Nullable;

public class JXBallBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(3,2,3,13,12,13);

    public JXBallBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new JXBallBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if (world.getBlockState(pos.add(0, -1, 0)).isOf(ModBlocks.JUE_XING_TAI)) {
                if (jueXing(player)) {
                    JXBallBlockEntity jxBallBlockEntity = (JXBallBlockEntity) world.getBlockEntity(pos);
                    assert jxBallBlockEntity != null;
                    if (jxBallBlockEntity.broken()) {
                        world.breakBlock(pos, false);
                    }
                }
            } else {
                world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 2f, false, World.ExplosionSourceType.NONE);
                world.breakBlock(pos, false);
            }
        }
        return ActionResult.SUCCESS;
    }

    private static boolean jueXing(PlayerEntity player) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
        if (!playerData.wuHun.isEmpty()) {
            return false;
        }
        player.setNoGravity(true);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2, false, false, false));
        MCDuro.scheduledTask(() -> {playerData.jueXing(player); player.setNoGravity(false);}, 100L);
        return true;
    }

}
