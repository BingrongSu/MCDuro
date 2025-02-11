package net.robert.mcduro.player;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.robert.mcduro.MCDuro;

import java.util.*;
import java.util.stream.Collectors;

public class StateSaverAndLoader extends PersistentState {
    public HashMap<UUID, PlayerData> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        players.forEach((uuid, playerData) -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putInt("hunLi", playerData.hunLi);
            playerNbt.putInt("maxHunLi", playerData.maxHunLi);
            playerNbt.putInt("hunLiLevel", playerData.hunLiLevel);

            NbtCompound wuHunNbt = new NbtCompound();
            playerData.wuHun.forEach((name, years) -> {
                NbtList yearsNbtList = new NbtList();
                years.forEach(year -> {
                    NbtCompound elementNbt = new NbtCompound();
                    elementNbt.putDouble("year", year.get(0));
                    elementNbt.putDouble("skillPower", year.get(1));
                    yearsNbtList.add(elementNbt);
                });
                wuHunNbt.put(name, yearsNbtList);
            });
            playerNbt.put("wuHun", wuHunNbt);

//            playerNbt.putString("openedWuHun", playerData.openedWuHun);
//            playerNbt.putInt("skillIndex", playerData.skillIndex);

            NbtCompound allysNbt = new NbtCompound();
            playerData.allys.forEach((uuid1, name) -> allysNbt.putString(uuid1.toString(), name));
            playerNbt.put("allys", allysNbt);

            NbtCompound effectsNbt = new NbtCompound();
            playerData.statusEffects.forEach((name, data) -> {
                NbtLongArray dataNbt = new NbtLongArray(data);
                effectsNbt.put(name, dataNbt);
            });
            playerNbt.put("statusEffects", effectsNbt);

            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);

        return nbt;
    }

    public static StateSaverAndLoader createFromNbt(NbtCompound tag) {
        StateSaverAndLoader state = new StateSaverAndLoader();
        NbtCompound playersNbt = tag.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            PlayerData playerData = new PlayerData();
            NbtCompound playerNbt = playersNbt.getCompound(key);
            playerData.hunLi = playerNbt.getInt("hunLi");
            playerData.maxHunLi = playerNbt.getInt("maxHunLi");
            playerData.hunLiLevel = playerNbt.getInt("hunLiLevel");

            NbtCompound wuHunNbt = playerNbt.getCompound("wuHun");
            playerData.wuHun.clear();
            for (String name : wuHunNbt.getKeys()) {
                playerData.wuHun.put(name, new ArrayList<>());
                NbtList yearsNbtList = (NbtList) wuHunNbt.get(name);
                assert yearsNbtList != null;
                for (int i = 0; i < yearsNbtList.size(); i++) {
                    double year = yearsNbtList.getCompound(i).getDouble("year");
                    double value1 = yearsNbtList.getCompound(i).getDouble("skillPower");
                    playerData.wuHun.get(name).add(List.of(year, value1));
                }
            }
            System.out.println(playerData.wuHun);

//            playerData.openedWuHun = playerNbt.getString("openedWuHun");
//            playerData.skillIndex = playerNbt.getInt("skillIndex");

            NbtCompound allysNbt = playerNbt.getCompound("allys");
            playerData.allys.clear();
            for (String uuidS : allysNbt.getKeys()) {
                playerData.allys.put(UUID.fromString(uuidS), allysNbt.getString(uuidS));
            }

            NbtCompound effectsNbt = playerNbt.getCompound("statusEffects");
            playerData.statusEffects.clear();
            for (String name : effectsNbt.getKeys()) {
                playerData.statusEffects.put(name, Arrays.stream(effectsNbt.getLongArray(name)).boxed().collect(Collectors.toList()));
            }

            UUID uuid = UUID.fromString(key);
            state.players.put(uuid, playerData);
        });

        return state;
    }

    private static Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::new, // 若不存在 'StateSaverAndLoader' 则创建
            StateSaverAndLoader::createFromNbt, // 若存在 'StateSaverAndLoader' NBT, 则调用 'createFromNbt' 传入参数
            null // 此处理论上应为 'DataFixTypes' 的枚举，但我们直接传递为空(null)也可以
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (注：如需在任意维度生效，请使用 'World.OVERWORLD' ，不要使用 'World.END' 或 'World.NETHER')
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        // 当第一次调用了方法 'getOrCreate' 后，它会创建新的 'StateSaverAndLoader' 并将其存储于  'PersistentStateManager' 中。
        //  'getOrCreate' 的后续调用将本地的 'StateSaverAndLoader' NBT 传递给 'StateSaverAndLoader::createFromNbt'。
        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, "players_data");
//        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, (new Identifier(MCDuro.MOD_ID, "players_data")).toString());

        // 若状态未标记为脏(dirty)，当 Minecraft 关闭时， 'writeNbt' 不会被调用，相应地，没有数据会被保存。
        // 从技术上讲，只有在事实上发生数据变更时才应当将状态标记为脏(dirty)。
        // 但大多数开发者和模组作者会对他们的数据未能保存而感到困惑，所以不妨直接使用 'markDirty' 。
        // 另外，这只将对应的布尔值设定为 TRUE，代价是文件写入磁盘时模组的状态不会有任何改变。(这种情况非常少见)
        state.markDirty();

        return state;
    }

    public static PlayerData getPlayerState(PlayerEntity player) {
        PlayerData playerState;
        if (!player.getWorld().isClient) {
            StateSaverAndLoader serverState = getServerState(Objects.requireNonNull(player.getWorld().getServer()));

            // 根据 UUID 获取对应玩家的状态，如果没有该玩家的数据，就创建一个新的玩家状态。
            playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
        } else {
            playerState = new PlayerData();
        }
        return playerState;
    }
}
