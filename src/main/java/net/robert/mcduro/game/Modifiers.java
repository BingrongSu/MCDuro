package net.robert.mcduro.game;

import net.minecraft.entity.LivingEntity;

import java.lang.reflect.Field;

public class Modifiers {
    public static void modModifyMobHealth(LivingEntity livingEntity) {
        try {
            Field healthField = LivingEntity.class.getDeclaredField("");
            healthField.setAccessible(true);

            healthField.setFloat(livingEntity, 2000);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
