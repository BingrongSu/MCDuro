package net.robert.mcduro.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.key.ModKeyBinds;
import net.robert.mcduro.math.Helper;
import net.robert.mcduro.player.PlayerData;

import java.util.*;

public class ModClientEvents {
    public static PlayerData playerData = new PlayerData();
    public static HashMap<UUID, List<Double>> showedYears = new HashMap<>();    // 所有进入过服务器的玩家当前要显示的魂环

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
            player.sendMessage(Text.of("Client: Max Hun Li set to: " + playerData.maxHunLi));
        });

        ClientPlayNetworking.registerGlobalReceiver(ModEvents.SET_HUN_LI, (client, handler, buf, sender) -> {
            playerData.hunLi = buf.readInt();
            PlayerEntity player = client.player;
            assert player != null;
            player.sendMessage(Text.of("Client: Hun Li set to: " + playerData.hunLi));
        });

        ClientPlayNetworking.registerGlobalReceiver(ModEvents.SET_HUN_LI_LEVEL, (client, handler, buf, sender) -> {
            playerData.hunLiLevel = buf.readInt();
            PlayerEntity player = client.player;
            assert player != null;
            player.sendMessage(Text.of("Client: Hun Li Level set to: " + playerData.hunLiLevel));
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
            if (InputUtil.isKeyPressed(client.getWindow().getHandle(), InputUtil.GLFW_KEY_J)) {
                System.out.println("Pressed Key J");
                assert client.player != null;
                assert client.world != null;
                Long tick = client.world.getTime();
                playerData.openWuHun(client.player, tick);
                System.out.println("Tick Order: " + tick);
            }
            while (ModKeyBinds.switchWuHunKeyBinding.wasPressed()) {
                assert client.world != null;
                System.out.println("Pressed Key Tab");
                assert client.player != null;
                assert client.world != null;
                playerData.switchWuHun(client.player);
                Long tick = client.world.getTime();
                playerData.openWuHun(client.player, tick);
                System.out.println("Tick Order: " + tick);
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

        HudRenderCallback.EVENT.register((context, v) -> {
            InGameHud hud = MinecraftClient.getInstance().inGameHud;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            Text text1 = Text.of("Level: " + Helper.hunLi2level(playerData.maxHunLi));
            int x = 5;
            int y = context.getScaledWindowHeight() - 10;
            context.drawText(textRenderer, text1, x, y, 0xff1111, true);
        });
    }
}
