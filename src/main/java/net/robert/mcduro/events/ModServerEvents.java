package net.robert.mcduro.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.data.ServerData;
import net.robert.mcduro.effects.ModEffects;
import net.robert.mcduro.game.ModGameRules;
import net.robert.mcduro.math.Helper;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;
import net.robert.mcduro.skills.FHSkills;

import java.util.*;

public class ModServerEvents {
    private static ServerData serverData = new ServerData();

    public static void registerModServerEvents() {
        MCDuro.LOGGER.info("Registering Mod Server Events");
        registerEvents();
    }

    private static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer -> {
            serverData = net.robert.mcduro.data.StateSaverAndLoader.getServerState(minecraftServer);
        }));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(handler.getPlayer());
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(playerData.hunLi);
            data.writeInt(playerData.maxHunLi);
            data.writeInt(playerData.hunLiLevel);

            ServerPlayerEntity player = handler.player;
            player.sendMessage(Text.of("Server -> Initialized Server Player: " + player.getName().getString()));
            player.sendMessage(Text.of("Server -> Hun Li: " + playerData.hunLi));
            player.sendMessage(Text.of("Server -> Max Hun Li: " + playerData.maxHunLi));
            player.sendMessage(Text.of("Server -> Hun Li Level: " + playerData.hunLiLevel));
