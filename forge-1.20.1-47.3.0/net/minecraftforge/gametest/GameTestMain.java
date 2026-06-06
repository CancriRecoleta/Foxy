//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.gametest;

import net.minecraft.server.Main;

public class GameTestMain {
    public GameTestMain() {
    }

    public static void main(String[] args) {
        System.setProperty("forge.enableGameTest", "true");
        System.setProperty("forge.gameTestServer", "true");
        Main.main(args);
    }
}
