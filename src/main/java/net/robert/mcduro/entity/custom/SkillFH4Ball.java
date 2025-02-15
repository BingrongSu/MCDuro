package net.robert.mcduro.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

import java.util.List;
import java.util.Objects;

public class SkillFH4Ball extends AbstractFireballEntity {
    private int explosionPower = 1;
    private final float damage;
    private final double range;
    private final List<Entity> targets;

    public SkillFH4Ball(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower, float damage, double range, List<Entity> targets) {
        super(EntityType.FIREBALL, owner, velocityX, velocityY, velocityZ, world);
        this.explosionPower = explosionPower;
        this.damage = damage;
        this.range = 0.25 * range;
        this.targets = targets;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            if (hitResult instanceof EntityHitResult) {
                if (!(((EntityHitResult) hitResult).getEntity() instanceof SkillFH4Ball)) {
                    rangeDamage(hitResult);
//                createLava(hitResult.getPos());
                }
            } else {
                rangeDamage(hitResult);
//            createLava(hitResult.getPos());
            }
//            this.createLava();
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, true, World.ExplosionSourceType.NONE);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            entity.damage(this.getDamageSources().fireball(this, entity2), 6.0F);
            if (entity2 instanceof LivingEntity) {
                this.applyDamageEffects((LivingEntity)entity2, entity);
            }

        }
    }

    private void rangeDamage(HitResult hitResult) {
        if (!this.getWorld().isClient) {
            PlayerData playerData = StateSaverAndLoader.getPlayerState((PlayerEntity) this.getOwner());
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
                    entity.damage(this.getDamageSources().mobAttack((LivingEntity) this.getOwner()), damage*(1 - (4*distance-4) / playerData.hunLiLevel));
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient && Objects.nonNull(targets) && !targets.isEmpty()) {
            Vec3d dir = targets.get(0).getPos().subtract(this.getPos());
            this.setVelocity(dir.normalize());
            if (Objects.isNull(Objects.requireNonNull(this.getServer()).getOverworld().getEntity(targets.get(0).getUuid()))) {
                targets.remove(0);
            }
        }
    }

    private void createLava() {
        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();
//            Runnable task = () -> {
//                refresh(pos1, world, Blocks.LAVA);
//            };
//            MCDuro.scheduledTask(task, 15L);
//            task = () -> {
//                refresh(pos1, world, Blocks.AIR);
//            };
//            MCDuro.scheduledTask(task, 45L);
            Runnable task = () -> {
                world.spawnParticles(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 5000, 3, 10, 3, 1);
            };
            for (int i = 0; i < 20; i++) {
                MCDuro.scheduledTask(task, 10L*i);
            }

        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("ExplosionPower", (byte)this.explosionPower);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("ExplosionPower", 99)) {
            this.explosionPower = nbt.getByte("ExplosionPower");
        }

    }
}
