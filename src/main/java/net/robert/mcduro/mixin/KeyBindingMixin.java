package net.robert.mcduro.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.robert.mcduro.events.ModClientEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {
    @Inject(at = @At("HEAD"), method = "onKeyPressed", cancellable = true)
    private static void onKeyPressed(InputUtil.Key key, CallbackInfo ci) {
        if (key.getCode() >= InputUtil.GLFW_KEY_1 && key.getCode() <= InputUtil.GLFW_KEY_9
                && !ModClientEvents.playerData.openedWuHun.equals("null")) {
            ci.cancel();
        }
    }
}