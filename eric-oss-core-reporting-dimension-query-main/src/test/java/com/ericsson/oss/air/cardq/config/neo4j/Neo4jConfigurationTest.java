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

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = Neo4jConfiguration.class)
@ActiveProfiles("test")
@EnableConfigurationProperties
class Neo4jConfigurationTest {

    @Autowired
    Neo4jConfiguration neo4jConfiguration;

    @Test
    void getAugmentationOutputTopic() {

        final Neo4jConfiguration.Config config = this.neo4jConfiguration.getConfig();
        final Neo4jConfiguration.Pool pool = this.neo4jConfiguration.getPool();

        assertFalse(ObjectUtils.isEmpty(config.getConnectionTimeout()));
        assertFalse(ObjectUtils.isEmpty(config.getMaxTransactionRetryTime()));
        assertFalse(ObjectUtils.isEmpty(pool.getMaxConnectionPoolSize()));
        assertFalse(ObjectUtils.isEmpty(pool.getMaxConnectionLifetime()));
        assertFalse(ObjectUtils.isEmpty(pool.getMaxConnectionPoolSize()));
        assertFalse(ObjectUtils.isEmpty(pool.isMetricsEnabled()));
        assertFalse(ObjectUtils.isEmpty(pool.isLogLeakedSessions()));
    }
}