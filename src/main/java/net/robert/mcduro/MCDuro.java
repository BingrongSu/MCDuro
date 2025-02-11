package net.robert.mcduro;

import net.fabricmc.api.ModInitializer;


import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.robert.mcduro.advancements.GainSoulRingCriterion;
import net.robert.mcduro.advancements.GainWuhunCriterion;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.block.entity.ModBlockEntities;
import net.robert.mcduro.effects.ModEffects;
import net.robert.mcduro.events.ModEvents;
import net.robert.mcduro.events.ModServerEvents;
import net.robert.mcduro.game.ModGameRules;
import net.robert.mcduro.item.ModItemGroup;
import net.robert.mcduro.item.ModItems;
import net.robert.mcduro.key.ModKeyBinds;
import net.robert.mcduro.math.Helper;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;
import net.robert.mcduro.recipe.ModRecipes;
import net.robert.mcduro.screen.ModScreenHandlers;
import net.robert.mcduro.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MCDuro implements ModInitializer {
	public static final String MOD_ID = "mcduro";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Map<Runnable, Long> tasks = new HashMap<>();

	public static GainWuhunCriterion GAIN_WUHUN = Criteria.register(MOD_ID + "/gain_wuhun", new GainWuhunCriterion());
	public static GainWuhunCriterion GAIN_WUHUN_FH = Criteria.register(MOD_ID + "/gain_wuhun_fh", new GainWuhunCriterion());
	public static GainWuhunCriterion GET_STUCK_CRITERION = Criteria.register(MOD_ID + "/get_stuck", new GainWuhunCriterion());
	public static GainSoulRingCriterion GET_RING_TEN_CRI = Criteria.register(MOD_ID + "/get_ring_ten", new GainSoulRingCriterion());
	public static GainSoulRingCriterion GET_RING_HUD_CRI = Criteria.register(MOD_ID + "/get_ring_hud", new GainSoulRingCriterion());
	public static GainSoulRingCriterion GET_RING_THD_CRI = Criteria.register(MOD_ID + "/get_ring_thd", new GainSoulRingCriterion());
	public static GainSoulRingCriterion GET_RING_TTD_CRI = Criteria.register(MOD_ID + "/get_ring_ttd", new GainSoulRingCriterion());
	public static GainSoulRingCriterion GET_RING_HTD_CRI = Criteria.register(MOD_ID + "/get_ring_htd", new GainSoulRingCriterion());


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Welcome to MC Duro world!");
		Helper.initialize();

		ModServerEvents.registerModServerEvents();
		ModEvents.registerModEvents();

		ModScreenHandlers.registerScreenHandlers();
		ModRecipes.registerRecipes();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModItemGroup.registerModItemGroup();


		ModBlockEntities.registerBlockEntities();
		ModEffects.registerModEffects();

		ModWorldGeneration.generateModWorldGen();
		ModGameRules.registerGameRules();

		registerCommands();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			List<Runnable> delete = new ArrayList<>();
			tasks.forEach((task, tickRemain) -> {
				if (--tickRemain <= 0) {
					task.run();
					delete.add(task);
				} else {
					tasks.replace(task, tickRemain);
				}
			});
			delete.forEach((task) -> tasks.remove(task));
		});
	}

	public static void scheduledTask(Runnable task, Long delayTicks) {
		tasks.put(task, delayTicks);
	}

	public static void registerCommands() {
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("ally")
						.then(CommandManager.literal("add")
								.then(CommandManager.argument("player", EntityArgumentType.player())
								.executes(context -> {
									final PlayerEntity ally = EntityArgumentType.getPlayer(context, "player");
									PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
									playerData.addAlly(ally);
									context.getSource().sendFeedback(() -> Text.literal("Add player %s as your ally.".formatted(ally.getName().getString())), true);
									return 1;
								})))
						.then(CommandManager.literal("del")
								.then(CommandManager.argument("player", EntityArgumentType.player())
								.executes(context -> {
									final PlayerEntity ally = EntityArgumentType.getPlayer(context, "player");
									PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
									playerData.delAlly(ally);
									context.getSource().sendFeedback(() -> Text.literal("Delete player %s from your ally list.".formatted(ally.getName().getString())), true);
									return 2;
								})))
						.then(CommandManager.literal("delAll")
								.executes(context -> {
									PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
									playerData.allys.clear();
									context.getSource().sendFeedback(() -> Text.literal("Delete all players in your ally list."), true);
									return 3;
								}))
						.then(CommandManager.literal("delAttacker")
								.executes(context -> {
									PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
									if (playerData.lastAttacker != null) {
										playerData.allys.remove(playerData.lastAttacker.getUuid());
									}
									context.getSource().sendFeedback(() -> Text.literal("Delete the attacker(%s) from your ally list.".formatted(playerData.lastAttacker.getName().getString())), true);
									return 4;
								}))
						.then(CommandManager.literal("addAll")
								.executes(context -> {
									PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
									for (PlayerEntity player : context.getSource().getWorld().getPlayers()) {
										context.getSource().sendFeedback(() -> Text.literal("Add ally: %s".formatted(player.getName().getString())), true);
										playerData.addAlly(player);
									}
									return 5;
								}))
						.then(CommandManager.literal("list")
								.executes(context -> {
									PlayerData playerData = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(context.getSource().getPlayer()));
									context.getSource().sendFeedback(() -> Text.literal("Your allys: "), false);
									for (String name : playerData.listOfAllys()) {
										context.getSource().sendFeedback(() -> Text.literal("  - %s".formatted(name)), false);
									}
									return 6;
								}))
				)));
	}
}
