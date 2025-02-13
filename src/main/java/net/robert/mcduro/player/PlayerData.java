package net.robert.mcduro.player;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.events.ModEvents;
import net.robert.mcduro.math.Helper;

import java.util.*;

public class PlayerData {
    public Integer hunLi = 0;                                                   // 玩家当前魂力
    public Integer maxHunLi = 0;                                                // 玩家当前魂力上限
    public Integer hunLiLevel = 0;                                              // 玩家当前魂力等级
    public HashMap<String, List<List<Double>>> wuHun = new HashMap<>();         // 玩家拥有的武魂和相关参数
    public String openedWuHun = "null";                                         // 玩家当前开启的武魂
    public Integer skillIndex = -1;                                             // 玩家当前使用的魂技
    public HashMap<String, Boolean> blProperties = new HashMap<>();
    public HashMap<UUID, Long> openWuHunTicks = new HashMap<>();                // 玩家打开武魂的时刻
    public HashMap<UUID, String> allys = new HashMap<>();                       // 玩家的盟友
    public PlayerEntity lastAttacker = null;                                    // 上一个攻击此玩家的玩家
    public Map<String, List<Long>> statusEffects = new HashMap<>();             // 玩家拥有的魂技相关状态效果
    public List<Entity> targets = new ArrayList<>();

    private final List<String> standardWuHun = List.of(                         // 标准武魂类型
                                                        "liuLi",
                                                        "xiangChang",
                                                        "fengHuang");

