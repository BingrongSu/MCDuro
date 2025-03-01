package net.robert.mcduro.math;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.robert.mcduro.MCDuro;
import net.robert.mcduro.events.ModClientEvents;

import java.util.*;

public class Helper {
    public static final List<Integer> list = new ArrayList<>();

    public static Map<String, Map<String, Double>> skillPower = new HashMap<>();    // 魂技所需要的魂力百分比

    public static int level2HunLi(Integer level) {
        if (level == 100) {
            return 30000000;
        }
        double amount = 1000000d*level*level/(980100d - 9801d*level);
        if (level < 90) {
            return (int) amount;
        } else {
            return (int) (amount * Math.pow((1 + (level-89)/10d), (level)/10d - 7));
        }
    } // Checked

    public static int hunLi2level(int amount) {
        if (amount == 0) {
            return 0;
        }
        for (int i = 0; i < 99; i++) {
            if (list.get(i) <= amount && amount < list.get(i+1)) {
                return i + 1;
            }
        }
        return 100;
    } // Checked

    public static int naturalIncrease(int level) {
        double n = 1000 * (-level*level + 199*level + 100) / (1411344d * (99 - level) * (100 - level));
        assert MinecraftClient.getInstance().world != null;
        double random = Math.random();
        if (level < 90) {
            if (random < n) {
                return 1;
            } else {
                return 0;
            }
        } else {
            n = (level2HunLi(level + 1) - level2HunLi(level)) / 144000d;
            if (random < n - (int) n) {
                return (int) n + 1;
            } else {
                return (int) n;
            }
        }
    } // Checked

    public static int getInitialLevel(long seed) {
        return gaussianRandom(seed, 6d, 1.5d, 1d, 10d);
    } // Checked

    public static int naturalRecover(int level) {
        int ans, base, bonus;
        double value;
        if (level < 70) {
            value = 11000 * Math.pow(level, 0.9) / (5880600d - 58806d * level);
        } else if (level < 90) {
            value = 12000 * Math.pow(level, 0.9) / (5880600d - 58806d * level);
        } else if (level < 99) {
            value = 15000 * Math.pow(level, 0.8) / (5880600d - 58806d * level) * Math.pow(1+(level-89)/10d, (level/10d)-7);
        } else {
            value = 20000 * Math.pow(level, 0.8) / (5880600d - 58806d * level) * Math.pow(1+(level-89)/9d, (level/10d)-7);
        }
        base = (int) value;
        bonus = Math.random() <= value - base ? 1 : 0;
        ans = base + bonus;
        return ans;
    } // checked

    public static int openDrain(int level) {
        int ans, base, bonus;
        double value;
        if (level < 90) {
            value = 11000 * Math.pow(level, 0.9) / (5880600d - 58806d * level);
        } else if (level < 99) {
            value = 15000 * Math.pow(level, 0.8) / (5880600d - 58806d * level) * Math.pow(1+(level-89)/10d, (level/10d)-7);
        } else {
            return 0;
        }
        base = (int) value;
        bonus = Math.random() <= value - base ? 1 : 0;
        ans = base + bonus;
        return ans;
    }

    public static int powerNeeded(String wuHun, String skill, double power, int maxSoulPower) {
        return (int) (maxSoulPower * (skillPower.get(wuHun).get(skill+"min") + (skillPower.get(wuHun).get(skill+"max") - skillPower.get(wuHun).get(skill+"min")) * power) + 0.5);
    }

    public static int totalPowerNeeded() {
        int ans = 0;
        List<List<Double>> wuHunData = ModClientEvents.playerData.wuHun.get(ModClientEvents.playerData.openedWuHun);
        for (int i = 0; i < wuHunData.size(); i++) {
            double min = Helper.skillPower.get(ModClientEvents.playerData.openedWuHun).get("%dmin".formatted(i+1));
            double max = Helper.skillPower.get(ModClientEvents.playerData.openedWuHun).get("%dmax".formatted(i+1));
            double thresholdVal = min / (max - min);
            ans += powerNeeded(ModClientEvents.playerData.openedWuHun, "" + (i+1), wuHunData.get(i).get(1) - thresholdVal, ModClientEvents.playerData.maxHunLi);
        }
        return ans;
    }

    public static double getChargeV(String wuHun, int soulPowerLevel, String skill) {
        return switch (wuHun) {
            case "fengHuang" -> switch (skill) {
                case "1" -> 1d / (2 * 20d);
                case "2" -> 1d / (2.5 * 20d);
                case "3" -> 1d / (2.6 * 20d);
                case "4" -> 1d / (3.3 * 20d);
                case "5" -> 1d / (3.2 * 20d);
                case "6" -> 1d / (3.5 * 20d);
                case "8" -> 1d / (4 * 20d);
                case "9" -> 1d / (6 * 20d);
                default -> 0;
            };
            default -> 0;
        };
    }

    public static int increaseMaxHunLi(int origin, int increment, PlayerEntity player) {
        int ans = origin;
        while (increment > 0) {
            if (ans + increment >= level2HunLi(hunLi2level(ans) + 1)) {
                if ((hunLi2level(ans) + 1) % 10 == 0) {
                    ans = level2HunLi(hunLi2level(ans) + 1) - 1;
                    increment = 0;
                    MCDuro.GET_STUCK_CRITERION.trigger((ServerPlayerEntity) player);
                } else {
                    increment -= level2HunLi(hunLi2level(ans) + 1) - ans;
                    ans = level2HunLi(hunLi2level(ans) + 1);
                }
            } else {
                ans += increment;
                increment = 0;
            }
        }
        return ans;
    }   // Checked

    /**
     * @param seed 随机种子
     * @param mean 期望值，中心点
     * @param stdDeviation 标准差，决定分布宽度
     * @param min 生成的最小值
     * @param max 生成的最大值
     * @return 正态分布的随机数
     */
    public static int gaussianRandom(long seed, double mean, double stdDeviation, double min, double max) {
        Random random = new Random(seed);

        // 正态分布的参数 // 期望值，中心点 // 标准差，决定分布宽度

        int result;
        do {
            // 生成正态分布随机数
            double randomValue = mean + random.nextGaussian() * stdDeviation;

            // 四舍五入并强制限制范围在 1 到 10
            result = (int) Math.round(randomValue);
        } while (result < min || result > max); // 确保在范围内

        return result;
    } // checked

    /**
     * @param times 保证出现的次数
     * @return 0-对应概率以外；1-对应概率以内
     */
    private static int uniformProbability(double times) {
        if (Math.random() < 1/times) return 1; else return 0;
    } // checked

    public static void initialize() {
        MCDuro.LOGGER.info("Initializing Math Helper");
        for (int i = 0; i < 99; i++) {
            list.add(level2HunLi(i+1));
        }
        list.add(30000000);     // 100级需要的魂力：3000万

        Map<String, Double> tmpFH = new HashMap<>();
        List<Double> tmpFH1 = List.of(0.05, 0.05, 0.05, 0.15, 0.15, 0.15, 0d, 0.3);
        List<Double> tmpFH2 = List.of(0.2, 0.2, 0.2, 0.5, 0.4, 0.4, 0d, 1d);
        for (int i = 0; i < 8; i++) {
            int index = i < 6 ? i : i+1;
            tmpFH.put("%dmin".formatted(index + 1), tmpFH1.get(i));
            tmpFH.put("%dmax".formatted(index + 1), tmpFH2.get(i));
        }
        skillPower.put("fengHuang", tmpFH);
    }
}
