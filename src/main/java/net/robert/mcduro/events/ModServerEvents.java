package net.robert.mcduro.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.data.ServerData;
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
            player.sendMessage(Text.of("Initialized Server Player: " + player.getName().getString()));
            player.sendMessage(Text.of("Hun Li: " + playerData.hunLi));
            player.sendMessage(Text.of("Max Hun Li: " + playerData.maxHunLi));
            player.sendMessage(Text.of("Hun Li Level: " + playerData.hunLiLevel));
            server.execute(() -> {
                ServerPlayNetworking.send(handler.getPlayer(), ModEvents.INIT_SYNC, data);
                playerData.syncWuHun(player);
            });
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            List<ServerPlayerEntity> players = server.getOverworld().getPlayers();
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
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 999999999, 2, false, false, false));
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
        }));

        ServerEntityEvents.ENTITY_LOAD.register(((entity, serverWorld) -> {
            serverData = net.robert.mcduro.data.StateSaverAndLoader.getServerState(serverWorld.getServer());
            if (entity instanceof HostileEntity hostile) {
                int randomYear = 0;
                if (serverWorld.getBiome(hostile.getBlockPos()).isIn(BiomeTags.IS_FOREST) || serverWorld.getBiome(hostile.getBlockPos()).isIn(BiomeTags.IS_JUNGLE)) {
                    randomYear = (int) (Math.random() * 3000);
                }
                // 随机生成 year 值（0~3000）
                boolean bl = serverData.appendMobIfAbsent(hostile.getUuid(), randomYear);
                if (bl) {
                    // Modify Attack Damage
                    Objects.requireNonNull(hostile.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE)).setBaseValue(100);
                    // 修改最大生命值（血量翻倍）
                    Objects.requireNonNull(hostile.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(hostile.getHealth() * 2);
                    hostile.setHealth(hostile.getHealth() * 2);
                    System.out.println("Server: Modified Hostile's Health" + hostile.getHealth());
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
            if (server.getOverworld().getTime() % 200 == 0) {
                serverData.deleteNullMobs(server);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(ModEvents.USE_SOUL_SKILL, ((server, player, handler, buf, sender) -> {
            String wuhun = buf.readString();
            int n = buf.readInt();
            double power = buf.readDouble();
            switch (wuhun) {
                case "fengHuang":
                    switch (n) {
                        case 0:
                            FHSkills.Skill1(player, server.getOverworld(), power);
                    }
            }
        }));
    }
    // TODO 01/11/2025 魂兽生成在丛林区域，后期添加生物群系-星斗大森林
}
