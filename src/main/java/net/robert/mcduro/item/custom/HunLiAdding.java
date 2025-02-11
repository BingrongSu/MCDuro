package net.robert.mcduro.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

public class HunLiAdding extends Item {
    private final Integer amount;

    public HunLiAdding(Settings settings, Integer amount) {
        super(settings);
        this.amount = amount;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient){
            PlayerData playerData = StateSaverAndLoader.getPlayerState((PlayerEntity) user);
            playerData.increaseHunLi(amount, (PlayerEntity) user);
        }
        return super.finishUsing(stack, world, user);
    }
}
