package net.robert.mcduro.item.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.robert.mcduro.player.PlayerData;
import net.robert.mcduro.player.StateSaverAndLoader;

public class RingAdding extends Item {
    private final Integer year;

    public RingAdding(Settings settings, Integer year) {
        super(settings);
        this.year = year;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient){
            PlayerData playerData = StateSaverAndLoader.getPlayerState((PlayerEntity) user);
            if (!playerData.openedWuHun.equals("null")) {
                playerData.addRing((PlayerEntity) user, this.year);
                user.sendMessage(Text.of("Server-> Consume RingAdding: " + this.year));
            } else {
                user.sendMessage(Text.of("Server-> No opened Wu Hun!"));
            }
        }
        return super.finishUsing(stack, world, user);
    }
}
