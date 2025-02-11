package net.robert.mcduro.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(KeyBinding.class)
public class KeyBindMixin {

    @Shadow private InputUtil.Key boundKey;

    @Inject(at = @At("HEAD"), method = "wasPressed", cancellable = true)
    public void wasPressed(CallbackInfoReturnable<Boolean> cir) {
//        if ()
    }


}