//            List<ServerPlayerEntity> players = new ArrayList<>(server.getPlayerManager().getPlayerList());
//            players.remove(handler.player);
            server.execute(() -> {
                ServerPlayNetworking.send(handler.getPlayer(), ModEvents.INIT_SYNC, data);
                playerData.syncWuHun(player);
                // Sync Showed years
            });
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            for (ServerPlayerEntity player : players) {
                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                if (playerData.maxHunLi != 0) {
                    int increment = Helper.naturalIncrease(playerData.hunLiLevel);
                    boolean bl = playerData.increaseMaxHunLi(increment, player);
                    if (!bl && increment > 0) {
                        System.out.println("Can't increase!");
                    }
                }
                if (playerData.maxHunLi > 0) {
                    if (playerData.hunLi == 0) {
                        if (player.getStatusEffect(StatusEffects.BLINDNESS) == null) {
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, Integer.MAX_VALUE, 2, false, false, false));
                        }
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2, false, false, false));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, 2, false, false, false));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 20, 2, false, false, false));
                    } else {
                        player.removeStatusEffect(StatusEffects.BLINDNESS);
                    }
                }
                if (playerData.hunLi < playerData.maxHunLi) {
                    playerData.increaseHunLi(Helper.naturalRecover(playerData.hunLiLevel), player);
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ModEvents.OPEN_WU_HUN, ((server, player, handler, buf, sender) -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            playerData.openWuHun(player, buf.readLong());
        }));

        ServerPlayNetworking.registerGlobalReceiver(ModEvents.SET_OPENED_WU_HUN, ((server, player, handler, buf, sender) -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            String name = buf.readString();
            playerData.setOpenedWuHun(name);
            System.out.println("Server-> Opened Wu Hun: " + name);
            // Broadcast to others
            PacketByteBuf data = PacketByteBufs.create();
            data.writeUuid(player.getUuid());
            data.writeString(name);
            for (ServerPlayerEntity serverPlayer : server.getPlayerManager().getPlayerList()) {
                if (!serverPlayer.equals(player)) {
                    ServerPlayNetworking.send(serverPlayer, ModEvents.SYNC_PLAYERS_WUHUN, data);
                    System.out.println("Server -> Send %s's showed wuhun to %s's client.".formatted(player.getName().getString(), serverPlayer.getName().getString()));
                }
            }
        }));

        ServerEntityEvents.ENTITY_LOAD.register(((entity, serverWorld) -> {
            serverData = net.robert.mcduro.data.StateSaverAndLoader.getServerState(serverWorld.getServer());
            if (entity instanceof HostileEntity hostile) {
                int randomYear = 0;
                if (!serverWorld.getGameRules().getBoolean(ModGameRules.LIMIT_SOUL_HOSTILE_SPAWN)
                        || (serverWorld.getBiome(hostile.getBlockPos()).isIn(BiomeTags.IS_FOREST)
                        || serverWorld.getBiome(hostile.getBlockPos()).isIn(BiomeTags.IS_JUNGLE)
                        || serverWorld.getBiome(hostile.getBlockPos()).isIn(BiomeTags.ANCIENT_CITY_HAS_STRUCTURE))) {
                    randomYear = (int) (Math.random() * 3000);
                }
                // 随机生成 year 值（0~3000）
                boolean bl = serverData.appendMobIfAbsent(hostile.getUuid(), randomYear);
                if (bl && randomYear > 20) {
                    // Modify Attack Damage
                    Objects.requireNonNull(hostile.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(randomYear/5f);
                    // 修改最大生命值
                    Objects.requireNonNull(hostile.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(randomYear);
                    hostile.setHealth(randomYear);

//                    Modifiers.modModifyMobHealth(hostile);
                    System.out.println("Server -> Spawned a soul hostile with health: " + hostile.getHealth() + ", and year: " + randomYear);
                }
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(ModEvents.GET_MOB_YEAR, ((server, player, handler, buf, sender) -> {
            UUID uuid = buf.readUuid();
            int year = serverData.getMobsYear(uuid);
            PacketByteBuf buf1 = PacketByteBufs.create();
            buf1.writeUuid(uuid);
            buf1.writeInt(year);
            ServerPlayNetworking.send(player, ModEvents.GET_MOB_YEAR, buf1);
            MCDuro.LOGGER.info("Server -> Return result to Player({})'s Client for this mob({}) with year: {}", player.getName().getString(), uuid.toString(), year);
        }));

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if (server.getOverworld().getTime() % 400 == 0) {
                serverData.deleteNullMobs(server);
            }
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                playerData.delNullTargets(player);
                if (!playerData.listOfAllys().contains(player.getName().getString())) {
                    playerData.addAlly(player);
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ModEvents.USE_SOUL_SKILL, ((server, player, handler, buf, sender) -> {
            String wuhun = buf.readString();
            int n = buf.readInt();
            double power = buf.readDouble();
            switch (wuhun) {
                case "fengHuang":
                    PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                    switch (n) {
                        case 0:
                            FHSkills.skill1(player, player.getServerWorld(), power, playerData.targets.isEmpty() ? null : new ArrayList<>(playerData.targets));
                            break;
                        case 1:
                            FHSkills.skill2(player, power);
                            break;
                        case 2:
                            FHSkills.skill3(player, power);
                            break;
                        case 3:
                            FHSkills.skill4(player, power, playerData.targets.isEmpty() ? null : new ArrayList<>(playerData.targets));
                            break;
                    }
                    break;
                case "xiangChang":
                    break;
            }
        }));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                if (playerData.openedWuHun.equals("fengHuang")) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 10, 1, false, false, false));
                }
                if (playerData.statusEffects.isEmpty()) continue;
                if (!playerData.statusEffects.containsKey("FHSkill3") && playerData.hunLiLevel < 70) {
                    if (player.interactionManager.getGameMode().isSurvivalLike()) {
                        player.getAbilities().allowFlying = false;
                        player.sendAbilitiesUpdate();
                    }
                }
                Set<String> effects = new HashSet<>(playerData.statusEffects.keySet());
                for (String name : effects) {
                    playerData.statusEffects.get(name).set(0, playerData.statusEffects.get(name).get(0) - 1L);
                    if (ModEffects.selfEnhancing.contains(name)) {
                        if (playerData.openedWuHun.equals("null")) {
                            playerData.statusEffects.get(name).set(1, playerData.statusEffects.get(name).get(1) + 1L);
                        } else {
                            playerData.statusEffects.get(name).set(1, 0L);
                        }
                    }
                    boolean showParticles = true, ambient = true;
                    if (playerData.statusEffects.get(name).get(1) > 0) {
                        showParticles = false;
                        ambient = false;
                    }
                    if (playerData.statusEffects.get(name).get(1) > 5 * 20L) {
                        playerData.removeStatusEffect(player, name);
                        continue;
                    }
                    if (playerData.statusEffects.get(name).get(0) <= 0) {
                        playerData.removeStatusEffect(player, name);
                    } else {
                        switch (name) {
                            case "FHSkill2":
                                player.addStatusEffect(new StatusEffectInstance(ModEffects.SkillFH2, 10, 1, ambient, showParticles, true));
                                break;
                            case "FHSkill3":
                                player.addStatusEffect(new StatusEffectInstance(ModEffects.SkillFH3, 10, 1, ambient, showParticles, true));
                                break;
                            case "FHSkill7":
                                player.addStatusEffect(new StatusEffectInstance(ModEffects.SkillFH7, 10, 1, ambient, showParticles, true));
                                break;
                            case "FHSkill8":
                                player.addStatusEffect(new StatusEffectInstance(ModEffects.SkillFH8, 10, 1, ambient, showParticles, true));
                                break;
                        }
                    }
                }
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ModEvents.SYNC_PLAYERS_WUHUN, (server, player, handler, buf, sender) -> {
            List<ServerPlayerEntity> players = new ArrayList<>(server.getPlayerManager().getPlayerList());
            players.remove(player);
            for (ServerPlayerEntity otherPlayer : players) {
                PlayerData playerData = StateSaverAndLoader.getPlayerState(otherPlayer);
                PacketByteBuf data = PacketByteBufs.create();
                data.writeUuid(otherPlayer.getUuid());
                data.writeString(playerData.openedWuHun);
                server.execute(() -> {
                    ServerPlayNetworking.send(player, ModEvents.SYNC_PLAYERS_WUHUN, data);
                });
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ModEvents.LOCKED_PLAYER, (server, player, handler, buf, sender) -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            playerData.lockOn(player);
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof PlayerEntity player) {
                PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                playerData.clearStatusEffect(player);
                System.out.println("Clear all the status effects of player %s".formatted(player.getName().getString()));
            }
        });
    }
    // TODO 01/11/2025 后期添加生物群系-星斗大森林
    // TODO 02/09/2025 魂兽生命值突破1024限制
    // TODO 02/09/2025 魂兽掉落物、经验值等
}
