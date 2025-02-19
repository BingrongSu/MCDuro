package net.robert.mcduro.entity.custom;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.robert.mcduro.game.ModGameRules;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SkillFH5Ball extends SmallFireballEntity {
    private final float damage;
    private final double range;
    private final List<Entity> targets;
    private final double directionX;
    private final double directionY;
    private final double directionZ;
    private Vec3d pp,pv;
    private World pw;// Previous Position, previous world
    private Entity target = null;
    private final int index;

    public SkillFH5Ball(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ, float damage, double range, List<Entity> targets, int index) {
        super(world, owner, velocityX, velocityY, velocityZ);
        this.directionX=velocityX;
        this.directionY=velocityY;
        this.directionZ=velocityZ;
        this.damage = damage;
        this.range = 0.5 * range;
        this.targets = targets;
        this.index = index;
        this.setPosition(owner.getEyePos().add(owner.getRotationVector().multiply(0.5d)));
        if (!Objects.isNull(targets) && !targets.isEmpty()) {
            this.target = targets.get(index % targets.size());
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
//        HitResult.Type type = hitResult.getType();
//        if (type == HitResult.Type.ENTITY) {
//            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
//            if (entityHitResult.getEntity() instanceof SkillFH4Ball ballEntity) {
//                if (Objects.equals(ballEntity.getOwner(), this.getOwner())) {
//                    return;
//                }
//            } else if (entityHitResult.getEntity() instanceof SkillFH5Ball ballEntity) {
//                if (Objects.equals(ballEntity.getOwner(), this.getOwner())) {
//                    return;
//                }
//            }
//        }
        if (!this.getWorld().isClient) {
            rangeDamage(hitResult);
        }
        super.onCollision(hitResult);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (!this.getWorld().isClient) {
            Entity entity = entityHitResult.getEntity();
            Entity entity2 = this.getOwner();
            entity.damage(this.getDamageSources().fireball(this, entity2), 6.0F);
            entity.setFireTicks(200);
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
                    entity.damage(this.getDamageSources().mobAttack((LivingEntity) this.getOwner()), damage);
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
            if (Objects.nonNull(targets)) {
                List<Entity> tmp = new ArrayList<>(targets);
                tmp.forEach(entity -> {
                    if (Objects.isNull(entity) || Objects.isNull(Objects.requireNonNull(this.getServer()).getOverworld().getEntity(entity.getUuid()))) {
                        targets.remove(entity);
                    }
                });
                if (Objects.isNull(target) && !targets.isEmpty()) {
                    target = targets.get(this.index % targets.size());
                } else {
                    this.target = null;
                }
                if (Objects.nonNull(target)) {
                    Vec3d dir = target.getPos().subtract(this.getPos());
                    this.setVelocity(dir.normalize().multiply(5d));
                }
            }
            if (Objects.isNull(this.target)) {
                if(pp == null){
                    pp = this.getPos();
                    pw = this.getWorld();
                }else{
                    double d1 = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
                    if (d1 != 0.0) {
                        this.powerX = directionX / d1 * 0.1;
                        this.powerY = directionY / d1 * 0.1;
                        this.powerZ = directionZ / d1 * 0.1;
                    }
                    Vec3d powerr = new Vec3d(this.powerX, this.powerY, this.powerZ);
                    ProjectileUtil.setRotationFromVelocity(this, 0.2F);
                    float g = this.getDrag();
                    if (this.getWorld() == pw) {
                        pv = this.getPos().subtract(pp);
                    } else {
                        this.setPosition(this.getPos().add(pv.multiply(2)));
                    }
                    Vec3d adden = (this.getPos().subtract(pp)).multiply(1 / (this.getPos().subtract(pp)).length());
                    Vec3d velocity = (this.getPos().subtract(pp)).add(adden.multiply(powerr.length())).multiply(g);
                    this.setVelocity(velocity.normalize().multiply(Math.min(5d, velocity.length() * 1.2d)));

                }
            }
            pp = this.getPos();
            pw = this.getWorld();
            if (this.getWorld().getBlockState(this.getBlockPos()).isIn(BlockTags.PORTALS)) {
                rangeDamage(this.getPos());
                this.discard();
            }
        }
    }
}
