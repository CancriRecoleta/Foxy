//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraftforge.energy;

public class EmptyEnergyStorage implements IEnergyStorage {
    public static final EmptyEnergyStorage INSTANCE = new EmptyEnergyStorage();

    protected EmptyEnergyStorage() {
    }

    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    public int getEnergyStored() {
        return 0;
    }

    public int getMaxEnergyStored() {
        return 0;
    }

    public boolean canExtract() {
        return false;
    }

    public boolean canReceive() {
        return false;
    }
}
