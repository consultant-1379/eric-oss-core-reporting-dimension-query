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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ericsson.oss.air.cardq.AbstractTestSetup;

@SpringBootTest(classes = { AthQueryProperties.class, AthQueryPropertiesTest.class }, properties = { "ath.enabled=true" })
public class AthQueryPropertiesTest extends AbstractTestSetup {

    @Autowired
    private AthQueryProperties athQueryProperties;

    @Test
    public void testInjection() {
        Assertions.assertNotNull(this.athQueryProperties.getCoreQuery());
        Assertions.assertNotNull(this.athQueryProperties.getRanQuery());
    }
}
