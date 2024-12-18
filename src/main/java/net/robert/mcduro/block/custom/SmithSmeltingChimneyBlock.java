package net.robert.mcduro.block.custom;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class SmithSmeltingChimneyBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(3,0,3,13,16,13);

    public SmithSmeltingChimneyBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
