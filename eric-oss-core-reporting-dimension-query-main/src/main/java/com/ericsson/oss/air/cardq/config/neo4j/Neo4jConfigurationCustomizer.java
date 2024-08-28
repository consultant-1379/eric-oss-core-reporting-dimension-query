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

import java.util.concurrent.TimeUnit;

import org.neo4j.driver.Config;
import org.neo4j.driver.MetricsAdapter;
import org.springframework.boot.autoconfigure.neo4j.ConfigBuilderCustomizer;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The Neo4j configuration customizer
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class Neo4jConfigurationCustomizer implements ConfigBuilderCustomizer {

    private final Neo4jConfiguration neo4jConfiguration;

    @Override
    public void customize(final Config.ConfigBuilder configBuilder) {

        final Neo4jConfiguration.Config config = this.neo4jConfiguration.getConfig();
        final Neo4jConfiguration.Pool pool = this.neo4jConfiguration.getPool();

        configBuilder
                .withConnectionTimeout(config.getConnectionTimeout(), TimeUnit.SECONDS)
                .withConnectionAcquisitionTimeout(pool.getConnectionAcquisitionTimeout(), TimeUnit.SECONDS)
                .withMaxConnectionLifetime(pool.getMaxConnectionLifetime(), TimeUnit.SECONDS)
                .withMaxConnectionPoolSize(pool.getMaxConnectionPoolSize())
                .withMaxTransactionRetryTime(config.getMaxTransactionRetryTime(), TimeUnit.SECONDS);

        if (pool.isLogLeakedSessions()) {
            configBuilder.withLeakedSessionsLogging();
        }

        if (pool.isMetricsEnabled()) {
            configBuilder.withMetricsAdapter(MetricsAdapter.MICROMETER);
        }

        configBuilder.build();
    }
}