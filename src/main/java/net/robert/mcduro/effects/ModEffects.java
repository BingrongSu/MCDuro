package net.robert.mcduro.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;

import java.util.ArrayList;
import java.util.List;

public class ModEffects {

    public static final StatusEffect SkillFH2 = Registry.register(Registries.STATUS_EFFECT, new Identifier(MCDuro.MOD_ID, "skill_fh2"), new SkillFH2());
    public static final StatusEffect SkillFH3 = Registry.register(Registries.STATUS_EFFECT, new Identifier(MCDuro.MOD_ID, "skill_fh3"), new SkillFH3());
    public static final StatusEffect SkillFH7 = Registry.register(Registries.STATUS_EFFECT, new Identifier(MCDuro.MOD_ID, "skill_fh7"), new SkillFH7());
    public static final StatusEffect SkillFH8 = Registry.register(Registries.STATUS_EFFECT, new Identifier(MCDuro.MOD_ID, "skill_fh8"), new SkillFH8());

    public static final List<String> selfEnhancing = new ArrayList<>(List.of(
            "FHSkill2",
            "FHSkill3",
            "FHSkill7",
            "XCSkill7"
            ));
    public static final List<String> allEnhancing = new ArrayList<>(List.of(
            "XCSkill1",
            "XCSkill2",
            "XCSkill3",
            "XCSkill4",
            "XCSkill5",
            "XCSkill8",
            "XCSkill9"
    ));

    public static void registerModEffects() {
        MCDuro.LOGGER.info("Registering Mod Effects");
    }
}
