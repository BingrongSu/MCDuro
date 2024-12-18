package net.robert.mcduro.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

public class WuHunAdding extends Item {
    private final String wuhun;

    public WuHunAdding(Settings settings, String wuhun) {
        super(settings);
        this.wuhun = wuhun;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient){
            PlayerData playerData = StateSaverAndLoader.getPlayerState((PlayerEntity) user);
            if (!playerData.wuHun.containsKey(wuhun)) {
                playerData.addWuHun((PlayerEntity) user, wuhun);
                user.sendMessage(Text.of("Server-> Consume WuHunAdding: " + wuhun));
            } else {
                user.sendMessage(Text.of("Server-> Can't consume because you've already had that Wu Hun!"));
            }
        }
        return super.finishUsing(stack, world, user);
    }
}
