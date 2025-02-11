package net.robert.mcduro.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SkillFH7 extends StatusEffect {
    public SkillFH7() {
        super(StatusEffectCategory.BENEFICIAL, 0xff2222);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public boolean isInstant() {
        return false;
    }
}
