package net.robert.mcduro.advancements;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.block.ModBlocks;
import net.robert.mcduro.item.ModItems;

import java.util.function.Consumer;

public class ModAdvancements implements Consumer<Consumer<AdvancementEntry>> {
    @Override
    public void accept(Consumer<AdvancementEntry> consumer) {
        AdvancementEntry gainWuhunAdvancement = Advancement.Builder.create()
                .display(
                        ModBlocks.JX_BALL, // 显示的图标
                        Text.translatable("advancements.jueXing.title").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), // 标题
                        Text.translatable("advancements.jueXing.description").setStyle(Style.EMPTY.withColor(Formatting.AQUA)), // 描述
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
                        AdvancementFrame.TASK, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("gain_wuhun", MCDuro.GAIN_WUHUN.create(new GainWuhunCriterion.Conditions()))     // TODO 02/10/2025 给予魂师修炼手册
                .build(consumer, MCDuro.MOD_ID + "/jue_xing");

//        AdvancementEntry gainWuhunLLAdvancement = Advancement.Builder.create().parent(gainWuhunAdvancement)
//                .display(
//                        ModItems.WU_HUN_LIU_LI, // 显示的图标
//                        Text.literal("顶级武魂-宝塔！").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), // 标题
//                        Text.literal("觉醒武魂 九宝琉璃塔").setStyle(Style.EMPTY.withColor(Formatting.GREEN)), // 描述
//                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
//                        AdvancementFrame.GOAL, // 选项: TASK, CHALLENGE, GOAL
//                        true, // 在右上角显示
//                        true, // 在聊天框中提示
//                        false // 在进度页面里隐藏
//                )
//                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
//                .criterion("gain_wuhun_ll", MCDuro.GAIN_WUHUN_LL.create(new GainWuhunCriterion.Conditions()))
//                .build(consumer, MCDuro.MOD_ID + "/gain_ll");
//
//        AdvancementEntry gainWuhunXCAdvancement = Advancement.Builder.create().parent(gainWuhunAdvancement)
//                .display(
//                        ModItems.WUHUN_ADDING_XC, // 显示的图标
//                        Text.literal("顶级武魂-香肠！").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), // 标题
//                        Text.literal("觉醒武魂 香肠").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)), // 描述
//                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
//                        AdvancementFrame.GOAL, // 选项: TASK, CHALLENGE, GOAL
//                        true, // 在右上角显示
//                        true, // 在聊天框中提示
//                        false // 在进度页面里隐藏
//                )
//                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
//                .criterion("gain_wuhun_xc", MCDuro.GAIN_WUHUN_XC.create(new GainWuhunCriterion.Conditions()))
//                .build(consumer, MCDuro.MOD_ID + "/gain_xc");

        AdvancementEntry gainWuhunFHAdvancement = Advancement.Builder.create().parent(gainWuhunAdvancement)
                .display(
                        ModItems.WU_HUN_FENG_HUANG, // 显示的图标
                        Text.translatable("advancements.wuHun_fengHuang.title").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), // 标题
                        Text.translatable("advancements.wuHun_fengHuang.description").setStyle(Style.EMPTY.withColor(Formatting.RED)), // 描述
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
                        AdvancementFrame.GOAL, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("gain_wuhun_fh", MCDuro.GAIN_WUHUN_FH.create(new GainWuhunCriterion.Conditions()))
                .build(consumer, MCDuro.MOD_ID + "/gain_fh");

        AdvancementEntry getStuckAdvancement = Advancement.Builder.create().parent(gainWuhunAdvancement)
                .display(
                        ModItems.MAX_HUNLI_PILL_L1, // 显示的图标
                        Text.translatable("advancements.get_stuck.title").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), // 标题
                        Text.translatable("advancements.get_stuck.description").setStyle(Style.EMPTY.withColor(Formatting.RED)), // 描述
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
                        AdvancementFrame.TASK, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("get_stuck", MCDuro.GET_STUCK_CRITERION.create(new GainWuhunCriterion.Conditions()))
                .build(consumer, MCDuro.MOD_ID + "/get_stuck");

        AdvancementEntry getSoulRingTenAdvancement = Advancement.Builder.create().parent(getStuckAdvancement)
                .display(
                        ModItems.SOUL_RING_TEN, // 显示的图标
                        Text.translatable("advancements.get_ring_ten.title").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), // 标题
                        Text.translatable("advancements.get_ring_ten.description").setStyle(Style.EMPTY.withColor(Formatting.WHITE)), // 描述
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
                        AdvancementFrame.CHALLENGE, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("get_ring_ten", MCDuro.GET_RING_TEN_CRI.create(new GainSoulRingCriterion.Conditions()))
                .build(consumer, MCDuro.MOD_ID + "/get_ring_ten");
        AdvancementEntry getSoulRingHudAdvancement = Advancement.Builder.create().parent(getStuckAdvancement)
                .display(
                        ModItems.SOUL_RING_HUD, // 显示的图标
                        Text.translatable("advancements.get_ring_hud.title").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), // 标题
                        Text.translatable("advancements.get_ring_hud.description").setStyle(Style.EMPTY.withColor(Formatting.YELLOW)), // 描述
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
                        AdvancementFrame.CHALLENGE, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("get_ring_hud", MCDuro.GET_RING_HUD_CRI.create(new GainSoulRingCriterion.Conditions()))
                .build(consumer, MCDuro.MOD_ID + "/get_ring_hud");
        AdvancementEntry getSoulRingThdAdvancement = Advancement.Builder.create().parent(getStuckAdvancement)
                .display(
                        ModItems.SOUL_RING_THD, // 显示的图标
                        Text.translatable("advancements.get_ring_thd.title").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)), // 标题
                        Text.translatable("advancements.get_ring_thd.description").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)), // 描述
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
                        AdvancementFrame.CHALLENGE, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("get_ring_thd", MCDuro.GET_RING_THD_CRI.create(new GainSoulRingCriterion.Conditions()))
                .build(consumer, MCDuro.MOD_ID + "/get_ring_thd");
        AdvancementEntry getSoulRingTtdAdvancement = Advancement.Builder.create().parent(getStuckAdvancement)
                .display(
                        ModItems.SOUL_RING_TTD, // 显示的图标
                        Text.translatable("advancements.get_ring_ttd.title").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)), // 标题
                        Text.translatable("advancements.get_ring_ttd.description").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)), // 描述
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
                        AdvancementFrame.CHALLENGE, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("get_ring_ttd", MCDuro.GET_RING_TTD_CRI.create(new GainSoulRingCriterion.Conditions()))
                .build(consumer, MCDuro.MOD_ID + "/get_ring_ttd");
        AdvancementEntry getSoulRingHtdAdvancement = Advancement.Builder.create().parent(getStuckAdvancement)
                .display(
                        ModItems.SOUL_RING_HTD, // 显示的图标
                        Text.translatable("advancements.get_ring_htd.title").setStyle(Style.EMPTY.withColor(Formatting.RED)), // 标题
                        Text.translatable("advancements.get_ring_htd.description").setStyle(Style.EMPTY.withColor(Formatting.RED)), // 描述
                        new Identifier("textures/gui/advancements/backgrounds/adventure.png"), // 使用的背景图片
                        AdvancementFrame.CHALLENGE, // 选项: TASK, CHALLENGE, GOAL
                        true, // 在右上角显示
                        true, // 在聊天框中提示
                        false // 在进度页面里隐藏
                )
                // Criterion 中使用的第一个字符串是其他进度在需要 'requirements' 时引用的名字
                .criterion("get_ring_htd", MCDuro.GET_RING_HTD_CRI.create(new GainSoulRingCriterion.Conditions()))
                .build(consumer, MCDuro.MOD_ID + "/get_ring_htd");
    }
}
