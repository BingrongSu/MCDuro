package net.robert.mcduro.player;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.robert.mcduro.events.ModEvents;
import net.robert.mcduro.math.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    public Integer hunLi = 0;                                               // 玩家当前魂力
    public Integer maxHunLi = 0;                                            // 玩家当前魂力上限
    public Integer hunLiLevel = 0;                                          // 玩家当前魂力等级
    public HashMap<String, List<List<Double>>> wuHun = new HashMap<>();     // 玩家拥有的武魂和相关参数
    public String openedWuHun = "null";                                     // 玩家当前开启的武魂
    public Integer skillIndex = -1;                                         // 玩家当前使用的魂技
    public HashMap<String, Boolean> blProperties = new HashMap<>();
    public HashMap<UUID, Long> openWuHunTicks = new HashMap<>();

    private final List<String> standardWuHun = List.of(                     // 标准武魂类型
                                                        "liuLi",
                                                        "xiangChang",
                                                        "fengHuang");

    public boolean increaseHunLi(int amount, PlayerEntity player) {
        World world = player.getWorld();
        if (!world.isClient && amount != 0) {
            boolean result = false;
            hunLi += amount;
            if (hunLi <= 0) {
                hunLi += amount;
            } else if (hunLi > maxHunLi) {
                hunLi = maxHunLi;
            } else {
                result = true;
            }
            // 向客户端发送数据
            MinecraftServer server = world.getServer();
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(hunLi);
            player.sendMessage(Text.of("Server: Hun Li Set To: " + hunLi));
            assert server != null;
            ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
            server.execute(() -> {
                assert playerEntity != null;
                ServerPlayNetworking.send(playerEntity, ModEvents.SET_HUN_LI, data);
            });
            return result;
        }
        return false;
    }

    public boolean increaseMaxHunLi(int amount, PlayerEntity player) {
        World world = player.getWorld();
        if (!world.isClient && amount != 0) {
            maxHunLi += amount;
            if (maxHunLi < 0) {
                maxHunLi = 0;
                return false;
            }
            if (maxHunLi >= Helper.level2HunLi(hunLiLevel)) {
                maxHunLi = Helper.level2HunLi(hunLiLevel);
                return false;
            }
            // 向客户端发送数据
            MinecraftServer server = world.getServer();
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(maxHunLi);
            player.sendMessage(Text.of("Server: Max Hun Li Set To: " + maxHunLi));
            assert server != null;
            ServerPlayerEntity playerEntity = server.getPlayerManager().getPlayer(player.getUuid());
            server.execute(() -> {
                assert playerEntity != null;
                ServerPlayNetworking.send(playerEntity, ModEvents.SET_MAX_HUN_LI, data);
            });
            return true;
        }
        return false;
    }

    public void jueXing(PlayerEntity player) {
        if (!player.getWorld().isClient && wuHun.isEmpty()) {
            World world = player.getWorld();
            float random = world.getRandom().nextFloat();
            int n;
            List<String> tmp = new ArrayList<>(standardWuHun);
            if (random >= .99f) {        // 三生武魂
                n = 3;
            } else if (random >= .9f) { // 双生武魂
                n = 2;
            } else {
                n = 1;
            }
            for (int i = 0; i < standardWuHun.size() - n; i++) {    // 随机生成武魂
                int index = world.getRandom().nextBetween(0, tmp.size()-1);
                tmp.remove(index);
            }
            tmp.forEach(wuhun -> wuHun.put(wuhun, new ArrayList<>()));
            for (String name : wuHun.keySet()) {player.sendMessage(Text.of("Server: 成功觉醒武魂：" + name));}
            hunLiLevel = Helper.getInitialLevel(world.getTime());
            maxHunLi = Helper.level2HunLi(hunLiLevel);
            MinecraftServer server = player.getServer();
            assert server != null;
            server.execute(() -> {
                syncWuHun(player);

                PacketByteBuf data2 = PacketByteBufs.create();
                data2.writeInt(hunLiLevel);
                ServerPlayNetworking.send((ServerPlayerEntity) player, ModEvents.SET_HUN_LI_LEVEL, data2);

                PacketByteBuf data3 = PacketByteBufs.create();
                data3.writeInt(maxHunLi);
                ServerPlayNetworking.send((ServerPlayerEntity) player, ModEvents.SET_MAX_HUN_LI, data3);
            });
        }
    }

    public void openWuHun(PlayerEntity player, Long tick) {
        if (player.getWorld().isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeLong(tick);
            System.out.println("Client: Player: " + player.getName().getString());
            System.out.println("Client: Tick: " + tick);
            ClientPlayNetworking.send(ModEvents.OPEN_WU_HUN, buf);
        } else {
            openWuHunTicks.put(player.getUuid(), tick);
            System.out.println("Server: Player: " + player.getName().getString());
            System.out.println("Server: Tick: " + tick);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(player.getUuid());
            buf.writeLong(tick);
            List<List<Double>> year = new ArrayList<>(wuHun.getOrDefault(openedWuHun, new ArrayList<>()));
            buf.writeInt(year.size());
            for (List<Double> data : year) {
                buf.writeDouble(data.get(0));
            }
            MinecraftServer server = player.getServer();
            assert server != null;
            server.execute(() -> {
                for (ServerPlayerEntity playerEntity : server.getPlayerManager().getPlayerList()) {
                    ServerPlayNetworking.send(playerEntity, ModEvents.SYNC_SHOWED_YEARS, buf);
                }
            });
        }
    }

    public void addWuHun(PlayerEntity player, String name) {
        if (!player.getWorld().isClient) {
            wuHun.put(name, new ArrayList<>());
        }
        syncWuHun(player);
    }

    public void addRing(PlayerEntity player, double year) {
        if (!player.getWorld().isClient) {
            wuHun.get(openedWuHun).add(List.of(year, 0d));
        }
        syncWuHun(player);
    }

    public void switchWuHun(PlayerEntity player) {
        if (player.getWorld().isClient) {
            System.out.println(wuHun.isEmpty() + " : " + wuHun.size());
            if (!wuHun.isEmpty()) {
                List<String> wuHuns = new ArrayList<>(wuHun.keySet());
                if (openedWuHun.equals("null")) {
                    openedWuHun = wuHuns.get(0);
                } else if (openedWuHun.equals(wuHuns.get(wuHuns.size() - 1))) {
                    openedWuHun = "null";
                } else {
                    openedWuHun = wuHuns.get(wuHuns.indexOf(openedWuHun) + 1);
                }
                syncOpenedWuHun(player);
            }
        }
    }

    public void setOpenedWuHun(String name) {
        openedWuHun = name;
    }

    public void syncWuHun(PlayerEntity player) {
        if (!player.getWorld().isClient) {
            MinecraftServer server = player.getServer();
            assert server != null;
            server.execute(() -> {
                PacketByteBuf data1 = PacketByteBufs.create();
                data1.writeInt(wuHun.size());
                wuHun.forEach((name, years) -> {
                    data1.writeString(name);
                    data1.writeInt(years.size());
                    years.forEach(yearData -> {
                        data1.writeDouble(yearData.get(0));
                        data1.writeDouble(yearData.get(1));
                    });
                });
                ServerPlayNetworking.send((ServerPlayerEntity) player, ModEvents.SET_WU_HUN, data1);
            });
            System.out.println("Server-> Wu Hun: ");
            System.out.println(wuHun);
        }
    }

    public void syncOpenedWuHun(PlayerEntity player) {
        if (player.getWorld().isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(openedWuHun);
            MinecraftClient client = MinecraftClient.getInstance();
            assert client != null;
            client.execute(() -> ClientPlayNetworking.send(ModEvents.SET_OPENED_WU_HUN, buf));
        }
    }
}
