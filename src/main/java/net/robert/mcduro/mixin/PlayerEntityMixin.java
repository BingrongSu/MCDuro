package net.robert.mcduro.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Shadow
    private GameProfile gameProfile = this.getGameProfile();

    @Shadow public abstract GameProfile getGameProfile();

    @Inject(at = @At("HEAD"), method = "damage", cancellable = true)
    public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity attacker) {
            PlayerData playerData1 = StateSaverAndLoader.getPlayerState(attacker);
            if (playerData1.allys.containsKey(gameProfile.getId())) {
                cir.cancel();
            } else {
                PlayerData playerData2 = StateSaverAndLoader.getPlayerState(Objects.requireNonNull(attacker.getWorld().getPlayerByUuid(gameProfile.getId())));
                playerData2.refreshAttacker(attacker);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onDeath", cancellable = true)
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        World world = Objects.requireNonNull(damageSource.getAttacker()).getWorld();
        if (!world.isClient) {
            PlayerEntity player = Objects.requireNonNull(world.getPlayerByUuid(gameProfile.getId()));
            PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
            playerData.clearStatusEffect(player);
            System.out.println("Server -> Clear player status");
        }
    }
}
