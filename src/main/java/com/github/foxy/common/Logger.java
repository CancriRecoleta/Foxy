package com.github.foxy.common;

import org.slf4j.LoggerFactory;

/**
 * Tiny SLF4J facade used by every loader-agnostic class in {@code com.github.foxy.common}.
 *
 * <p>The upstream Voxy module exposed a hand-rolled logger so the {@code common} tree could
 * stay loader-agnostic; we mirror that here against the SLF4J binding that Forge ships. All
 * messages are tagged with the {@code "foxy"} marker so they can be filtered in log4j2.</p>
 */
public final class Logger {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("foxy");

    private Logger() {}

    /** Logs at INFO level. */
    public static void info(String msg) { LOG.info(msg); }

    /** Logs at WARN level. */
    public static void warn(String msg) { LOG.warn(msg); }

    /** Logs at ERROR level. */
    public static void error(String msg) { LOG.error(msg); }

    /** Logs at ERROR level with a throwable. */
    public static void error(String msg, Throwable t) { LOG.error(msg, t); }

    public static void error(Throwable t) { LOG.error(t.getMessage(), t); }

    public static void showInHUD(String msg) { info(msg); }
}
