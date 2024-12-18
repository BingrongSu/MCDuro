package net.robert.mcduro.events;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

public class ModEvents {
    public static Identifier SYNC_SHOWED_YEARS =  new Identifier(MCDuro.MOD_ID, "events.sync_showed_years");
    public static Identifier INIT_SYNC = new Identifier(MCDuro.MOD_ID, "events.initial_sync");
    public static Identifier SET_HUN_LI = new Identifier(MCDuro.MOD_ID, "events.set_hun_li");
    public static Identifier SET_HUN_LI_LEVEL = new Identifier(MCDuro.MOD_ID, "events.set_hun_li_level");
    public static Identifier SET_MAX_HUN_LI = new Identifier(MCDuro.MOD_ID, "events.set_max_hun_li");
    public static Identifier SET_WU_HUN = new Identifier(MCDuro.MOD_ID, "events.set_wu_hun");
    public static Identifier SET_OPENED_WU_HUN = new Identifier(MCDuro.MOD_ID, "events.set_opened_wu_hun");
    public static Identifier OPEN_WU_HUN = new Identifier(MCDuro.MOD_ID, "events.open_wu_hun");


    public static void registerModEvents() {
        AttackBlockCallback.EVENT.register((player, world, hand, blockPos, direction) -> {
            if (!world.isClient) {
                if (world.getBlockState(blockPos).isOf(Blocks.GRASS_BLOCK)) {
                    PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
                }
            }
            return ActionResult.PASS;
        });
    }
}
