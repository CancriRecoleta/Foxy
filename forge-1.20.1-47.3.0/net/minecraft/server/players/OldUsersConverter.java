//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

public class OldUsersConverter {
    static final Logger LOGGER = LogUtils.getLogger();
    public static final File OLD_IPBANLIST = new File("banned-ips.txt");
    public static final File OLD_USERBANLIST = new File("banned-players.txt");
    public static final File OLD_OPLIST = new File("ops.txt");
    public static final File OLD_WHITELIST = new File("white-list.txt");

    public OldUsersConverter() {
    }

    static List<String> readOldListFormat(File p_11074_, Map<String, String[]> p_11075_) throws IOException {
        List<String> $$2 = Files.readLines(p_11074_, StandardCharsets.UTF_8);
        Iterator var3 = $$2.iterator();

        while(var3.hasNext()) {
            String $$3 = (String)var3.next();
            $$3 = $$3.trim();
            if (!$$3.startsWith("#") && $$3.length() >= 1) {
                String[] $$4 = $$3.split("\\|");
                p_11075_.put($$4[0].toLowerCase(Locale.ROOT), $$4);
            }
        }

        return $$2;
    }

    private static void lookupPlayers(MinecraftServer p_11087_, Collection<String> p_11088_, ProfileLookupCallback p_11089_) {
        String[] $$3 = (String[])p_11088_.stream().filter((p_11077_) -> {
            return !StringUtil.isNullOrEmpty(p_11077_);
        }).toArray((p_11070_) -> {
            return new String[p_11070_];
        });
        if (p_11087_.usesAuthentication()) {
            p_11087_.getProfileRepository().findProfilesByNames($$3, Agent.MINECRAFT, p_11089_);
        } else {
            String[] var4 = $$3;
            int var5 = $$3.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String $$4 = var4[var6];
                UUID $$5 = UUIDUtil.getOrCreatePlayerUUID(new GameProfile((UUID)null, $$4));
                GameProfile $$6 = new GameProfile($$5, $$4);
                p_11089_.onProfileLookupSucceeded($$6);
            }
        }

    }

    public static boolean convertUserBanlist(final MinecraftServer p_11082_) {
        final UserBanList $$1 = new UserBanList(PlayerList.USERBANLIST_FILE);
        if (OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
            IOException $$5;
            if ($$1.getFile().exists()) {
                try {
                    $$1.load();
                } catch (IOException var6) {
                    $$5 = var6;
                    LOGGER.warn("Could not load existing file {}", $$1.getFile().getName(), $$5);
                }
            }

            try {
                final Map<String, String[]> $$3 = Maps.newHashMap();
                readOldListFormat(OLD_USERBANLIST, $$3);
                ProfileLookupCallback $$4 = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile p_11123_) {
                        p_11082_.getProfileCache().add(p_11123_);
                        String[] $$1x = (String[])$$3.get(p_11123_.getName().toLowerCase(Locale.ROOT));
                        if ($$1x == null) {
                            OldUsersConverter.LOGGER.warn("Could not convert user banlist entry for {}", p_11123_.getName());
                            throw new ConversionError("Profile not in the conversionlist");
                        } else {
                            Date $$2 = $$1x.length > 1 ? OldUsersConverter.parseDate($$1x[1], (Date)null) : null;
                            String $$3x = $$1x.length > 2 ? $$1x[2] : null;
                            Date $$4 = $$1x.length > 3 ? OldUsersConverter.parseDate($$1x[3], (Date)null) : null;
                            String $$5 = $$1x.length > 4 ? $$1x[4] : null;
                            $$1.add(new UserBanListEntry(p_11123_, $$2, $$3x, $$4, $$5));
                        }
                    }

                    public void onProfileLookupFailed(GameProfile p_11120_, Exception p_11121_) {
                        OldUsersConverter.LOGGER.warn("Could not lookup user banlist entry for {}", p_11120_.getName(), p_11121_);
                        if (!(p_11121_ instanceof ProfileNotFoundException)) {
                            throw new ConversionError("Could not request user " + p_11120_.getName() + " from backend systems", p_11121_);
                        }
                    }
                };
                lookupPlayers(p_11082_, $$3.keySet(), $$4);
                $$1.save();
                renameOldFile(OLD_USERBANLIST);
                return true;
            } catch (IOException var4) {
                $$5 = var4;
                LOGGER.warn("Could not read old user banlist to convert it!", $$5);
                return false;
            } catch (ConversionError var5) {
                ConversionError $$6 = var5;
                LOGGER.error("Conversion failed, please try again later", $$6);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean convertIpBanlist(MinecraftServer p_11099_) {
        IpBanList $$1 = new IpBanList(PlayerList.IPBANLIST_FILE);
        if (OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
            IOException $$10;
            if ($$1.getFile().exists()) {
                try {
                    $$1.load();
                } catch (IOException var11) {
                    $$10 = var11;
                    LOGGER.warn("Could not load existing file {}", $$1.getFile().getName(), $$10);
                }
            }

            try {
                Map<String, String[]> $$3 = Maps.newHashMap();
                readOldListFormat(OLD_IPBANLIST, $$3);
                Iterator var3 = $$3.keySet().iterator();

                while(var3.hasNext()) {
                    String $$4 = (String)var3.next();
                    String[] $$5 = (String[])$$3.get($$4);
                    Date $$6 = $$5.length > 1 ? parseDate($$5[1], (Date)null) : null;
                    String $$7 = $$5.length > 2 ? $$5[2] : null;
                    Date $$8 = $$5.length > 3 ? parseDate($$5[3], (Date)null) : null;
                    String $$9 = $$5.length > 4 ? $$5[4] : null;
                    $$1.add(new IpBanListEntry($$4, $$6, $$7, $$8, $$9));
                }

                $$1.save();
                renameOldFile(OLD_IPBANLIST);
                return true;
            } catch (IOException var10) {
                $$10 = var10;
                LOGGER.warn("Could not parse old ip banlist to convert it!", $$10);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean convertOpsList(final MinecraftServer p_11103_) {
        final ServerOpList $$1 = new ServerOpList(PlayerList.OPLIST_FILE);
        if (OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
            IOException $$5;
            if ($$1.getFile().exists()) {
                try {
                    $$1.load();
                } catch (IOException var6) {
                    $$5 = var6;
                    LOGGER.warn("Could not load existing file {}", $$1.getFile().getName(), $$5);
                }
            }

            try {
                List<String> $$3 = Files.readLines(OLD_OPLIST, StandardCharsets.UTF_8);
                ProfileLookupCallback $$4 = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile p_11133_) {
                        p_11103_.getProfileCache().add(p_11133_);
                        $$1.add(new ServerOpListEntry(p_11133_, p_11103_.getOperatorUserPermissionLevel(), false));
                    }

                    public void onProfileLookupFailed(GameProfile p_11130_, Exception p_11131_) {
                        OldUsersConverter.LOGGER.warn("Could not lookup oplist entry for {}", p_11130_.getName(), p_11131_);
                        if (!(p_11131_ instanceof ProfileNotFoundException)) {
                            throw new ConversionError("Could not request user " + p_11130_.getName() + " from backend systems", p_11131_);
                        }
                    }
                };
                lookupPlayers(p_11103_, $$3, $$4);
                $$1.save();
                renameOldFile(OLD_OPLIST);
                return true;
            } catch (IOException var4) {
                $$5 = var4;
                LOGGER.warn("Could not read old oplist to convert it!", $$5);
                return false;
            } catch (ConversionError var5) {
                ConversionError $$6 = var5;
                LOGGER.error("Conversion failed, please try again later", $$6);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean convertWhiteList(final MinecraftServer p_11105_) {
        final UserWhiteList $$1 = new UserWhiteList(PlayerList.WHITELIST_FILE);
        if (OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
            IOException $$5;
            if ($$1.getFile().exists()) {
                try {
                    $$1.load();
                } catch (IOException var6) {
                    $$5 = var6;
                    LOGGER.warn("Could not load existing file {}", $$1.getFile().getName(), $$5);
                }
            }

            try {
                List<String> $$3 = Files.readLines(OLD_WHITELIST, StandardCharsets.UTF_8);
                ProfileLookupCallback $$4 = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile p_11143_) {
                        p_11105_.getProfileCache().add(p_11143_);
                        $$1.add(new UserWhiteListEntry(p_11143_));
                    }

                    public void onProfileLookupFailed(GameProfile p_11140_, Exception p_11141_) {
                        OldUsersConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", p_11140_.getName(), p_11141_);
                        if (!(p_11141_ instanceof ProfileNotFoundException)) {
                            throw new ConversionError("Could not request user " + p_11140_.getName() + " from backend systems", p_11141_);
                        }
                    }
                };
                lookupPlayers(p_11105_, $$3, $$4);
                $$1.save();
                renameOldFile(OLD_WHITELIST);
                return true;
            } catch (IOException var4) {
                $$5 = var4;
                LOGGER.warn("Could not read old whitelist to convert it!", $$5);
                return false;
            } catch (ConversionError var5) {
                ConversionError $$6 = var5;
                LOGGER.error("Conversion failed, please try again later", $$6);
                return false;
            }
        } else {
            return true;
        }
    }

    @Nullable
    public static UUID convertMobOwnerIfNecessary(final MinecraftServer p_11084_, String p_11085_) {
        if (!StringUtil.isNullOrEmpty(p_11085_) && p_11085_.length() <= 16) {
            Optional<UUID> $$3 = p_11084_.getProfileCache().get(p_11085_).map(GameProfile::getId);
            if ($$3.isPresent()) {
                return (UUID)$$3.get();
            } else if (!p_11084_.isSingleplayer() && p_11084_.usesAuthentication()) {
                final List<GameProfile> $$4 = Lists.newArrayList();
                ProfileLookupCallback $$5 = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile p_11153_) {
                        p_11084_.getProfileCache().add(p_11153_);
                        $$4.add(p_11153_);
                    }

                    public void onProfileLookupFailed(GameProfile p_11150_, Exception p_11151_) {
                        OldUsersConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", p_11150_.getName(), p_11151_);
                    }
                };
                lookupPlayers(p_11084_, Lists.newArrayList(new String[]{p_11085_}), $$5);
                return !$$4.isEmpty() && ((GameProfile)$$4.get(0)).getId() != null ? ((GameProfile)$$4.get(0)).getId() : null;
            } else {
                return UUIDUtil.getOrCreatePlayerUUID(new GameProfile((UUID)null, p_11085_));
            }
        } else {
            try {
                return UUID.fromString(p_11085_);
            } catch (IllegalArgumentException var5) {
                return null;
            }
        }
    }

    public static boolean convertPlayers(final DedicatedServer p_11091_) {
        final File $$1 = getWorldPlayersDirectory(p_11091_);
        final File $$2 = new File($$1.getParentFile(), "playerdata");
        final File $$3 = new File($$1.getParentFile(), "unknownplayers");
        if ($$1.exists() && $$1.isDirectory()) {
            File[] $$4 = $$1.listFiles();
            List<String> $$5 = Lists.newArrayList();
            File[] var6 = $$4;
            int var7 = $$4.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                File $$6 = var6[var8];
                String $$7 = $$6.getName();
                if ($$7.toLowerCase(Locale.ROOT).endsWith(".dat")) {
                    String $$8 = $$7.substring(0, $$7.length() - ".dat".length());
                    if (!$$8.isEmpty()) {
                        $$5.add($$8);
                    }
                }
            }

            try {
                final String[] $$9 = (String[])$$5.toArray(new String[$$5.size()]);
                ProfileLookupCallback $$10 = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile p_11175_) {
                        p_11091_.getProfileCache().add(p_11175_);
                        UUID $$1x = p_11175_.getId();
                        if ($$1x == null) {
                            throw new ConversionError("Missing UUID for user profile " + p_11175_.getName());
                        } else {
                            this.movePlayerFile($$2, this.getFileNameForProfile(p_11175_), $$1x.toString());
                        }
                    }

                    public void onProfileLookupFailed(GameProfile p_11172_, Exception p_11173_) {
                        OldUsersConverter.LOGGER.warn("Could not lookup user uuid for {}", p_11172_.getName(), p_11173_);
                        if (p_11173_ instanceof ProfileNotFoundException) {
                            String $$2x = this.getFileNameForProfile(p_11172_);
                            this.movePlayerFile($$3, $$2x, $$2x);
                        } else {
                            throw new ConversionError("Could not request user " + p_11172_.getName() + " from backend systems", p_11173_);
                        }
                    }

                    private void movePlayerFile(File p_11168_, String p_11169_, String p_11170_) {
                        File $$3x = new File($$1, p_11169_ + ".dat");
                        File $$4 = new File(p_11168_, p_11170_ + ".dat");
                        OldUsersConverter.ensureDirectoryExists(p_11168_);
                        if (!$$3x.renameTo($$4)) {
                            throw new ConversionError("Could not convert file for " + p_11169_);
                        }
                    }

                    private String getFileNameForProfile(GameProfile p_11166_) {
                        String $$1x = null;
                        String[] var3 = $$9;
                        int var4 = var3.length;

                        for(int var5 = 0; var5 < var4; ++var5) {
                            String $$2x = var3[var5];
                            if ($$2x != null && $$2x.equalsIgnoreCase(p_11166_.getName())) {
                                $$1x = $$2x;
                                break;
                            }
                        }

                        if ($$1x == null) {
                            throw new ConversionError("Could not find the filename for " + p_11166_.getName() + " anymore");
                        } else {
                            return $$1x;
                        }
                    }
                };
                lookupPlayers(p_11091_, Lists.newArrayList($$9), $$10);
                return true;
            } catch (ConversionError var12) {
                LOGGER.error("Conversion failed, please try again later", var12);
                return false;
            }
        } else {
            return true;
        }
    }

    static void ensureDirectoryExists(File p_11094_) {
        if (p_11094_.exists()) {
            if (!p_11094_.isDirectory()) {
                throw new ConversionError("Can't create directory " + p_11094_.getName() + " in world save directory.");
            }
        } else if (!p_11094_.mkdirs()) {
            throw new ConversionError("Can't create directory " + p_11094_.getName() + " in world save directory.");
        }
    }

    public static boolean serverReadyAfterUserconversion(MinecraftServer p_11107_) {
        boolean $$1 = areOldUserlistsRemoved();
        $$1 = $$1 && areOldPlayersConverted(p_11107_);
        return $$1;
    }

    private static boolean areOldUserlistsRemoved() {
        boolean $$0 = false;
        if (OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
            $$0 = true;
        }

        boolean $$1 = false;
        if (OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
            $$1 = true;
        }

        boolean $$2 = false;
        if (OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
            $$2 = true;
        }

        boolean $$3 = false;
        if (OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
            $$3 = true;
        }

        if (!$$0 && !$$1 && !$$2 && !$$3) {
            return true;
        } else {
            LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
            LOGGER.warn("** please remove the following files and restart the server:");
            if ($$0) {
                LOGGER.warn("* {}", OLD_USERBANLIST.getName());
            }

            if ($$1) {
                LOGGER.warn("* {}", OLD_IPBANLIST.getName());
            }

            if ($$2) {
                LOGGER.warn("* {}", OLD_OPLIST.getName());
            }

            if ($$3) {
                LOGGER.warn("* {}", OLD_WHITELIST.getName());
            }

            return false;
        }
    }

    private static boolean areOldPlayersConverted(MinecraftServer p_11109_) {
        File $$1 = getWorldPlayersDirectory(p_11109_);
        if (!$$1.exists() || !$$1.isDirectory() || $$1.list().length <= 0 && $$1.delete()) {
            return true;
        } else {
            LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
            LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
            LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", $$1.getPath());
            return false;
        }
    }

    private static File getWorldPlayersDirectory(MinecraftServer p_11111_) {
        return p_11111_.getWorldPath(LevelResource.PLAYER_OLD_DATA_DIR).toFile();
    }

    private static void renameOldFile(File p_11101_) {
        File $$1 = new File(p_11101_.getName() + ".converted");
        p_11101_.renameTo($$1);
    }

    static Date parseDate(String p_11096_, Date p_11097_) {
        Date $$4;
        try {
            $$4 = BanListEntry.DATE_FORMAT.parse(p_11096_);
        } catch (ParseException var4) {
            $$4 = p_11097_;
        }

        return $$4;
    }

    private static class ConversionError extends RuntimeException {
        ConversionError(String p_11182_, Throwable p_11183_) {
            super(p_11182_, p_11183_);
        }

        ConversionError(String p_11177_) {
            super(p_11177_);
        }
    }
}
