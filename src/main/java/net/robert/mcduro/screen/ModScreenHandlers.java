package net.robert.mcduro.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;

public class ModScreenHandlers {
    public static final ScreenHandlerType<SmithSmeltingScreenHandler> SMITH_SMELTING_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(MCDuro.MOD_ID, "smith_smelting"),
                    new ExtendedScreenHandlerType<>(SmithSmeltingScreenHandler::new));

    public static void registerScreenHandlers() {
        MCDuro.LOGGER.info("Registering screen handlers for " + MCDuro.MOD_ID);
    }
}
