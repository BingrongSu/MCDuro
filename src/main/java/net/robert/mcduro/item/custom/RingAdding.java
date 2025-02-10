package net.robert.mcduro.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.robert.mcduro.math.Helper;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

public class RingAdding extends Item {
    private final Integer level;

    public RingAdding(Settings settings, Integer level) {
        super(settings);
        this.level = level;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient){
            PlayerData playerData = StateSaverAndLoader.getPlayerState((PlayerEntity) user);
            if (!playerData.openedWuHun.equals("null")) {
                double max = Math.pow(10, level + 1);
                double min = Math.pow(10, level);
                int year = Helper.gaussianRandom(world.getTime(), (min + max) / 2d, (min - max) / 2d, min, max - 1);
                playerData.addRing((PlayerEntity) user, year);
                user.sendMessage(Text.of("Server-> Consume RingAdding: " + year));
            } else {
                user.sendMessage(Text.of("Server-> No opened Wu Hun!"));
            }
        }
        return super.finishUsing(stack, world, user);
    }
}
