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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Value;

/**
 * Neo4j Configuration.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.neo4j")
public class Neo4jConfiguration {

    private Config config;
    private Pool pool;

    @Value
    public static class Config {
        int connectionTimeout;
        int maxTransactionRetryTime;
    }

    @Value
    public static class Pool {
        boolean metricsEnabled;
        int connectionAcquisitionTimeout;
        int maxConnectionLifetime;
        boolean logLeakedSessions;
        int maxConnectionPoolSize;
    }
}
