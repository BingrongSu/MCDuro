package net.robert.mcduro.math;

import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class MathTest {
    public static void main(String[] args) {
        Helper.initialize();
        Scanner input = new Scanner(System.in);
//        for (int i = 1; i < 99; i++) {
//            System.out.print(i);
//            System.out.print("   ");
//            System.out.print(Helper.level2HunLi(i));
//            System.out.print("   ");
//            System.out.println((Helper.level2HunLi(i + 1) - Helper.level2HunLi(i))/144000d);
//        }
//
//        int ans = 0;
//        for (int i = 0; i < 144000; i++) {
//            ans += Helper.naturalIncrease(94);
//        }
//        System.out.println(ans);

        int level = 0;
        while (level >= 0) {
            level = input.nextInt();
            int hunLi = 0;
            int maxHunLi = Helper.level2HunLi(level);
            int ticks = 0;
            while (hunLi < maxHunLi) {
                hunLi += Helper.naturalRecover(level);
                ticks ++;
            }
            System.out.println("Max Hun Li: " + maxHunLi);
            System.out.println("Ticks: " + ticks + "\n" + "Seconds: " + ticks / 20 + "\n" + "Minutes: " + ticks / 20 / 60);
        }
    }
}
