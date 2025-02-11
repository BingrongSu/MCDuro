package net.robert.mcduro.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class GainSoulRingCriterion extends AbstractCriterion<GainSoulRingCriterion.Conditions> {

    @Override
    protected Conditions conditionsFromJson(JsonObject obj,
                                            Optional<LootContextPredicate> predicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        Conditions conditions = new Conditions();
        return conditions;
    }

    public static class Conditions extends AbstractCriterionConditions {

        public Conditions() {
            super(Optional.empty());
        }

        boolean requirementsMet() {
            return true;
        }
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, Conditions::requirementsMet);
    }
}
