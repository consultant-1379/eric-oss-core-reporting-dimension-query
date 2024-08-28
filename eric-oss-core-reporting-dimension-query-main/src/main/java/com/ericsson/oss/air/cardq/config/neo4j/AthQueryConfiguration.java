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

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.ericsson.oss.air.cardq.utils.GraphServiceType;

/**
 * Cypher query configuration bean
 */
@Configuration
@ConditionalOnProperty(value = "ath.enabled", havingValue = "true")
public class AthQueryConfiguration {

    private final AthQueryProperties athQueryProperties;

    public AthQueryConfiguration(AthQueryProperties athQueryProperties) {
        this.athQueryProperties = athQueryProperties;
    }

    /**
     * Gets Cypher query string
     *
     * @param graphType
     *     {@link GraphServiceType}
     * @return query string
     */
    public String getQuery(final GraphServiceType graphType) {
        return switch (graphType) {
            case CORE -> this.athQueryProperties.getCoreQuery();
            case RAN -> this.athQueryProperties.getRanQuery();
        };
    }
}
