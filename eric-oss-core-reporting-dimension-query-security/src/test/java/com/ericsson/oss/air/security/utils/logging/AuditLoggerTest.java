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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.stream.Stream;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

public class AuditLoggerTest {

    private Logger log;
    private ListAppender<ILoggingEvent> listAppender;
    private AuditLogger auditLogger;
    private final String beforeMessage = "Before Message";
    private final String auditMessage = "Audit Message";

    @BeforeEach
    void setup() {
        this.log = (Logger) LoggerFactory.getLogger(AuditLoggerTest.class);
        this.log.setLevel(Level.TRACE);
        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        this.log.addAppender(this.listAppender);
        this.auditLogger = AuditLogFactory.getLogger(AuditLoggerTest.class);
    }

    @AfterEach
    void teardown() {
        this.listAppender.stop();
    }

    static Stream<Arguments> logCalls() {
        return Stream.of(
                // No argument log message
                Arguments.of((Object) null, null),
                // Object argument log message
                Arguments.of("log Argument", null),
                // Exception argument log message
                Arguments.of(null, new Exception("test exception"))
        );
    }

    @ParameterizedTest
    @MethodSource("logCalls")
    void traceTest(Object args, Exception exception) {
        this.log.info(beforeMessage);
        if (exception == null) {
            this.auditLogger.trace(auditMessage, args);
        } else {
            this.auditLogger.trace(auditMessage, exception);
        }
        assertMessages(Level.TRACE, this.listAppender.list.get(0), this.listAppender.list.get(1));
    }

    @ParameterizedTest
    @MethodSource("logCalls")
    void debugTest(Object args, Exception exception) {
        this.log.info(beforeMessage);
        if (exception == null) {
            this.auditLogger.debug(auditMessage, args);
        } else {
            this.auditLogger.debug(auditMessage, exception);
        }
        assertMessages(Level.DEBUG, this.listAppender.list.get(0), this.listAppender.list.get(1));
    }

    @ParameterizedTest
    @MethodSource("logCalls")
    void infoTest(Object args, Exception exception) {
        this.log.info(beforeMessage);
        if (exception == null) {
            this.auditLogger.info(auditMessage, args);
        } else {
            this.auditLogger.info(auditMessage, exception);
        }
        assertMessages(Level.INFO, this.listAppender.list.get(0), this.listAppender.list.get(1));
    }

    @ParameterizedTest
    @MethodSource("logCalls")
    void warnTest(Object args, Exception exception) {
        this.log.info(beforeMessage);
        if (exception == null) {
            this.auditLogger.warn(auditMessage, args);
        } else {
            this.auditLogger.warn(auditMessage, exception);
        }
        assertMessages(Level.WARN, this.listAppender.list.get(0), this.listAppender.list.get(1));
    }

    @ParameterizedTest
    @MethodSource("logCalls")
    void errorTest(Object args, Exception exception) {
        this.log.info(beforeMessage);
        if (exception == null) {
            this.auditLogger.error(auditMessage, args);
        } else {
            this.auditLogger.error(auditMessage, exception);
        }
        assertMessages(Level.ERROR, this.listAppender.list.get(0), this.listAppender.list.get(1));
    }

    void assertMessages(Level level, ILoggingEvent preMessage, ILoggingEvent message) {
        assertNotNull(preMessage);
        assertEquals(Level.INFO, preMessage.getLevel());
        assertEquals(beforeMessage, preMessage.getFormattedMessage());
        assertTrue(preMessage.getMDCPropertyMap().isEmpty());
        assertNotNull(message);
        assertEquals(level, message.getLevel());
        assertEquals(auditMessage, message.getFormattedMessage());
        final Map<String, String> mdcProps = message.getMDCPropertyMap();
        assertFalse(mdcProps.isEmpty());
        assertEquals(2, mdcProps.size());
        assertEquals("log audit", mdcProps.get("facility"));
        assertEquals("N/A", mdcProps.get("subject"));
    }

}
