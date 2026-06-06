//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.world.entity;

public interface PlayerRideableJumping extends PlayerRideable {
    void onPlayerJump(int var1);

    boolean canJump();

    void handleStartJump(int var1);

    void handleStopJump();

    default int getJumpCooldown() {
        return 0;
    }
}
