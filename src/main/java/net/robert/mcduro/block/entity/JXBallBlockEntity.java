package net.robert.mcduro.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class JXBallBlockEntity extends BlockEntity{

    protected final PropertyDelegate propertyDelegate;
    private int usage = 0;
    private int maxUsage = 2;

    public JXBallBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.JX_BALL_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> JXBallBlockEntity.this.usage;
                    case 1 -> JXBallBlockEntity.this.maxUsage;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> JXBallBlockEntity.this.usage = value;
                    case 1 -> JXBallBlockEntity.this.maxUsage = value;
                }
            }

            @Override
            public int size() {
                return 2;   // Number of integers we are going to synchronize
            }
        };
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("jx_ball.usage", usage);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        usage = nbt.getInt("jx_ball.usage");
    }


    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public boolean broken() {
        return ++usage >= maxUsage;
    }

}