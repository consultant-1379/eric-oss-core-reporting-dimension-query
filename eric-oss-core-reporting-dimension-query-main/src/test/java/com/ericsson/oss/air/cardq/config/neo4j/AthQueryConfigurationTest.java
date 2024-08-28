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

package com.ericsson.oss.air.cardq.config.neo4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ericsson.oss.air.cardq.utils.GraphServiceType;

@ExtendWith(MockitoExtension.class)
public class AthQueryConfigurationTest {

    @Mock private AthQueryProperties athQueryProperties;

    @Test
    void getQueryTest_core() {
        final AthQueryConfiguration athQueryConfiguration = new AthQueryConfiguration(this.athQueryProperties);
        when(this.athQueryProperties.getCoreQuery()).thenReturn("core query string");

        assertEquals("core query string", athQueryConfiguration.getQuery(GraphServiceType.CORE));
    }

    @Test
    void getQueryTest_ran() {
        final AthQueryConfiguration athQueryConfiguration = new AthQueryConfiguration(this.athQueryProperties);
        when(this.athQueryProperties.getRanQuery()).thenReturn("ran query string");

        assertNotNull(athQueryConfiguration.getQuery(GraphServiceType.RAN));
    }
}
