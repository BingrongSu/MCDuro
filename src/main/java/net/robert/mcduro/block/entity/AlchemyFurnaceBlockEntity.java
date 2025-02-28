package net.robert.mcduro.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.block.custom.SmithSmeltingBlock;
import net.robert.mcduro.recipe.SmithSmeltingRecipe;
import net.robert.mcduro.screen.SmithSmeltingScreenHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AlchemyFurnaceBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private static final int INPUT_SLOT01 = 0;
    private static final int INPUT_SLOT02 = 1;
    private static final int INPUT_SLOT03 = 2;
    private static final int INPUT_SLOT04 = 3;
    private static final int OUTPUT_SLOT = 4;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 50;
    private int fire = 0;
    private int maxFire = 4 * maxProgress;

    public AlchemyFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMITH_SMELTING_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> AlchemyFurnaceBlockEntity.this.progress;
                    case 1 -> AlchemyFurnaceBlockEntity.this.maxProgress;
                    case 2 -> AlchemyFurnaceBlockEntity.this.fire;
                    case 3 -> AlchemyFurnaceBlockEntity.this.maxFire;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index){
                    case 0 -> AlchemyFurnaceBlockEntity.this.progress = value;
                    case 1 -> AlchemyFurnaceBlockEntity.this.maxProgress = value;
                    case 2 -> AlchemyFurnaceBlockEntity.this.fire = value;
                    case 3 -> AlchemyFurnaceBlockEntity.this.maxFire = value;
                }
            }

            @Override
            public int size() {
                return 4;   // Number of integers we are going to synchronize
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("smith_smelting");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("smith_smelting.progress", progress);
        nbt.putInt("smith_smelting.fire", fire);
//        nbt.putInt("smith_smelting.facing", facing.getId());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        progress = nbt.getInt("smith_smelting.progress");
        fire = nbt.getInt("smith_smelting.fire");
//        facing = Direction.byId(nbt.getInt("smith_smelting.facing"));
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SmithSmeltingScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient){
            return;
        }

        this.increaseFireProgress();
        if (isOutputSlotEmptyOrReceivable()){
            if (this.hasRecipe() && (hasFire() || hasFuel())){
                this.increaseCraftProgress();
                if (hasCraftingFinished()){
                    this.craftItem();
                    this.resetProgress();
                }
                markDirty(world, pos, state);
            } else {
                this.resetProgress();
                markDirty(world, pos, state);
            }
        } else {
            this.resetProgress();
            markDirty(world, pos, state);
        }
        if (hasFire()) {
            if (world.getTickOrder() % 10 == 0) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME,
                        this.getPos().getX() + 0.5f,
                        this.getPos().getY() + 0.2f,
                        this.getPos().getZ() + 0.5f,
                        5, .1, .03, .1, 0);

            }
            if (world.getTickOrder() % 10 == (int) (Math.random() * 10)) {
                MinecraftClient.getInstance().execute(() -> {
                    ClientWorld clientWorld = MinecraftClient.getInstance().world;
                    assert clientWorld != null;
                    clientWorld.addImportantParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                            this.getPos().getX() + 0.5f,
                            this.getPos().getY() + 2f,
                            this.getPos().getZ() + 0.5f,
                            0, 0.13, 0);
                });
            }
        }
    }

    public List<ItemStack> getRendererStack(){
        if(this.getStack(OUTPUT_SLOT).isEmpty()){
            return List.of(this.getStack(INPUT_SLOT01), this.getStack(INPUT_SLOT02));
        } else {
            return List.of(this.getStack(OUTPUT_SLOT));
        }
    }

    @Override
    public void markDirty() {
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        super.markDirty();
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private void craftItem() {
        Optional<RecipeEntry<SmithSmeltingRecipe>> recipe = getCurrentRecipe();

        this.removeStack(INPUT_SLOT01, 1);
        this.removeStack(INPUT_SLOT02, 1);
        this.removeStack(INPUT_SLOT03, 1);

        this.setStack(OUTPUT_SLOT, new ItemStack(recipe.get().value().getResult(null).getItem(),
                getStack(OUTPUT_SLOT).getCount() + recipe.get().value().getResult(null).getCount()));
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftProgress() {
        assert world != null;
        if (!world.isClient) {
            if (world.getBlockState(pos.up()).isOf(ModBlocks.SMITH_SMELTING_CHIMNEY)) {
                progress++;
            } else {
                world.breakBlock(pos, false);
            }
        }
    }

    private void increaseFireProgress() {
        assert world != null;
        if (!world.isClient) {
            fire--;
            if (fire < 0) {
                if (!this.getStack(INPUT_SLOT04).isEmpty()
                    && List.of(Items.COAL, Items.CHARCOAL).contains(this.getStack(INPUT_SLOT04).getItem())
                    && hasRecipe()
                    && isOutputSlotEmptyOrReceivable()) {
                    this.removeStack(INPUT_SLOT04, 1);
                    fire = maxFire;
                } else {
                    fire = 0;
                }
            }
        }
    }

    public boolean hasFire() {
        assert world != null;
        if (!world.isClient) {
            return fire > 0;
        }
        return false;
    }

    public boolean hasFuel() {
        assert world != null;
        if (!world.isClient) {
            return (!this.getStack(INPUT_SLOT04).isEmpty() && List.of(Items.COAL, Items.CHARCOAL).contains(this.getStack(INPUT_SLOT04).getItem()));
        }
        return false;
    }

    private boolean hasRecipe() {
        Optional<RecipeEntry<SmithSmeltingRecipe>> recipe = getCurrentRecipe();

        return recipe.isPresent() && canInsertAmountIntoOutputSlot(recipe.get().value().getResult(null))
                && canInsertItemIntoOutputSlot(recipe.get().value().getResult(null).getItem());
    }

    private Optional<RecipeEntry<SmithSmeltingRecipe>> getCurrentRecipe() {
        SimpleInventory inv = new SimpleInventory(this.size());
        for (int i = 0; i < this.size(); i++) {
            inv.setStack(i, this.getStack(i));
        }

        return getWorld().getRecipeManager().getFirstMatch(SmithSmeltingRecipe.Type.INSTANCE, inv, this.getWorld());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getItem() == item;
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public Direction getFacing() {
        return this.getCachedState().get(SmithSmeltingBlock.FACING);
    }
}