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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.ericsson.oss.air.cardq.config.MeterRegistryConfiguration.NEO4J_ERROR_COUNT;
import static com.ericsson.oss.air.cardq.utils.Constants.NODE_FDN;
import static com.ericsson.oss.air.cardq.utils.Constants.SNSSAI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.ericsson.oss.air.cardq.CoreApplication;
import com.ericsson.oss.air.cardq.api.model.AugmentationField;
import com.ericsson.oss.air.cardq.config.neo4j.AthQueryConfiguration;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootTest(classes = { CoreApplication.class, TopologyAugmentationDao.class })
@TestPropertySource(properties = "ath.enabled=true")
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class TopologyAugmentationDaoTest {

    @Mock
    private Counter counter;

    @SpyBean
    private MeterRegistry meterRegistry;

    @MockBean
    private AthQueryConfiguration athQueryConfiguration;

    @MockBean
    private AthQueryBuilder athQueryBuilder;

    @MockBean
    private Neo4jClient neo4jClient;

    @Autowired
    private TopologyAugmentationDao topologyAugmentationDao;

    @BeforeEach
    void setup() {
        when(this.meterRegistry.counter(NEO4J_ERROR_COUNT)).thenReturn(this.counter);
    }

    @Test
    void testFindByQueryService() {

        final Map<String, String> parameters = Map.of(NODE_FDN, "MeContext=PCG00032,ManagedElement=PCC00032", SNSSAI, "83-5");

        final Neo4jClient.UnboundRunnableSpec unboundRunnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class);
        final Neo4jClient.RecordFetchSpec recordFetchSpec = mock(Neo4jClient.RecordFetchSpec.class);

        when(this.neo4jClient.query(nullable(String.class))).thenReturn(unboundRunnableSpec);
        when(unboundRunnableSpec.fetch()).thenReturn(recordFetchSpec);
        when(recordFetchSpec.all()).thenReturn(List.of(parameters));

        final var fieldList = this.topologyAugmentationDao.findByQueryService(GraphServiceType.CORE, parameters);
        assertEquals(1, fieldList.size());

        assertTrue(fieldList.get(0).containsAll(List.of(
                new AugmentationField().name(NODE_FDN).value("MeContext=PCG00032,ManagedElement=PCC00032"),
                new AugmentationField().name(SNSSAI).value("83-5")
        )));
    }

    @Test
    void testFindByQueryService_ErrorCount() {
        when(this.neo4jClient.query(nullable(String.class))).thenThrow(RuntimeException.class);
        Assertions.assertThrows(RuntimeException.class, () -> this.topologyAugmentationDao.findByQueryService(GraphServiceType.CORE,
                                                                                                              new HashMap<>()));

        verify(this.counter, times(1)).increment();
        verify(this.neo4jClient, times(1)).query(nullable(String.class));
    }

    @Test
    void testFindByQueryService_ErrorCountWithRetry(final CapturedOutput output) {
        when(this.neo4jClient.query(nullable(String.class))).thenThrow(TransientDataAccessResourceException.class);

        Assertions.assertThrows(TransientDataAccessResourceException.class,
                                () -> this.topologyAugmentationDao.findByQueryService(GraphServiceType.CORE, new HashMap<>()));
        verify(this.counter, times(1)).increment();
        verify(this.neo4jClient, times(5)).query(nullable(String.class));
        assertThat(output).contains("Neo4j request failed");
    }
}