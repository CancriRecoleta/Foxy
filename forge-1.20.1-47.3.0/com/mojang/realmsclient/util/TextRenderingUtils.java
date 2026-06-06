//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.mojang.realmsclient.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextRenderingUtils {
    private TextRenderingUtils() {
    }

    @VisibleForTesting
    protected static List<String> lineBreak(String p_90249_) {
        return Arrays.asList(p_90249_.split("\\n"));
    }

    public static List<Line> decompose(String p_90257_, LineSegment... p_90258_) {
        return decompose(p_90257_, Arrays.asList(p_90258_));
    }

    private static List<Line> decompose(String p_90254_, List<LineSegment> p_90255_) {
        List<String> $$2 = lineBreak(p_90254_);
        return insertLinks($$2, p_90255_);
    }

    private static List<Line> insertLinks(List<String> p_90260_, List<LineSegment> p_90261_) {
        int $$2 = 0;
        List<Line> $$3 = Lists.newArrayList();
        Iterator var4 = p_90260_.iterator();

        while(var4.hasNext()) {
            String $$4 = (String)var4.next();
            List<LineSegment> $$5 = Lists.newArrayList();
            List<String> $$6 = split($$4, "%link");
            Iterator var8 = $$6.iterator();

            while(var8.hasNext()) {
                String $$7 = (String)var8.next();
                if ("%link".equals($$7)) {
                    $$5.add((LineSegment)p_90261_.get($$2++));
                } else {
                    $$5.add(com.mojang.realmsclient.util.TextRenderingUtils.LineSegment.text($$7));
                }
            }

            $$3.add(new Line($$5));
        }

        return $$3;
    }

    public static List<String> split(String p_90251_, String p_90252_) {
        if (p_90252_.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be the empty string");
        } else {
            List<String> $$2 = Lists.newArrayList();

            int $$3;
            int $$4;
            for($$3 = 0; ($$4 = p_90251_.indexOf(p_90252_, $$3)) != -1; $$3 = $$4 + p_90252_.length()) {
                if ($$4 > $$3) {
                    $$2.add(p_90251_.substring($$3, $$4));
                }

                $$2.add(p_90252_);
            }

            if ($$3 < p_90251_.length()) {
                $$2.add(p_90251_.substring($$3));
            }

            return $$2;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LineSegment {
        private final String fullText;
        @Nullable
        private final String linkTitle;
        @Nullable
        private final String linkUrl;

        private LineSegment(String p_90273_) {
            this.fullText = p_90273_;
            this.linkTitle = null;
            this.linkUrl = null;
        }

        private LineSegment(String p_90275_, @Nullable String p_90276_, @Nullable String p_90277_) {
            this.fullText = p_90275_;
            this.linkTitle = p_90276_;
            this.linkUrl = p_90277_;
        }

        public boolean equals(Object p_90287_) {
            if (this == p_90287_) {
                return true;
            } else if (p_90287_ != null && this.getClass() == p_90287_.getClass()) {
                LineSegment $$1 = (LineSegment)p_90287_;
                return Objects.equals(this.fullText, $$1.fullText) && Objects.equals(this.linkTitle, $$1.linkTitle) && Objects.equals(this.linkUrl, $$1.linkUrl);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.fullText, this.linkTitle, this.linkUrl});
        }

        public String toString() {
            return "Segment{fullText='" + this.fullText + "', linkTitle='" + this.linkTitle + "', linkUrl='" + this.linkUrl + "'}";
        }

        public String renderedText() {
            return this.isLink() ? this.linkTitle : this.fullText;
        }

        public boolean isLink() {
            return this.linkTitle != null;
        }

        public String getLinkUrl() {
            if (!this.isLink()) {
                throw new IllegalStateException("Not a link: " + this);
            } else {
                return this.linkUrl;
            }
        }

        public static LineSegment link(String p_90282_, String p_90283_) {
            return new LineSegment((String)null, p_90282_, p_90283_);
        }

        @VisibleForTesting
        protected static LineSegment text(String p_90280_) {
            return new LineSegment(p_90280_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Line {
        public final List<LineSegment> segments;

        Line(LineSegment... p_167625_) {
            this(Arrays.asList(p_167625_));
        }

        Line(List<LineSegment> p_90264_) {
            this.segments = p_90264_;
        }

        public String toString() {
            return "Line{segments=" + this.segments + "}";
        }

        public boolean equals(Object p_90266_) {
            if (this == p_90266_) {
                return true;
            } else if (p_90266_ != null && this.getClass() == p_90266_.getClass()) {
                Line $$1 = (Line)p_90266_;
                return Objects.equals(this.segments, $$1.segments);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.segments});
        }
    }
}
