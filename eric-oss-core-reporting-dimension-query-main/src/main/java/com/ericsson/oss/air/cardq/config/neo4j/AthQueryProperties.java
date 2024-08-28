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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.ericsson.oss.air.cardq.config.YamlPropertySourceFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * Loads cypher query configuration properties
 */
@Setter
@Getter
@Configuration
@PropertySource(value = "file:${ath.query.path}/ath-query.yaml", factory = YamlPropertySourceFactory.class)
@ConditionalOnProperty(value = "ath.enabled", havingValue = "true")
public class AthQueryProperties {

    @Value("${athQuery.core}")
    private String coreQuery;

    @Value("${athQuery.ran}")
    private String ranQuery;

}
