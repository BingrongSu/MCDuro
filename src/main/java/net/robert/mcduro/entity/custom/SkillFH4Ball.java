package net.robert.mcduro.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import net.robert.mcduro.MCDuro;

import java.util.List;

public class SkillFH4Ball extends FireballEntity {
    private final float damage;
    private final double range;

    public SkillFH4Ball(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower, float damage, double range) {
        super(world, owner, velocityX, velocityY, velocityZ, explosionPower);
        this.damage = damage;
        this.range = range;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult instanceof EntityHitResult) {
            if (!(((EntityHitResult) hitResult).getEntity() instanceof SkillFH4Ball)) {
                super.onCollision(hitResult);
                rangeDamage(hitResult);
//                createLava(hitResult.getPos());
            }
        } else {
            super.onCollision(hitResult);
            rangeDamage(hitResult);
//            createLava(hitResult.getPos());
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!(entityHitResult.getEntity() instanceof SkillFH4Ball)) {
            super.onEntityHit(entityHitResult);
            rangeDamage(entityHitResult);
//            createLava(entityHitResult.getPos());
        }
    }

    private void rangeDamage(HitResult hitResult) {
        if (!this.getWorld().isClient) {
            double x = hitResult.getPos().x;
            double y = hitResult.getPos().y;
            double z = hitResult.getPos().z;
            Box box = new Box(x - range, y - range, z - range,
                    x + range, y + range, z + range);
            List<Entity> entities = this.getWorld().getOtherEntities(this, box);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    entity.setFireTicks(250);
                    float distance = Math.max(entity.distanceTo(this), 1);
                    entity.damage(this.getDamageSources().mobAttack((LivingEntity) this.getOwner()), damage/(distance*distance));
                }
            }
        }
    }

    @Override
    public void tick() {

    }

    private void createLava(Position pos1) {
        if (!this.getWorld().isClient) {
            World world = this.getWorld();
            Runnable task = () -> {
                refresh(pos1, world, Blocks.LAVA);
            };
            MCDuro.scheduledTask(task, 15L);
            task = () -> {
                refresh(pos1, world, Blocks.AIR);
            };
            MCDuro.scheduledTask(task, 45L);
        }
    }

    private void refresh(Position pos1, World world, Block block) {
        BlockPos pos = BlockPos.ofFloored(pos1);
        for (int i = -1; i < 2; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = -1; k < 2; k++) {
                    if (block.equals(Blocks.LAVA)) {
                        if (world.getBlockState(pos.add(i, j, k)).isOf(Blocks.AIR)) {
                            world.setBlockState(pos.add(i, j, k), block.getDefaultState());
                        }
                    } else if (block.equals(Blocks.AIR)){
                        if (world.getBlockState(pos.add(i, j, k)).isOf(Blocks.LAVA)) {
                            world.setBlockState(pos.add(i, j, k), block.getDefaultState());
                        }
                    }
                }
            }
        }
    }
}