    public boolean increaseHunLi(int amount, PlayerEntity player) {
        World world = player.getWorld();
        if (!world.isClient && amount != 0) {
            boolean result = false;
            hunLi += amount;
            if (hunLi <= 0) {
                hunLi = 0;
            } else if (hunLi > maxHunLi) {
                hunLi = maxHunLi;
            } else {
                result = true;
            }
            // 向客户端发送数据
            MinecraftServer server = world.getServer();
            PacketByteBuf data = PacketByteBufs.create();
            data.writeInt(hunLi);
            System.out.println("Server: Hun Li Set To: " + hunLi);
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
        MinecraftServer server = world.getServer();
        assert server != null;
        ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(player.getUuid());
        assert serverPlayer != null;
        if (!world.isClient && amount != 0) {
//            maxHunLi += amount;
//            if (maxHunLi < 0) {
//                maxHunLi = 0;
//                return false;
//            }
//            if (maxHunLi >= Helper.level2HunLi(hunLiLevel+1)) {
//                if (hunLiLevel % 10 == 0) {
//                    maxHunLi -= amount;
//                    System.out.println("到达瓶颈，无法突破！");
//                    return false;
//                }
//                else {
//                    hunLiLevel ++;
//                    PacketByteBuf buf = PacketByteBufs.create();
//                    buf.writeInt(hunLiLevel);
//                    ServerPlayNetworking.send(serverPlayer, ModEvents.SET_HUN_LI_LEVEL, buf);
//                    System.out.println("成功突破到下一级! ");
//                }
//            }
            maxHunLi = Helper.increaseMaxHunLi(maxHunLi, amount, player);
            hunLiLevel = Helper.hunLi2level(maxHunLi);
            // 向客户端发送数据
            PacketByteBuf data1 = PacketByteBufs.create();
            data1.writeInt(maxHunLi);
            PacketByteBuf data2 = PacketByteBufs.create();
            data2.writeInt(hunLiLevel);
            System.out.println("Server -> Max Hun Li Set To: " + maxHunLi);
            server.execute(() -> {
                ServerPlayNetworking.send(serverPlayer, ModEvents.SET_MAX_HUN_LI, data1);
                ServerPlayNetworking.send(serverPlayer, ModEvents.SET_HUN_LI_LEVEL, data2);
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
                MCDuro.GAIN_WUHUN.trigger((ServerPlayerEntity) player);
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
            MCDuro.GAIN_WUHUN.trigger((ServerPlayerEntity) player);
            wuHun.put(name, new ArrayList<>());
            switch (name) {
                case "fengHuang":
                    MCDuro.GAIN_WUHUN_FH.trigger((ServerPlayerEntity) player);
            }
        }
        syncWuHun(player);
    }

    public void addRing(PlayerEntity player, double year) {
        if (!player.getWorld().isClient) {
            boolean stuck = Helper.hunLi2level(maxHunLi) % 10 == 9 && Helper.hunLi2level(maxHunLi+1) % 10 == 0;
            if (stuck && wuHun.get(openedWuHun).size() < 9) {
                wuHun.get(openedWuHun).add(List.of(year, 0d));
                syncWuHun(player);
                maxHunLi += 1;
                hunLiLevel += 1;
                PacketByteBuf dat1 = PacketByteBufs.create();
                dat1.writeInt(maxHunLi);
                PacketByteBuf dat2 = PacketByteBufs.create();
                dat2.writeInt(hunLiLevel);

                World world = player.getWorld();
                MinecraftServer server = world.getServer();
                assert server != null;
                ServerPlayerEntity serverPlayer = server.getPlayerManager().getPlayer(player.getUuid());
                assert serverPlayer != null;
                server.execute(() -> {
                    ServerPlayNetworking.send(serverPlayer, ModEvents.SET_MAX_HUN_LI, dat1);
                    ServerPlayNetworking.send(serverPlayer, ModEvents.SET_HUN_LI_LEVEL, dat2);
                });
                if (year < 100) {
                    MCDuro.GET_RING_TEN_CRI.trigger(serverPlayer);
                } else if (year < 1000) {
                    MCDuro.GET_RING_HUD_CRI.trigger(serverPlayer);
                } else if (year < 10000) {
                    MCDuro.GET_RING_THD_CRI.trigger(serverPlayer);
                } else if (year < 100000) {
                    MCDuro.GET_RING_TTD_CRI.trigger(serverPlayer);
                } else if (year < 1000000) {
                    MCDuro.GET_RING_HTD_CRI.trigger(serverPlayer);
                }
//                for (int i = 0; i < wuHun.size() + 1; i++) {
//                    Runnable task = () -> {
//                        switchWuHun(player);
//                        System.out.println("Auto Switch");
//                    };
//                    MCDuro.scheduledTask(task, 2L * i + 1);
//                }
            }
        }
    }
    // TODO 02/08/2025 吸收魂环的成就提示

    public void switchWuHun(PlayerEntity player) {
        if (player.getWorld().isClient) {
            System.out.println(wuHun.isEmpty() + " : " + wuHun.size());
            if (!wuHun.isEmpty()) {
                List<String> wuHuns = new ArrayList<>(wuHun.keySet());
                if (openedWuHun.equals("null")) {
                    openedWuHun = wuHuns.get(0);
                    player.sendMessage(Text.translatable("wuhun." + openedWuHun), true);
                } else if (openedWuHun.equals(wuHuns.get(wuHuns.size() - 1))) {
                    openedWuHun = "null";
                    player.sendMessage(Text.translatable("tips.closeWuhun"), true);
                } else {
                    openedWuHun = wuHuns.get(wuHuns.indexOf(openedWuHun) + 1);
                    player.sendMessage(Text.translatable("wuhun." + openedWuHun), true);
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

    public void addAlly(PlayerEntity player) {
        if (allys.containsKey(player.getUuid())) {
            MCDuro.LOGGER.info("Ally Manager -> Already added {}!", player.getName().getString());
        } else {
            allys.put(player.getUuid(), player.getName().getString());
            MCDuro.LOGGER.info("Ally Manager -> Successfully added {}!", player.getName().getString());
        }
    }

    public void delAlly(PlayerEntity player) {
        if (!allys.containsKey(player.getUuid())) {
            MCDuro.LOGGER.info("Ally Manager -> Don't have {} in your ally list!", player.getName().getString());
        } else {
            allys.remove(player.getUuid());
            MCDuro.LOGGER.info("Ally Manager -> Successfully removed {}!", player.getName().getString());
        }
    }

    public List<String> listOfAllys() {
        return new ArrayList<>(allys.values());
    }

    public void refreshAttacker(PlayerEntity player) {
        lastAttacker = player;
    }

    public void addStatusEffect(PlayerEntity player, String effect, List<Long> data) {
        if (!player.getWorld().isClient) {
            statusEffects.put(effect, data);
            System.out.println("Server -> Add status effect %s%s to player %s.".formatted(effect, data, player.getName().getString()));
            syncStatusEffects(player);
        }
    }

    public void removeStatusEffect(PlayerEntity player, String effect) {
        if (!player.getWorld().isClient) {
            statusEffects.remove(effect);
            syncStatusEffects(player);
        }
    }

    public void syncStatusEffects(PlayerEntity player) {
        if (!player.getWorld().isClient) {
            System.out.println("Server -> Original status effects: %s".formatted(statusEffects));

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(statusEffects.size());
            statusEffects.forEach((name, list) -> {
                buf.writeString(name);
                buf.writeLongArray(list.stream().mapToLong(Long::longValue).toArray());
            });
            MinecraftServer server = player.getServer();
            assert server != null;
            server.execute(() -> {
                ServerPlayNetworking.send((ServerPlayerEntity) player, ModEvents.SYNC_STATUS_EFFECTS, buf);
            });
        }
    }

    public void lockOn(PlayerEntity player) {
        if (!player.getWorld().isClient) {
            ServerWorld world = Objects.requireNonNull(player.getServer()).getOverworld();
            for (int i = 0; i < 32; i++) {
                Vec3d pos = player.getEyePos().add(player.getRotationVector().multiply(i));
                List<Entity> entities = world.getOtherEntities(player, new Box(pos.add(0.5, 0.5, 0.5), pos.subtract(0.5, 0.5, 0.5)));
                if (!entities.isEmpty()) {
                    System.out.println("Server -> Get entity: %s".formatted(entities.get(0).getUuidAsString()));
                    player.sendMessage(Text.of("Server -> Get entity: %s".formatted(entities.get(0).getUuidAsString())));
                    this.targets.add(entities.get(0));
                    break;
                }
            }
        } else {
            ClientPlayNetworking.send(ModEvents.LOCKED_PLAYER, PacketByteBufs.create());
        }
    }

    public void delNullTargets(ServerPlayerEntity serverPlayer) {
        List<Entity> entities = new ArrayList<>(this.targets);
        for (Entity entity : entities) {
            if (Objects.isNull(entity) || Objects.isNull(Objects.requireNonNull(serverPlayer.getServer()).getOverworld().getEntity(entity.getUuid()))){
                targets.remove(entity);
            }
        }
    }

    // TODO 02/09/2025 玩家使用魂技时魂环变化
    // TODO 02/09/2025 玩家使用技能切换 -> 魂环的显示和消失、HUD快捷栏显示
    // TODO 02/09/2025 玩家是用精神力锁定

}
