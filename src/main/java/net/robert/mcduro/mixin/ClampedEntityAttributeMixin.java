package net.robert.mcduro.mixin;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClampedEntityAttribute.class)
public abstract class ClampedEntityAttributeMixin {
    @Mutable
    @Final
    @Shadow
    private final double minValue;

    protected ClampedEntityAttributeMixin(double minValue) {
        this.minValue = minValue;
    }

    @Inject(at = @At("HEAD"), method = "clamp", cancellable = true)
    public void clamp(double value, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(Double.isNaN(value) ?  this.minValue: MathHelper.clamp(value, this.minValue, Double.MAX_VALUE));
        // Modified the maximum value of living entities' max health to very large.
    }
}
