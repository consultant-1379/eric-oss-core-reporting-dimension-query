/*******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/

package com.ericsson.oss.air.security.utils.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;

public class AuditLogger {

    private static final String FACILITY_KEY = "facility";
    private static final String SUBJECT_KEY = "subject";
    private static final String AUDIT_LOG = "log audit";
    private static final String NA_SUBJECT = "N/A";
    private final Logger logger;

    public AuditLogger(final Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Record a Trace log.
     *
     * @param message The log message
     * @param args    Additional arguments
     */
    public void trace(final String message, final Object... args) {
        this.logAudit(Level.TRACE, message, args);
    }

    /**
     * Record a Trace log.
     *
     * @param message   The log message
     * @param throwable The thrown error
     */
    public void trace(final String message, final Throwable throwable) {
        this.logAudit(Level.TRACE, message, throwable);
    }

    /**
     * Record a Debug log.
     *
     * @param message The log message
     * @param args    Additional arguments
     */
    public void debug(final String message, final Object... args) {
        this.logAudit(Level.DEBUG, message, args);
    }

    /**
     * Record a Debug log.
     *
     * @param message   The log message
     * @param throwable The thrown error
     */
    public void debug(final String message, final Throwable throwable) {
        this.logAudit(Level.DEBUG, message, throwable);
    }

    /**
     * Record an Info log.
     *
     * @param message The log message
     * @param args    Additional arguments
     */
    public void info(final String message, final Object... args) {
        this.logAudit(Level.INFO, message, args);
    }

    /**
     * Record an Info log.
     *
     * @param message   The log message
     * @param throwable The thrown error
     */
    public void info(final String message, final Throwable throwable) {
        this.logAudit(Level.INFO, message, throwable);
    }

    /**
     * Record a Warn log.
     *
     * @param message The log message
     * @param args    Additional arguments
     */
    public void warn(final String message, final Object... args) {
        this.logAudit(Level.WARN, message, args);
    }

    /**
     * Record a Warn log.
     *
     * @param message   The log message
     * @param throwable The thrown error
     */
    public void warn(final String message, final Throwable throwable) {
        this.logAudit(Level.WARN, message, throwable);
    }

    /**
     * Record an Error log.
     *
     * @param message The log message
     * @param args    Additional arguments
     */
    public void error(final String message, final Object... args) {
        this.logAudit(Level.ERROR, message, args);
    }

    /**
     * Record an Error log.
     *
     * @param message   The log message
     * @param throwable The thrown error
     */
    public void error(final String message, final Throwable throwable) {
        this.logAudit(Level.ERROR, message, throwable);
    }

    /**
     * Records a logged message
     *
     * @param level   The level of the logged message
     * @param message The message logged
     * @param args    Additional arguments
     */
    private void logAudit(final Level level, final String message, final Object... args) {
        MDC.put(FACILITY_KEY, AUDIT_LOG);
        MDC.put(SUBJECT_KEY, NA_SUBJECT);
        logger.makeLoggingEventBuilder(level).log(message, args);
        MDC.remove(FACILITY_KEY);
        MDC.remove(SUBJECT_KEY);
    }

    /**
     * Records a logged message
     *
     * @param level     The level of the logged message
     * @param message   The message logged
     * @param throwable The thrown error
     */
    private void logAudit(final Level level, final String message, final Throwable throwable) {
        MDC.put(FACILITY_KEY, AUDIT_LOG);
        MDC.put(SUBJECT_KEY, NA_SUBJECT);
        logger.makeLoggingEventBuilder(level).setMessage(message).setCause(throwable).log();
        MDC.remove(FACILITY_KEY);
        MDC.remove(SUBJECT_KEY);
    }

}
