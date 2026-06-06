//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.gametest.framework;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.logging.LogUtils;
import net.minecraft.Util;
import org.slf4j.Logger;

public class TeamcityTestReporter implements TestReporter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Escaper ESCAPER = Escapers.builder().addEscape('\'', "|'").addEscape('\n', "|n").addEscape('\r', "|r").addEscape('|', "||").addEscape('[', "|[").addEscape(']', "|]").build();

    public TeamcityTestReporter() {
    }

    public void onTestFailed(GameTestInfo p_177783_) {
        String $$1 = ESCAPER.escape(p_177783_.getTestName());
        String $$2 = ESCAPER.escape(p_177783_.getError().getMessage());
        String $$3 = ESCAPER.escape(Util.describeError(p_177783_.getError()));
        LOGGER.info("##teamcity[testStarted name='{}']", $$1);
        if (p_177783_.isRequired()) {
            LOGGER.info("##teamcity[testFailed name='{}' message='{}' details='{}']", new Object[]{$$1, $$2, $$3});
        } else {
            LOGGER.info("##teamcity[testIgnored name='{}' message='{}' details='{}']", new Object[]{$$1, $$2, $$3});
        }

        LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", $$1, p_177783_.getRunTime());
    }

    public void onTestSuccess(GameTestInfo p_177785_) {
        String $$1 = ESCAPER.escape(p_177785_.getTestName());
        LOGGER.info("##teamcity[testStarted name='{}']", $$1);
        LOGGER.info("##teamcity[testFinished name='{}' duration='{}']", $$1, p_177785_.getRunTime());
    }
}
