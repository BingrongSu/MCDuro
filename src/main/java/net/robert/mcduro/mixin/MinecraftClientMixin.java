package net.robert.mcduro.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void onHandleHotbarInput(CallbackInfo ci) {
        ci.cancel();                         // 取消原版输入处理
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null) return;

        // 遍历检测 1-9 按键
        for (int i = 0; i < 9; i++) {
            KeyBinding key = client.options.hotbarKeys[i];
            if (key.wasPressed()) {
                // 自定义条件判断（示例：手持钻石时拦截）
                if (player.getMainHandStack().isOf(Items.DIAMOND)) {
                    performCustomAction(player, i + 1);  // 执行自定义操作
                    key.setPressed(false);                // 重置按键状态
                    ci.cancel();                         // 取消原版输入处理
                    return;
                }
            }
        }
    }

    private void performCustomAction(ClientPlayerEntity player, int slot) {
        player.sendMessage(Text.literal("自定义操作: 槽位 " + slot), true);
        // 在此添加自定义逻辑（如发送网络包、触发事件等）
    }
}
