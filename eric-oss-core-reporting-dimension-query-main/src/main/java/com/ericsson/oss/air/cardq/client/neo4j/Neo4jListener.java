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

package com.ericsson.oss.air.cardq.client.neo4j;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * This class enables the reloading of neo4j client object.
 */
@Slf4j
@Component
@AllArgsConstructor
public class Neo4jListener {

    @Getter
    private Neo4jClient neo4jClient;

    /**
     * Reload the neo4j client
     *
     * @param neo4jClient {@link Neo4jClient} neo4j client object
     */
    @EventListener
    public void reloadNeo4jClient(final Neo4jClient neo4jClient) {
        log.debug("Listener for Neo4jClient");
        Assert.notNull(neo4jClient, "Provided Neo4jClient is null");
        this.neo4jClient = neo4jClient;
    }

}
