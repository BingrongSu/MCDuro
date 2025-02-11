package net.robert.mcduro.data;

import net.minecraft.server.MinecraftServer;
import net.robert.mcduro.MCDuro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ServerData {
    public HashMap<UUID, Integer> mobYears = new HashMap<>();

    public boolean appendMobIfAbsent(UUID uuid, Integer year) {
        if (!mobYears.containsKey(uuid)) {
            MCDuro.LOGGER.info("Server -> Appended Mob: {}    Year: {}", uuid, year);
            mobYears.put(uuid, year);
            return true;
        } else {
            MCDuro.LOGGER.info("Server -> Load Mob: {}    Year: {} ", uuid, mobYears.get(uuid));
            return false;
        }
    }

    public Integer getMobsYear(UUID uuid) {
        return mobYears.getOrDefault(uuid, -1);
    }

    public void deleteMob(UUID uuid) {
        MCDuro.LOGGER.info("Server -> Delete Mob: {}    Year: {}", uuid, mobYears.get(uuid));
        mobYears.remove(uuid);
    }

    public void deleteNullMobs(MinecraftServer server) {
        Set<UUID> allMobs = new HashSet<>(mobYears.keySet());
        for (UUID uuid : allMobs) {
            if (server.getOverworld().getEntity(uuid) == null) {
                deleteMob(uuid);
            }
        }
    }
}
