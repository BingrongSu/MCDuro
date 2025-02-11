package net.robert.mcduro.data;


import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.robert.mcduro.MCDuro;

import java.util.*;

public class StateSaverAndLoader extends PersistentState {
    public ServerData serverData = new ServerData();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound mobYearsNbt = new NbtCompound();
        serverData.mobYears.forEach(((uuid, year) -> mobYearsNbt.putInt(uuid.toString(), year)));

        nbt.put("mob_years", mobYearsNbt);
        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        NbtCompound mobYearsNbt = tag.getCompound("mob_years");
        mobYearsNbt.getKeys().forEach((key) -> {
            UUID uuid = UUID.fromString(key);
            Integer year = mobYearsNbt.getInt(key);
            state.serverData.mobYears.put(uuid, year);
        });

        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new, // 若不存在 'StateSaverAndLoader' 则创建
            StateSaverAndLoader::createFromNbt, // 若存在 'StateSaverAndLoader' NBT, 则调用 'createFromNbt' 传入参数
            null // 此处理论上应为 'DataFixTypes' 的枚举，但我们直接传递为空(null)也可以
    );

    public static ServerData getServerState(MinecraftServer server) {
        // (注：如需在任意维度生效，请使用 'World.OVERWORLD' ，不要使用 'World.END' 或 'World.NETHER')
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        // 当第一次调用了方法 'getOrCreate' 后，它会创建新的 'StateSaverAndLoader' 并将其存储于  'PersistentStateManager' 中。
        //  'getOrCreate' 的后续调用将本地的 'StateSaverAndLoader' NBT 传递给 'StateSaverAndLoader::createFromNbt'。
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, "server_data");

        // 若状态未标记为脏(dirty)，当 Minecraft 关闭时， 'writeNbt' 不会被调用，相应地，没有数据会被保存。
        // 从技术上讲，只有在事实上发生数据变更时才应当将状态标记为脏(dirty)。
        // 但大多数开发者和模组作者会对他们的数据未能保存而感到困惑，所以不妨直接使用 'markDirty' 。
        // 另外，这只将对应的布尔值设定为 TRUE，代价是文件写入磁盘时模组的状态不会有任何改变。(这种情况非常少见)
        state.markDirty();

        return state.serverData;
    }
//
//    public static ServerData getPlayerState(PlayerEntity player) {
//        ServerData playerState;
//        if (!player.getWorld().isClient) {
//            StateSaverAndLoader serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));
//
//            // 根据 UUID 获取对应玩家的状态，如果没有该玩家的数据，就创建一个新的玩家状态。
//            playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new ServerData());
//        } else {
//            playerState = new ServerData();
//        }
//        return playerState;
//    }
}
