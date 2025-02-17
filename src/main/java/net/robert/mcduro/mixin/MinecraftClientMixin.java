package net.robert.mcduro.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(
            method = "handleInputEvents",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z",
                    ordinal = 0 // 根据实际反编译结果调整 ordinal 值
            ),
            locals = LocalCapture.CAPTURE_FAILHARD, // 强制捕获局部变量
            cancellable = true
    )
    private void onHotbarKeyCheck(
            CallbackInfo ci,
            @Local(index = 21) int i // 根据反编译结果调整局部变量索引
    ) {
        MinecraftClient client = (MinecraftClient) (Object) this;
        ClientPlayerEntity player = client.player;

        // 条件检查：是否拦截快捷栏操作
        if (player != null && shouldOverrideHotbar(player)) {
            // 执行自定义操作
            performCustomAction(player, i + 1);

            // 重置按键状态并取消原版逻辑
            client.options.hotbarKeys[i].setPressed(false);
            ci.cancel(); // 阻止原版代码继续执行（跳过本次循环内的处理）
        }
    }

    // 条件判断（示例：手持钻石时拦截）
    private boolean shouldOverrideHotbar(ClientPlayerEntity player) {
        return player.getMainHandStack().isOf(Items.DIAMOND);
    }

    // 自定义操作（如发送消息、同步到服务端等）
    private void performCustomAction(ClientPlayerEntity player, int slot) {
        player.sendMessage(Text.literal("拦截快捷栏按键: 槽位 " + slot), true);
        // 若需服务端同步，在此发送网络包
    }
}