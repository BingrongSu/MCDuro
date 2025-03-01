package net.robert.mcduro.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.robert.mcduro.entity.custom.SkillFH5Ball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Unique
    private float damage = 0f;

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyAmount(float amount) {
        if (amount == -11.21f) {
            float ans = damage;
            System.out.printf("释放所有累加伤害：%f.2%n", damage);
            damage = 0;
            return ans;
        } else {
            return amount;
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (amount < 0) {
            this.damage -= amount;
            System.out.printf("累加伤害：%.2f\n", -amount);
            cir.cancel();
        }
    }
}
