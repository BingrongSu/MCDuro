package net.robert.mcduro.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickScheduler;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.math.Helper;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModServerEvents {
    public static void registerModServerEvents() {
        MCDuro.LOGGER.info("Registering Mod Server Events");
        registerEvents();
    }

    private static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerData playerData = StateSaverAndLoader.getPlayerState(handler.getPlayer());
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(playerData.hunLi);
            data.writeInt(playerData.maxHunLi);
            data.writeInt(playerData.hunLiLevel);

            ServerPlayerEntity player = handler.player;
            player.sendMessage(Text.of("Initialized Server Player: " + player.getName().toString()));
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
                        player.sendMessage(Text.of("Can't increase!"));
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
            player.sendMessage(Text.of("Server-> Opened Wu Hun: " + name));
        }));
    }
}
