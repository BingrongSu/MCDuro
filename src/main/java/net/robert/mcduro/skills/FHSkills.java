package net.robert.mcduro.skills;
//Zac Edit

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.robert.mcduro.entity.custom.SkillFH4Ball;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

import java.util.ArrayList;
import java.util.List;

public class FHSkills {
    // 1. 凤凰火线
    public static void skill1(PlayerEntity player, ServerWorld world, double power) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);

        Vec3d looking = player.getRotationVector();
        double x = looking.x, y = looking.y, z = looking.z;
        List<Entity> tmp = new ArrayList<>();
        double dDistance = .25;
        double distance = (playerData.hunLiLevel / 1.2d) * (1 + 0.05+(0.2-0.05)*power);
        distance = rangeBoosted(distance, player, playerData);
        double input = playerData.maxHunLi * (0.05+(0.2-0.05)*power);
        double cross = 0;

        playerData.increaseHunLi(-(int)(input + 1), player);
        float damageDrain = 0;

        for (double i = 0; i < distance && cross <= 25; i+=dDistance) {
            float damage = (float) (input/5d * (1-((i - 1) / playerData.hunLiLevel)));
            damage *= (float) (0.8 + 0.2 * Math.log10(playerData.wuHun.get(playerData.openedWuHun).get(0).get(0)));
            damage = damageBoosted(damage, player, playerData);
            double range = .5;
            if (i >= 1) {
                world.spawnParticles(ParticleTypes.FLAME,
                        player.getEyePos().x + x * i, player.getEyePos().y + y * i, player.getEyePos().z + z * i,
                        100, .2d, .2d, .2d, 0d);
            }
            Box box = new Box(player.getEyePos().x + x * i - range, player.getEyePos().y + y * i - range, player.getEyePos().z + z * i - range,
                    player.getEyePos().x + x * i + range, player.getEyePos().y + y * i + range, player.getEyePos().z + z * i + range);
            List<Entity> entities = world.getOtherEntities(player, box);
            tmp = new ArrayList<>(entities);
            BlockPos pos = BlockPos.ofFloored(
                    player.getEyePos().x + x * i,
                    player.getEyePos().y + y * i,
                    player.getEyePos().z + z * i);
            BlockState blockState = world.getBlockState(pos);

            if (!blockState.isOf(Blocks.AIR)) {
                cross ++;
                if (blockState.isIn(BlockTags.LOGS)) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    ItemStack stack = new ItemStack(Items.CHARCOAL, 1);
                    ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    world.spawnEntity(item);
                } else if (blockState.isIn(BlockTags.PLANKS)) {
                    world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                } else if (blockState.isBurnable()) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                } else if (blockState.isIn(BlockTags.ICE)) {
                    world.setBlockState(pos, Blocks.WATER.getDefaultState());
                } else if (blockState.isOf(Blocks.LAVA)) {
                    cross -= (int) (1 / dDistance + 0.5d);
                } else {
                    cross += blockState.getBlock().getHardness();
                    damageDrain += blockState.getBlock().getHardness() * 800;
                    if (damage - damageDrain > 0 && cross <= 25) {
                        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                    }
                }
//                else if (blockState.isOf(ModBlocks.ice_ether_block)) {
//                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
//                    ItemStack stack = new ItemStack(ModBlocks.ice_ether_block.asItem(), 1);
//                    ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
//                    world.spawnEntity(item);
//                    cross += 999999;
//                } else if (blockState.isOf(ModBlocks.fire_ether_block)) {
//                    cross = 0;
//                    i = 0;
//                }
                if (damage - damageDrain < 0) {
                    break;
                }
                for (Entity entity : entities) {
                    if (entity instanceof LivingEntity && !tmp.contains(entity)) {
                        entity.setFireTicks(200);
                        entity.damage(player.getDamageSources().mobAttack(player), damage - damageDrain);
                    }
                }
            }
        }
    }

    // 2. 欲火凤凰
    public static void skill2(PlayerEntity player, double power) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
        double amp = 600 + (3600-600) * power;
        Long duration = (long) (amp * (0.8 + 0.2*Math.log10(playerData.wuHun.get("fengHuang").get(1).get(0))) * (1 + (0.05+(0.2-0.05)*power)));
        playerData.addStatusEffect(player, "FHSkill2", new ArrayList<>(List.of(duration, 0L)));
    }
    // TODO 02/09/2025 魂核的凝聚、魂核增幅魂力恢复

    // 3. 凤翼天翔
    public static void skill3(PlayerEntity player, double power) {
        if (!player.getWorld().isClient) {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            double amp = 2400 + (6000-2400) * power;
            Long duration = (long) (amp * (0.8 + 0.2*Math.log10(playerData.wuHun.get("fengHuang").get(2).get(0))) * (1 + (0.05+(0.2-0.05)*power)));
            playerData.addStatusEffect(player, "FHSkill3", new ArrayList<>(List.of(duration, 0L)));
            if (((ServerPlayerEntity) player).interactionManager.getGameMode().isSurvivalLike()) {
                player.getAbilities().allowFlying = true;
                player.sendAbilitiesUpdate();
            }
        }
    }

    // 4. 凤凰啸天击
    public static void skill4(PlayerEntity player) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
        float damage = 10;
        damage = damageBoosted(damage, player, playerData);
        double range = 10;
        range = rangeBoosted(range, player, playerData);
        double x = player.getRotationVector().x;
        double y = player.getRotationVector().y;
        double z = player.getRotationVector().z;
        double v = 5d;
        SkillFH4Ball fireball = new SkillFH4Ball(player.getWorld(), player, x*v, y*v, z*v,
                                                4, damage, range);
        fireball.setPos(player.getX() + x*2, player.getY() + y*2, player.getZ() + z*2);
        player.getWorld().spawnEntity(fireball);
        // 第一阶段

        // 第二阶段 事件处理 - SkillFH4Ball 中注册完成
    }

