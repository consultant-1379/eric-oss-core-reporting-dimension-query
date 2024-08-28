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

package com.ericsson.oss.air.cardq.repository;

import static com.ericsson.oss.air.cardq.config.MeterRegistryConfiguration.NEO4J_ERROR_COUNT;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

import com.ericsson.oss.air.cardq.api.model.AugmentationField;
import com.ericsson.oss.air.cardq.config.neo4j.AthQueryConfiguration;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;
import com.ericsson.oss.air.security.utils.logging.AuditLogFactory;
import com.ericsson.oss.air.security.utils.logging.AuditLogger;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This DAO class adds a simple DAO API to encapsulate Neo4j queries.
 */
@Slf4j
@RequiredArgsConstructor
@Repository
@ConditionalOnProperty(value = "ath.enabled", havingValue = "true")
public class TopologyAugmentationDao {

    private static final String NEO4J_REQUEST_FAILURE_ERROR_MESSAGE = "Neo4j request failed";

    private static final AuditLogger AUDIT_LOGGER = AuditLogFactory.getLogger(TopologyAugmentationDao.class);

    private final AthQueryConfiguration athQueryConfiguration;

    private final Neo4jClient neo4jClient;

    private final AthQueryBuilder athQueryBuilder;

    private final MeterRegistry registry;

    /**
     * Returns List<List<{@link AugmentationField}>> which contains the query result from Neo4j
     *
     * @param graphServiceType graph service type.
     * @param parameters       map with key value pairs for query fields.
     * @return List<List < { @ link AugmentationField }>> format from the query result of Neo4j
     */
    @Retryable(retryFor = TransientDataAccessResourceException.class,
            maxAttemptsExpression = "${spring.neo4j.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${spring.neo4j.retry.delay}"))
    public List<List<AugmentationField>> findByQueryService(final GraphServiceType graphServiceType, final Map<String, String> parameters) {
        final String queryTemplate = this.athQueryConfiguration.getQuery(graphServiceType);
        final String query = this.athQueryBuilder.build(queryTemplate, parameters, graphServiceType);
        log.debug(String.format("Send query %s to Neo4j with query template %s.", query, queryTemplate));

        return this.neo4jClient.query(query)
                .fetch()
                .all()
                .stream()
                .map(stringObjectMap ->
                             stringObjectMap.entrySet()
                                     .stream()
                                     .map(entry ->
                                                  new AugmentationField().name(entry.getKey()).value(entry.getValue().toString()))
                                     .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Upon exhaustion of the retry operation in findByQueryService for retryable exception, the system will log the error, increment the error
     * count, and subsequently throw an ExternalServiceUnavailableException.
     *
     * @param e the exception
     * @return List<List < { @ link AugmentationField }>> format from the query result of Neo4j
     */
    @Recover
    public List<List<AugmentationField>> recoverFindByQueryService(final RuntimeException e) {
        this.registry.counter(NEO4J_ERROR_COUNT).increment();

        if (e instanceof TransientDataAccessResourceException) {
            AUDIT_LOGGER.error(NEO4J_REQUEST_FAILURE_ERROR_MESSAGE, e);
        }

        throw e;
    }
}
