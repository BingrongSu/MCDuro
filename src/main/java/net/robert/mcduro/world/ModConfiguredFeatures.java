package net.robert.mcduro.world;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.block.ModBlocks;

import java.util.List;

public class ModConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> SHEN_SILVER_ORE_KEY = registerKey("shen_silver_ore");
    public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_SHEN_SILVER_ORE_KEY = registerKey("nether_shen_silver_ore");

    public static void boostrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplacables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplacables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplacables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);

        List<OreFeatureConfig.Target> overworldShenSilverOres =
                List.of(OreFeatureConfig.createTarget(stoneReplacables, ModBlocks.SHEN_SILVER_ORE.getDefaultState()),
                        OreFeatureConfig.createTarget(deepslateReplacables, ModBlocks.DEEPSLATE_SHEN_SILVER_ORE.getDefaultState()));

        List<OreFeatureConfig.Target> netherShenSilverOres =
                List.of(OreFeatureConfig.createTarget(netherReplacables, ModBlocks.NETHER_SHEN_SILVER_ORE.getDefaultState()));

        register(context, SHEN_SILVER_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldShenSilverOres, 4));
        register(context, NETHER_SHEN_SILVER_ORE_KEY, Feature.ORE, new OreFeatureConfig(netherShenSilverOres, 6));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, new Identifier(MCDuro.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
