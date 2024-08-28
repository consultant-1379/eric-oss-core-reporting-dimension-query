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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

@ExtendWith(MockitoExtension.class)
class Neo4JListenerTest {

    @Mock
    private Neo4jClient neo4jClientOld;

    @Mock
    private Neo4jClient neo4jClientNew;

    private Neo4jListener neo4JListener = new Neo4jListener(neo4jClientOld);

    @Test
    void testReloadNeo4jClient() {
        this.neo4JListener.reloadNeo4jClient(neo4jClientNew);
        assertNotNull(this.neo4JListener.getNeo4jClient());
    }

}