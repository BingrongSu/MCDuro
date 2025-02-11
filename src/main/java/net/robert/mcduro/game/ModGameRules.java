package net.robert.mcduro.game;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import net.robert.mcduro.MCDuro;

public class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanRule> LIMIT_SOUL_HOSTILE_SPAWN =
            GameRuleRegistry.register("limitSoulHostileSpawn", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(true));
    public static void registerGameRules() {
        MCDuro.LOGGER.info("Registering Mod Game Rules.");
    }
}
