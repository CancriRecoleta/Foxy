//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.blaze3d.platform;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFWVidMode;

@OnlyIn(Dist.CLIENT)
public final class VideoMode {
    private final int width;
    private final int height;
    private final int redBits;
    private final int greenBits;
    private final int blueBits;
    private final int refreshRate;
    private static final Pattern PATTERN = Pattern.compile("(\\d+)x(\\d+)(?:@(\\d+)(?::(\\d+))?)?");

    public VideoMode(int p_85322_, int p_85323_, int p_85324_, int p_85325_, int p_85326_, int p_85327_) {
        this.width = p_85322_;
        this.height = p_85323_;
        this.redBits = p_85324_;
        this.greenBits = p_85325_;
        this.blueBits = p_85326_;
        this.refreshRate = p_85327_;
    }

    public VideoMode(GLFWVidMode.Buffer p_85329_) {
        this.width = p_85329_.width();
        this.height = p_85329_.height();
        this.redBits = p_85329_.redBits();
        this.greenBits = p_85329_.greenBits();
        this.blueBits = p_85329_.blueBits();
        this.refreshRate = p_85329_.refreshRate();
    }

    public VideoMode(GLFWVidMode p_85331_) {
        this.width = p_85331_.width();
        this.height = p_85331_.height();
        this.redBits = p_85331_.redBits();
        this.greenBits = p_85331_.greenBits();
        this.blueBits = p_85331_.blueBits();
        this.refreshRate = p_85331_.refreshRate();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getRedBits() {
        return this.redBits;
    }

    public int getGreenBits() {
        return this.greenBits;
    }

    public int getBlueBits() {
        return this.blueBits;
    }

    public int getRefreshRate() {
        return this.refreshRate;
    }

    public boolean equals(Object p_85340_) {
        if (this == p_85340_) {
            return true;
        } else if (p_85340_ != null && this.getClass() == p_85340_.getClass()) {
            VideoMode $$1 = (VideoMode)p_85340_;
            return this.width == $$1.width && this.height == $$1.height && this.redBits == $$1.redBits && this.greenBits == $$1.greenBits && this.blueBits == $$1.blueBits && this.refreshRate == $$1.refreshRate;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.width, this.height, this.redBits, this.greenBits, this.blueBits, this.refreshRate});
    }

    public String toString() {
        return String.format(Locale.ROOT, "%sx%s@%s (%sbit)", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
    }

    public static Optional<VideoMode> read(@Nullable String p_85334_) {
        if (p_85334_ == null) {
            return Optional.empty();
        } else {
            try {
                Matcher $$1 = PATTERN.matcher(p_85334_);
                if ($$1.matches()) {
                    int $$2 = Integer.parseInt($$1.group(1));
                    int $$3 = Integer.parseInt($$1.group(2));
                    String $$4 = $$1.group(3);
                    int $$6;
                    if ($$4 == null) {
                        $$6 = 60;
                    } else {
                        $$6 = Integer.parseInt($$4);
                    }

                    String $$7 = $$1.group(4);
                    int $$9;
                    if ($$7 == null) {
                        $$9 = 24;
                    } else {
                        $$9 = Integer.parseInt($$7);
                    }

                    int $$10 = $$9 / 3;
                    return Optional.of(new VideoMode($$2, $$3, $$10, $$10, $$10, $$6));
                }
            } catch (Exception var9) {
            }

            return Optional.empty();
        }
    }

    public String write() {
        return String.format(Locale.ROOT, "%sx%s@%s:%s", this.width, this.height, this.refreshRate, this.redBits + this.greenBits + this.blueBits);
    }
}
