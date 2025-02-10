package net.robert.mcduro.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.key.ModKeyBinds;
import net.robert.mcduro.math.Helper;
import net.robert.mcduro.player.PlayerData;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class ModClientEvents {
    public static PlayerData playerData = new PlayerData();
    public static HashMap<UUID, List<Double>> showedYears = new HashMap<>();    // 所有进入过服务器的玩家当前要显示的魂环
    public static List<Double> chargeVal = List
            .of(0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d, 0d);                            // 玩家使用魂技的蓄力值
    private static final double chargeV = 1 / 80d;                              // 蓄力速率
    private static final double thresholdVal = 4 * chargeV;                     // 长按的阀值
    private static HashMap<UUID, Integer> mobsYear = new HashMap<>();           // 生物年限
    public static Boolean readyForSkill = false;                                // 玩家是否处于准备释放魂技的状态

    public static void registerModClientEvents() {
        MCDuro.LOGGER.info("Registering Mod Client Events");
        registerEvents();
    }

    private static void registerEvents() {
        ClientPlayNetworking.registerGlobalReceiver(ModEvents.INIT_SYNC, (client, handler, buf, sender) -> {
            playerData.hunLi = buf.readInt();
            playerData.maxHunLi = buf.readInt();
            playerData.hunLiLevel = buf.readInt();
            client.execute(() -> {
                assert client.player != null;
                client.player.sendMessage(Text.of("Client initialized player Hun Li: " + playerData.hunLi));
                client.player.sendMessage(Text.of("Client initialized player Max Hun Li: " + playerData.maxHunLi));
                client.player.sendMessage(Text.of("Client initialized player Hun Li Level: " + playerData.hunLiLevel));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModEvents.SET_MAX_HUN_LI, (client, handler, buf, sender) -> {
            playerData.maxHunLi = buf.readInt();
            PlayerEntity player = client.player;
            assert player != null;
            System.out.println("Client: Max Hun Li set to: " + playerData.maxHunLi);
        });

        ClientPlayNetworking.registerGlobalReceiver(ModEvents.SET_HUN_LI, (client, handler, buf, sender) -> {
            playerData.hunLi = buf.readInt();
            PlayerEntity player = client.player;
            assert player != null;
            System.out.println("Client: Hun Li set to: " + playerData.hunLi);
        });

        ClientPlayNetworking.registerGlobalReceiver(ModEvents.SET_HUN_LI_LEVEL, (client, handler, buf, sender) -> {
            playerData.hunLiLevel = buf.readInt();
            PlayerEntity player = client.player;
            assert player != null;
            System.out.println("Client: Hun Li Level set to: " + playerData.hunLiLevel);
        });

        ClientPlayNetworking.registerGlobalReceiver(ModEvents.SET_WU_HUN, (client, handler, buf, sender) -> {
            PlayerEntity player = client.player;

            playerData.wuHun.clear();
            assert player != null;
//            player.sendMessage(Text.of("Client-> Wu Hun set to: ==============================="));

            int wuHunSize = buf.readInt();
            for (int i = 0; i < wuHunSize; i++) {
                List<List<Double>> years = new ArrayList<>();
                String name = buf.readString();
//                player.sendMessage(Text.of("Client-> Wu Hun " + name + "  -------"));
                int yearsSize = buf.readInt();
                for (int j = 0; j < yearsSize; j++) {
                    List<Double> yearData = new ArrayList<>();
                    double year = buf.readDouble();
                    double value1 = buf.readDouble();
//                    player.sendMessage(Text.of("Client-> year: " + year));
//                    player.sendMessage(Text.of("Client-> value1: " + value1));
                    yearData.add(year);
                    yearData.add(value1);
                    years.add(yearData);
                }
                playerData.wuHun.put(name, years);
            }
            System.out.println("Client-> playerData.wuHun");
            System.out.println(playerData.wuHun);

        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            while (ModKeyBinds.switchWuHunKeyBinding.wasPressed()) {
                assert client.world != null;
                System.out.println("Pressed " + ModKeyBinds.switchWuHunKeyBinding.getBoundKeyTranslationKey());
                System.out.println("Pressed Key Tab");
                assert client.player != null;
                assert client.world != null;
                playerData.switchWuHun(client.player);
                Long tick = client.world.getTime();
                playerData.openWuHun(client.player, tick);
                System.out.println("Tick Order: " + tick);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ModKeyBinds.useSoulSkillKeyBinding.wasPressed() && !playerData.openedWuHun.equals("null")) {
                readyForSkill = !readyForSkill;
                System.out.println("Client -> readyForSkill => " + readyForSkill);
            }
            if (readyForSkill && playerData.openedWuHun.equals("null")) {
                readyForSkill = false;
                System.out.println("Client -> readyForSkill => false");
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
//            if (ModKeyBinds.useSoulSkillKeyBinding.isPressed()) {
//                chargeVal += chargeVal < 1 ? chargeV : 0;
//                System.out.println("Is pressing : " + chargeVal);
//            } else {
//                if (chargeVal > 0) {
//                    // use soul skill
//                    System.out.println("---------------------Charge Value : " + chargeVal);
//                    chargeVal = 0;
//                }
//            }
            if (!playerData.openedWuHun.equals("null") && readyForSkill) {
                List<List<Double>> wuHunData = playerData.wuHun.get(playerData.openedWuHun);
                for (int i = 0; i < wuHunData.size(); i++) {
                    boolean isPressed = GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_1 + i) == GLFW.GLFW_PRESS;
                    if (isPressed) {
//                            chargeVal[i] += chargeVal < 1 ? chargeV : 0;
//                        chargeVal.set(i, chargeVal.get(i) + (chargeVal.get(i) < 1 ? chargeV : 0));
                        wuHunData.get(i).set(1, wuHunData.get(i).get(1) + (wuHunData.get(i).get(1) < 1 + thresholdVal ? chargeV : 0));
                        System.out.println("Client -> Key " + (i + 1) + " is pressed. Of value: " + wuHunData.get(i).get(1));
                    } else {
                        if (wuHunData.get(i).get(1) > 0) {
                            if (wuHunData.get(i).get(1) > thresholdVal) {
                                // use soul skill
                                double chargeValue = wuHunData.get(i).get(1) - thresholdVal;
                                System.out.println("Key " + (i+1) + " ---------------------Charge Value : " + chargeValue);
                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeString(playerData.openedWuHun);
                                buf.writeInt(i);
                                buf.writeDouble(chargeValue);
                                ClientPlayNetworking.send(ModEvents.USE_SOUL_SKILL, buf);
                            }
                            wuHunData.get(i).set(1, 0d);
                        }
                    }
                }
            } else {
                playerData.wuHun.forEach((key, value) -> value.forEach(list -> list.set(1, 0d)));   // 将所有魂技的蓄力值清零
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ModEvents.SYNC_SHOWED_YEARS, (client, handler, buf, sender) -> {
            UUID uuid = buf.readUuid();
            playerData.openWuHunTicks.put(uuid, buf.readLong());
            int n = buf.readInt();
            List<Double> years = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                years.add(buf.readDouble());
            }
            showedYears.put(uuid, years);
            PlayerEntity player = client.player;
            assert player != null;
//            player.sendMessage(Text.of("Client-> Refresh Player's Showed Years: \n" + player.getName().getString() + ": " + years));
        });

        ClientPlayNetworking.registerGlobalReceiver(ModEvents.GET_MOB_YEAR, ((client, handler, buf, sender) -> {
            UUID uuid = buf.readUuid();
            int year = buf.readInt();
            if (year >= 0) {
                mobsYear.put(uuid, year);
            } else {
                System.out.println("Client -> Unable to find this entity in Server's mapping: " + uuid);
            }
        }));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                if (client.world.getTime() % 200 == 0) {
                    Set<UUID> deleteMobs = new HashSet<>(mobsYear.keySet());
                    Iterable<Entity> allEntities = client.world.getEntities();
                    for (Entity entity : allEntities) {
                        deleteMobs.remove(entity.getUuid());
                    }
                    for (UUID uuid : deleteMobs) {
                        mobsYear.remove(uuid);
                        MCDuro.LOGGER.info("Client -> Removed mob({}) from client's mapping.", uuid);
                    }
                }
            }
        });



        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            InGameHud hud = client.inGameHud;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            Text text1 = Text.of("Level: " + Helper.hunLi2level(playerData.maxHunLi));
            int x = 5;
            int y = context.getScaledWindowHeight() - 10;
            context.drawText(textRenderer, text1, x, y, 0xff1111, true);

            HitResult hitResult = client.crosshairTarget;
            assert hitResult != null;
            if (hitResult.getType().equals(HitResult.Type.ENTITY)) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                    float health = livingEntity.getHealth();
                    UUID uuid = livingEntity.getUuid();
                    Text text01 = Text.of("Name: " + livingEntity.getName().getString() + "   UUID: " + uuid);
                    Text text02 = Text.of("Health: " + health);
                    y = 5;
                    context.drawText(textRenderer, text01, x, y, 0xff1111, true);
                    y += 10;
                    context.drawText(textRenderer, text02, x, y, 0xff1111, true);
                    if (livingEntity instanceof HostileEntity) {
                        int year = -1;
                        if (mobsYear.containsKey(uuid)) {
                            year = mobsYear.get(uuid);
                        } else {
                            PacketByteBuf buf = PacketByteBufs.create();
                            buf.writeUuid(uuid);
                            ClientPlayNetworking.send(ModEvents.GET_MOB_YEAR, buf);
                            MCDuro.LOGGER.info("Client -> Sent request to Server for this mob's year: {}", uuid.toString());
                        }
                        Text text03 = Text.of("Year: " + year);
                        y += 10;
                        context.drawText(textRenderer, text03, x, y, 0xff1111, true);
                    }
                }
            }
        });
        // TODO 01/11/2025 魂技蓄力条：仿照马跳跃时的蓄力显示，更改颜色
        // TODO 01/11/2025 魂技蓄力时间：根据修为-修为越高时间越短
    }
}
