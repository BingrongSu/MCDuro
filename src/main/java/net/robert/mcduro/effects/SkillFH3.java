package net.robert.mcduro.effects;
//到此一游
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class SkillFH3 extends StatusEffect {
    public SkillFH3() {
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
