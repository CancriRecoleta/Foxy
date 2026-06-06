//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import net.minecraft.server.dedicated.DedicatedServerProperties;

public interface ServerInterface {
    DedicatedServerProperties getProperties();

    String getServerIp();

    int getServerPort();

    String getServerName();

    String getServerVersion();

    int getPlayerCount();

    int getMaxPlayers();

    String[] getPlayerNames();

    String getLevelIdName();

    String getPluginNames();

    String runCommand(String var1);
}
