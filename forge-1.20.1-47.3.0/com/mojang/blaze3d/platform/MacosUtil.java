//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.blaze3d.platform;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWNativeCocoa;

@OnlyIn(Dist.CLIENT)
public class MacosUtil {
    private static final int NS_FULL_SCREEN_WINDOW_MASK = 16384;

    public MacosUtil() {
    }

    public static void toggleFullscreen(long p_182518_) {
        getNsWindow(p_182518_).filter(MacosUtil::isInKioskMode).ifPresent(MacosUtil::toggleFullscreen);
    }

    private static Optional<NSObject> getNsWindow(long p_182522_) {
        long $$1 = GLFWNativeCocoa.glfwGetCocoaWindow(p_182522_);
        return $$1 != 0L ? Optional.of(new NSObject(new Pointer($$1))) : Optional.empty();
    }

    private static boolean isInKioskMode(NSObject p_182520_) {
        return ((Long)p_182520_.sendRaw("styleMask", new Object[0]) & 16384L) == 16384L;
    }

    private static void toggleFullscreen(NSObject p_182524_) {
        p_182524_.send("toggleFullScreen:", new Object[]{Pointer.NULL});
    }

    public static void loadIcon(IoSupplier<InputStream> p_250929_) throws IOException {
        InputStream $$1 = (InputStream)p_250929_.get();

        try {
            String $$2 = Base64.getEncoder().encodeToString($$1.readAllBytes());
            Client $$3 = Client.getInstance();
            Object $$4 = $$3.sendProxy("NSData", "alloc", new Object[0]).send("initWithBase64Encoding:", new Object[]{$$2});
            Object $$5 = $$3.sendProxy("NSImage", "alloc", new Object[0]).send("initWithData:", new Object[]{$$4});
            $$3.sendProxy("NSApplication", "sharedApplication", new Object[0]).send("setApplicationIconImage:", new Object[]{$$5});
        } catch (Throwable var7) {
            if ($$1 != null) {
                try {
                    $$1.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }
            }

            throw var7;
        }

        if ($$1 != null) {
            $$1.close();
        }

    }
}
