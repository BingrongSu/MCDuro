package net.robert.mcduro.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SkillFH2 extends StatusEffect {
    public SkillFH2() {
        super(StatusEffectCategory.BENEFICIAL, 0xff1010);
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
