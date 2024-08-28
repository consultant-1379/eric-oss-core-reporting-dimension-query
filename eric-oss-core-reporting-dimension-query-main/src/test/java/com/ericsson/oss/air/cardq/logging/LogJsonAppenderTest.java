/*******************************************************************************
 * COPYRIGHT Ericsson 2023
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

package com.ericsson.oss.air.cardq.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.LifeCycle;
import com.ericsson.oss.air.cardq.AbstractTestSetup;
import com.ericsson.oss.air.cardq.CoreApplication;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {CoreApplication.class})
@TestPropertySource(properties = "logging.config=classpath:logback-json.xml")
@Disabled("Disabling for now and it will be covered by IDUN-100538")
public class LogJsonAppenderTest extends AbstractTestSetup {
    @Test
    public void checkLoggingJsonAppender() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        Appender<ILoggingEvent> jsonAppender = lc.getLogger("root").getAppender("json");
        AssertionsForClassTypes.assertThat(jsonAppender).isNotNull().matches(LifeCycle::isStarted);
        lc.stop();
    }
}
