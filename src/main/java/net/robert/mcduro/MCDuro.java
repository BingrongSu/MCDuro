package net.robert.mcduro;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.block.entity.ModBlockEntities;
import net.robert.mcduro.events.ModEvents;
import net.robert.mcduro.events.ModServerEvents;
import net.robert.mcduro.item.ModItemGroup;
import net.robert.mcduro.item.ModItems;
import net.robert.mcduro.key.ModKeyBinds;
import net.robert.mcduro.math.Helper;
import net.robert.mcduro.recipe.ModRecipes;
import net.robert.mcduro.screen.ModScreenHandlers;
import net.robert.mcduro.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCDuro implements ModInitializer {
	public static final String MOD_ID = "mcduro";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Map<Runnable, Long> tasks = new HashMap<>();

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

		ModKeyBinds.registerKeyBinds();

		ModBlockEntities.registerBlockEntities();

		ModWorldGeneration.generateModWorldGen();


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
}