//    // 5. 凤凰流星雨
//    public static void Skill5(PlayerEntity player) {
//        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
//        float damage = playerData.years.get("fengHuang").get(4) / 50f;
//        damage = damageBoosted(damage, player, playerData);
//        double range = 0.5d * Math.log10(playerData.years.get("fengHuang").get(4));
//        double v = 5;
//        int n = (int) (5 * Math.log(playerData.years.get("fengHuang").get(4)));
//        for (int i = 0; i < n; i++) {
//            double vx = (player.getRotationVector().x + (Math.random() - 0.5d) / 5d) * v;
//            double vy = (player.getRotationVector().y + (Math.random() - 0.5d) / 5d) * v;
//            double vz = (player.getRotationVector().z + (Math.random() - 0.5d) / 5d) * v;
//            double x = player.getEyePos().getX() + vx / v * 1.5;
//            double y = player.getEyePos().getY() + vy / v * 1.5;
//            double z = player.getEyePos().getZ() + vz / v * 1.5;
//            SkillFH5Ball ball = new SkillFH5Ball(player.getWorld(), x, y, z, vx, vy, vz, damage, range, player);
//            player.getWorld().spawnEntity(ball);
//        }
//    }
//
//    // 6. 凤凰穿云击
//    public static void Skill6(PlayerEntity player) {
//        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
//        float damage = playerData.years.get("fengHuang").get(5) / 5f;
//        damage = damageBoosted(damage, player, playerData);
//        double range = Math.min(15, Math.log(playerData.years.get("fengHuang").get(5) * 2));
//        range = rangeBoosted(range, player, playerData);
//        double x = player.getRotationVector().x;
//        double y = player.getRotationVector().y;
//        double z = player.getRotationVector().z;
//        double v = 10d;
//        SkillFH6Ball fireball = new SkillFH6Ball(player.getWorld(), player, x * v, y * v, z * v,
//                6, damage, range);
//        fireball.setPos(player.getX() + x * 2, player.getY() + y * 2, player.getZ() + z * 2);
//        player.getWorld().spawnEntity(fireball);
//    }
//
//    // 7. 武魂真身 - 邪火凤凰真身
//    public static void Skill7(PlayerEntity player) {
//        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
//        int reduce = (int) (Math.max((7 - Math.log10(playerData.years.get("fengHuang").get(6))) / 7d, 0.1d) * playerData.maxHunLi);
//        if (playerData.hunLi >= reduce) {
//            playerData.increaseHunLi(-reduce, player, player.getWorld());
//            player.addStatusEffect(new StatusEffectInstance(ModEffects.SkillFH7, Math.min(playerData.years.get("fengHuang").get(6), 9000), 1, false, true, true));
//        }
//    }
//
//    // 8. 凤凰弑心链
//    public static void Skill8(PlayerEntity player) {
//        Vec3d looking = player.getRotationVector();
//        Vec3d pos = player.getEyePos();
//        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
//        double distance = 32;
//        double range = 2;
//        range = rangeBoosted(range, player, playerData);
//        double d = 0.5;
//        double time = Math.log(playerData.years.get("fengHuang").get(7));
//        int amplifier = (int) Math.log(playerData.years.get("fengHuang").get(7));
//        LivingEntity target = null;
//        for (int i = 0; i < distance; i++) {
//            Vec3d pos1 = pos.add(looking.multiply(i));
//            Box box = new Box(pos1.x - d, pos1.y - d, pos1.z - d,
//                    pos1.x + d, pos1.y + d, pos1.z + d);
//            List<Entity> entities = player.getWorld().getOtherEntities(player, box);
//            for (Entity entity : entities) {
//                if (entity instanceof LivingEntity) {
//                    target = (LivingEntity) entity;
//                    break;
//                }
//            }
//            if (target != null) {
//                break;
//            }
//        }
//        if (target != null) {
//            ((ServerWorld) player.getWorld()).spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, target.getX(), target.getY(), target.getZ(), 1000, .5, .5, .5, 0);
//            target.setGlowing(true);
//            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 11, amplifier, false, false, false));
//            Box box = new Box(target.getPos().add(-range, -range, -range), target.getPos().add(range, range, range));
//            UUID id = target.getUuid();
//            AtomicBoolean bl = new AtomicBoolean(true);
//            for (int i = 1; i <= (int) time*2 + 1 && bl.get(); i++) {
//                Runnable damageEntity = () -> {
//                    LivingEntity entity = (LivingEntity) ((ServerWorld) player.getWorld()).getEntity(id);
//                    if (entity != null) {
//                        if (player.getWorld().getOtherEntities(null, box).contains(((ServerWorld) player.getWorld()).getEntity(id))) {
//                            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 11, amplifier, false, false, false));
//                            ((ServerWorld) player.getWorld()).spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY(), entity.getZ(), 1000, .5, .5, .5, 0);
//                        } else {
//                            entity.setGlowing(false);
//                            bl.set(false);
//                        }
//                    }
//                };
//                if (i >= (int) time*2 + 1) {
//                    damageEntity = () -> {
//                        LivingEntity entity = (LivingEntity) ((ServerWorld) player.getWorld()).getEntity(id);
//                        if (entity != null) {
//                            entity.setGlowing(false);
//                        }
//                    };
//                }
//                TemplateMod.scheduledTask(damageEntity, i*10L);
//            }
//        }
//    }
//
//    // 9. 凤凰涅槃
//    public static void Skill9(PlayerEntity player) {
//        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
//        ServerWorld world = (ServerWorld) player.getWorld();
//        Vec3d pos = player.getPos();
//        double range = 2 * Math.log10(playerData.years.get("fengHuang").get(8));
//        range = rangeBoosted(range, player, playerData);
//        float damage = playerData.years.get("fengHuang").get(8) / 100f;
//        damage = damageBoosted(damage, player, playerData);
//        for (double i = 0; i < 10; i++) {
//            double finalRange = range;
//            float finalDamage = damage;
//            Runnable task = () -> {
//                world.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z,
//                        99999, finalRange, .5, finalRange, 0);
//                Box box = new Box(pos.add(-finalRange, -1.5, -finalRange), pos.add(finalRange, 1.5, finalRange));
//                List<Entity> entities = world.getOtherEntities(player, box);
//                for (Entity entity : entities) {
//                    if (entity instanceof LivingEntity) {
//                        entity.setOnFireFromLava();
//                        entity.damage(player.getDamageSources().mobAttack(player), finalDamage);
//                    }
//                }
//            };
//            TemplateMod.scheduledTask(task, (long) (i * 10));
//        }
//    }

    private static float damageBoosted(float damage, PlayerEntity player, PlayerData playerData) {
        if (playerData.statusEffects.containsKey("FHSkill2")) {
            damage *= 1.3f;
            System.out.println("Skill Damage Boost FH2");
        }
        if (playerData.statusEffects.containsKey("FHSkill3")) {
            damage *= 1.2f;
            System.out.println("Skill Damage Boost FH3");
        }

        return damage;
    }

    private static double rangeBoosted(double range, PlayerEntity player, PlayerData playerData) {
        if (playerData.statusEffects.containsKey("FHSkill3")) {
            range *= 1.2f;
            System.out.println("Skill Range Boost FH3");
        }
//        if (player.hasStatusEffect(ModEffects.SkillFH3)) {
//            range *= 1d + (Math.log10(playerData.years.get("fengHuang").get(2)) / 10d);
//            player.sendMessage(Text.of("增幅3!"));
//        }
//        if (player.hasStatusEffect(ModEffects.SkillFH7)) {
//            range *= 2d;
//            player.sendMessage(Text.of("增幅7!"));
//        }
        return range;
    }
}
