//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class MultipleTestTracker {
    private static final char NOT_STARTED_TEST_CHAR = ' ';
    private static final char ONGOING_TEST_CHAR = '_';
    private static final char SUCCESSFUL_TEST_CHAR = '+';
    private static final char FAILED_OPTIONAL_TEST_CHAR = 'x';
    private static final char FAILED_REQUIRED_TEST_CHAR = 'X';
    private final Collection<GameTestInfo> tests = Lists.newArrayList();
    @Nullable
    private final Collection<GameTestListener> listeners = Lists.newArrayList();

    public MultipleTestTracker() {
    }

    public MultipleTestTracker(Collection<GameTestInfo> p_127802_) {
        this.tests.addAll(p_127802_);
    }

    public void addTestToTrack(GameTestInfo p_127810_) {
        this.tests.add(p_127810_);
        Collection var10000 = this.listeners;
        Objects.requireNonNull(p_127810_);
        var10000.forEach(p_127810_::addListener);
    }

    public void addListener(GameTestListener p_127812_) {
        this.listeners.add(p_127812_);
        this.tests.forEach((p_127815_) -> {
            p_127815_.addListener(p_127812_);
        });
    }

    public void addFailureListener(final Consumer<GameTestInfo> p_127808_) {
        this.addListener(new GameTestListener() {
            public void testStructureLoaded(GameTestInfo p_127830_) {
            }

            public void testPassed(GameTestInfo p_177685_) {
            }

            public void testFailed(GameTestInfo p_127832_) {
                p_127808_.accept(p_127832_);
            }
        });
    }

    public int getFailedRequiredCount() {
        return (int)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isRequired).count();
    }

    public int getFailedOptionalCount() {
        return (int)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isOptional).count();
    }

    public int getDoneCount() {
        return (int)this.tests.stream().filter(GameTestInfo::isDone).count();
    }

    public boolean hasFailedRequired() {
        return this.getFailedRequiredCount() > 0;
    }

    public boolean hasFailedOptional() {
        return this.getFailedOptionalCount() > 0;
    }

    public Collection<GameTestInfo> getFailedRequired() {
        return (Collection)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isRequired).collect(Collectors.toList());
    }

    public Collection<GameTestInfo> getFailedOptional() {
        return (Collection)this.tests.stream().filter(GameTestInfo::hasFailed).filter(GameTestInfo::isOptional).collect(Collectors.toList());
    }

    public int getTotalCount() {
        return this.tests.size();
    }

    public boolean isDone() {
        return this.getDoneCount() == this.getTotalCount();
    }

    public String getProgressBar() {
        StringBuffer $$0 = new StringBuffer();
        $$0.append('[');
        this.tests.forEach((p_127806_) -> {
            if (!p_127806_.hasStarted()) {
                $$0.append(' ');
            } else if (p_127806_.hasSucceeded()) {
                $$0.append('+');
            } else if (p_127806_.hasFailed()) {
                $$0.append((char)(p_127806_.isRequired() ? 'X' : 'x'));
            } else {
                $$0.append('_');
            }

        });
        $$0.append(']');
        return $$0.toString();
    }

    public String toString() {
        return this.getProgressBar();
    }
}
