package net.robert.mcduro.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.robert.mcduro.game.ModGameRules;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

import java.util.List;
import java.util.Objects;

public class SkillFH4Ball extends AbstractFireballEntity {
    private int explosionPower = 1;
    private final float damage;
    private final double range;
    private final List<Entity> targets;
    private final double directionX;
    private final double directionY;
    private final double directionZ;
    private Vec3d pp,pv;
    private World pw;// Previous Position, previous world

    public SkillFH4Ball(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, int explosionPower, float damage, double range, List<Entity> targets) {
        super(EntityType.FIREBALL, owner, velocityX, velocityY, velocityZ, world);
        this.directionX=velocityX;
        this.directionY=velocityY;
        this.directionZ=velocityZ;
        this.explosionPower = explosionPower;
        this.damage = damage;
        this.range = range;
        this.targets = targets;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
            if (entityHitResult.getEntity() instanceof SkillFH4Ball ballEntity) {
                if (Objects.equals(ballEntity.getOwner(), this.getOwner())) {
                    return;
                }
            } else if (entityHitResult.getEntity() instanceof SkillFH5Ball ballEntity) {
                if (Objects.equals(ballEntity.getOwner(), this.getOwner())) {
                    return;
                }
            }

        }
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            rangeDamage(hitResult);
            this.explosion((ServerWorld) this.getWorld());
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
            List<Entity> entities = this.getWorld().getOtherEntities(this.getOwner(), box);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    entity.setFireTicks(250);
                    float distance = Math.max(entity.distanceTo(this), 1);
                    entity.damage(this.getDamageSources().mobAttack((LivingEntity) this.getOwner()), damage*(1 - (4*distance-4) / playerData.hunLiLevel));
                }
            }
        }
    }

    private void rangeDamage(Vec3d pos) {
        if (!this.getWorld().isClient) {
            PlayerData playerData = StateSaverAndLoader.getPlayerState((PlayerEntity) this.getOwner());
            double x = pos.x;
            double y = pos.y;
            double z = pos.z;
            Box box = new Box(x - range, y - range, z - range,
                    x + range, y + range, z + range);
            List<Entity> entities = this.getWorld().getOtherEntities(this.getOwner(), box);
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
        if (!this.getWorld().isClient) {
            if (Objects.nonNull(targets) && !targets.isEmpty()) {
                Vec3d dir = targets.get(0).getPos().subtract(this.getPos());
                this.setVelocity(dir.normalize().multiply(7d));
                if (Objects.isNull(Objects.requireNonNull(this.getServer()).getOverworld().getEntity(targets.get(0).getUuid()))) {
                    targets.remove(0);
                }
            } else if(pp == null){
                pp = this.getPos();
                pw = this.getWorld();
            }else{
                double d1 = Math.sqrt(directionX* directionX + directionY * directionY + directionZ * directionZ);
                if (d1 != 0.0) {
                    this.powerX = directionX / d1 * 0.1;
                    this.powerY = directionY / d1 * 0.1;
                    this.powerZ = directionZ / d1 * 0.1;
                }
                Vec3d powerr = new Vec3d(this.powerX,this.powerY,this.powerZ);
                ProjectileUtil.setRotationFromVelocity(this, 0.2F);
                float g = this.getDrag();
                if(this.getWorld()==pw) {
                    pv = this.getPos().subtract(pp);
                }else{
                    this.setPosition(this.getPos().add(pv.multiply(2)));
                }
                Vec3d adden = (this.getPos().subtract(pp)).multiply(1/(this.getPos().subtract(pp)).length());
                Vec3d velocity = (this.getPos().subtract(pp)).add(adden.multiply(powerr.length())).multiply(g);
                this.setVelocity(velocity.normalize().multiply(Math.min(7D, velocity.length() * 1.3d)));
                pp = this.getPos();
                pw = this.getWorld();

            }
            if (this.getWorld().getBlockState(this.getBlockPos()).isIn(BlockTags.PORTALS)) {
                rangeDamage(this.getPos());
                this.explosion((ServerWorld) this.getWorld());
                this.discard();
            }
        }
    }

    public void explosion(ServerWorld world) {
        world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, true, world.getGameRules().getBoolean(ModGameRules.DO_EXPLOSIVE_SKILLS_DESTROY_BLOCKS) ? World.ExplosionSourceType.MOB : World.ExplosionSourceType.NONE);
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
