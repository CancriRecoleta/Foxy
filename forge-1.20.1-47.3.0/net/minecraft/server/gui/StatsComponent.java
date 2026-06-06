//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;

public class StatsComponent extends JComponent {
    private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("########0.000"), (p_139968_) -> {
        p_139968_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    });
    private final int[] values = new int[256];
    private int vp;
    private final String[] msgs = new String[11];
    private final MinecraftServer server;
    private final Timer timer;

    public StatsComponent(MinecraftServer p_139963_) {
        this.server = p_139963_;
        this.setPreferredSize(new Dimension(456, 246));
        this.setMinimumSize(new Dimension(456, 246));
        this.setMaximumSize(new Dimension(456, 246));
        this.timer = new Timer(500, (p_139966_) -> {
            this.tick();
        });
        this.timer.start();
        this.setBackground(Color.BLACK);
    }

    private void tick() {
        long $$0 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        this.msgs[0] = "Memory use: " + $$0 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
        String[] var10000 = this.msgs;
        DecimalFormat var10002 = DECIMAL_FORMAT;
        double var10003 = this.getAverage(this.server.tickTimes);
        var10000[1] = "Avg tick: " + var10002.format(var10003 * 1.0E-6) + " ms";
        this.values[this.vp++ & 255] = (int)($$0 * 100L / Runtime.getRuntime().maxMemory());
        this.repaint();
    }

    private double getAverage(long[] p_139970_) {
        long $$1 = 0L;
        long[] var4 = p_139970_;
        int var5 = p_139970_.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            long $$2 = var4[var6];
            $$1 += $$2;
        }

        return (double)$$1 / (double)p_139970_.length;
    }

    public void paint(Graphics p_139973_) {
        p_139973_.setColor(new Color(16777215));
        p_139973_.fillRect(0, 0, 456, 246);

        int $$3;
        for($$3 = 0; $$3 < 256; ++$$3) {
            int $$2 = this.values[$$3 + this.vp & 255];
            p_139973_.setColor(new Color($$2 + 28 << 16));
            p_139973_.fillRect($$3, 100 - $$2, 1, $$2);
        }

        p_139973_.setColor(Color.BLACK);

        for($$3 = 0; $$3 < this.msgs.length; ++$$3) {
            String $$4 = this.msgs[$$3];
            if ($$4 != null) {
                p_139973_.drawString($$4, 32, 116 + $$3 * 16);
            }
        }

    }

    public void close() {
        this.timer.stop();
    }
}
