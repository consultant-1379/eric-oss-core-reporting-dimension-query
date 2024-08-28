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

package com.ericsson.oss.air.cardq.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import static com.ericsson.oss.air.cardq.utils.Constants.NSI;
import static com.ericsson.oss.air.cardq.utils.Constants.NSSI;
import static com.ericsson.oss.air.cardq.utils.Constants.PLMNID;
import static com.ericsson.oss.air.cardq.utils.Constants.SNSSAI;
import static com.ericsson.oss.air.cardq.utils.Constants.TA;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.util.Pair;

import com.ericsson.oss.air.cardq.CoreApplication;
import com.ericsson.oss.air.cardq.StubRunnerTestSetup;
import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.cache.Graph;
import com.ericsson.oss.air.cardq.cache.Vertex;
import com.ericsson.oss.air.cardq.handler.util.mofdn.MoFdn;
import com.ericsson.oss.air.cardq.handler.util.mofdn.MoFdnFactory;
import com.ericsson.oss.air.cardq.services.CacheService;
import com.ericsson.oss.air.cardq.services.CoreGraphService;
import com.ericsson.oss.air.cardq.services.RanGraphService;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = { CoreApplication.class, CtsAugmentationRequestHandler.class })
@ExtendWith(MockitoExtension.class)
class CtsAugmentationRequestHandlerTest extends StubRunnerTestSetup {

    final String coreRequestStr = "{\"queryType\": \"core\",\"inputFields\": [{\"name\": \"nodeFDN\",\"value\": \"MeContext=PCC00010,"
            + "ManagedElement=PCC00010\"},{\"name\": "
            + "\"snssai\",\"value\": \"1-1\"}],\"augmentationFields\": [{\"name\": \"site\"}]}";

    @SpyBean
    private CacheService cacheService;

    @SpyBean
    private CoreGraphService coreGraphService;

    @SpyBean
    private RanGraphService ranGraphService;

    private final MoFdnFactory moFdnFactory = new MoFdnFactory();

    @InjectMocks
    private CtsAugmentationRequestHandler testHandler;

    @BeforeEach
    void setUp() {
        this.cacheService.initialiseCache();
        this.testHandler = spy(new CtsAugmentationRequestHandler(moFdnFactory, cacheService));
    }

    @Test
    void getIsCached_uncached() throws Exception {

        final AugmentationRequest request = new ObjectMapper().readValue(coreRequestStr, AugmentationRequest.class);

        assertFalse(this.testHandler.getIsCached(request));
    }

    @Test
    void getIsCached_cached() throws Exception {

        final AugmentationRequest request = new ObjectMapper().readValue(coreRequestStr, AugmentationRequest.class);

        doReturn(Pair.of(new Graph(), LocalDateTime.now())).when(cacheService).get(anyString());

        assertTrue(this.testHandler.getIsCached(request));
    }

    @Test
    void getCoreAugmentation() throws Exception {

        final List<List<Map<String, String>>> fields = List.of(
                List.of(Map.of("name", "site", "value", "site-A"))
        );

        final String expectedStr = "{\"fields\":[[{\"name\":\"site\",\"value\":\"site-A\"}]]}";
        final Augmentation expected = new ObjectMapper().readValue(expectedStr, Augmentation.class);

        final AugmentationRequest request = new ObjectMapper().readValue(coreRequestStr, AugmentationRequest.class);

        doReturn(new Graph()).when(this.testHandler).getGraph(any(GraphServiceType.class), anyString());
        doReturn(fields).when(this.testHandler).getAugmentationFields(any(Graph.class), eq(request));

        final Augmentation actual = this.testHandler.getAugmentation(request);

        assertEquals(expected, actual);
    }

    @Test
    void getGraph_uncached() throws Exception {

        final Graph actual = this.testHandler.getGraph(GraphServiceType.CORE, "MeContext=PCC00011,ManagedElement=PCC00011");

        assertNotNull(actual);
    }

    @Test
    void getGraph_cached() throws Exception {

        Pair<Graph, LocalDateTime> pair = Pair.of(new Graph(), LocalDateTime.now());
        doReturn(pair).when(this.cacheService).get(any());

        final Graph actual = this.testHandler.getGraph(GraphServiceType.CORE, "MeContext=PCC00011,ManagedElement=PCC00011");

        assertNotNull(actual);
    }

    @Test
    void getGraph_createGraphEmptyCache() {
        Graph dummyGraph = new Graph();
        dummyGraph.addVertex(new Vertex("ManualDataSet-WirelessNetworkFunction-amf2", "ctw/amf"));
        doReturn(dummyGraph).when(coreGraphService).createGraph("pcc-amf2");
        Graph graph = testHandler.getGraph(GraphServiceType.getByString("core"), "pcc-amf2");
        assertThat(graph.getOrder()).isEqualTo(1);
    }

    @Test
    void getAugmentationFields_core() throws Exception {

        final AugmentationRequest request = new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00011,ManagedElement=PCC00011"))
                .addInputFieldsItem(new QueryField().name("snssai").value("83-5"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));

        final Graph graph = this.testHandler.getGraph(GraphServiceType.CORE, "MeContext=PCC00011,ManagedElement=PCC00011");

        final List<List<Map<String, String>>> actual = this.testHandler.getAugmentationFields(graph, request);

        assertEquals(1, actual.size());

        final List<Map<String, String>> fieldList = actual.get(0);

        final Set<String> actualOutputFields = new HashSet<>();

        for (final Map<String, String> outputField : actual.get(0)) {
            actualOutputFields.add(outputField.get("name"));
        }

        assertTrue(actualOutputFields.contains("nsi"));
        assertTrue(actualOutputFields.contains("plmnId"));
    }

    @Test
    void getAugmentationFields_ran() throws Exception {

        final AugmentationRequest request = new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("localDn").value(
                        "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1"))
                .addInputFieldsItem(new QueryField().name("measObjLdn").value("ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(TA))
                .queryType("ran");

        final MoFdn moFdn = this.moFdnFactory.getMoFdn(request);

        final Graph graph = this.testHandler.getGraph(GraphServiceType.RAN, moFdn.get());

        final List<List<Map<String, String>>> actual = this.testHandler.getAugmentationFields(graph, request);

        assertEquals(1, actual.size());

        final Set<String> actualOutputFields = new HashSet<>();
        for (final Map<String, String> field : actual.get(0)) {
            actualOutputFields.add(field.get("name"));
        }

        assertTrue(actualOutputFields.contains("tac"));
    }

    /**
     * Test case for transforming the graph with a valid RAN request, expecting specific entity values to be set to true.
     */
    @Test
    void testAugmentationTransformGraphMethodWithValidRanRequestANdReturnEntityValueSetToTrue() {
        Graph graph = ranGraphService.createGraph(
                "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A");
        List<List<Map<String, String>>> fields = this.testHandler.getAugmentationFields(graph, getDummyRanRequestNSSIData());

        assertThat(fields).hasSize(1);
        Assertions.assertThat(fields).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", TA, "value", "20"))
        ));
    }

    /**
     * Test case for transforming the graph with a valid request, expecting specific entity values to be set to false.
     */
    @Test
    void testAugmentationTransformGraphMethodWithValidRequestANdReturnEntityValueSetToFalse() {
        AugmentationRequest augmentationRequest = new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00011,ManagedElement=PCC00011"))
                .addInputFieldsItem(new QueryField().name("snssai").value("83-5"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"));
        Graph graph = coreGraphService.createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> fields = this.testHandler.getAugmentationFields(graph, augmentationRequest);

        assertThat(fields).hasSize(1);
        assertThat(fields).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", "nsi", "value", "NetworkSlice:NSI-C")))
        );
    }

    /**
     * Utility method to provide dummy RAN augmentation request data with NSSI for testing purposes.
     *
     * @return AugmentationRequest object with dummy RAN data and NSSI.
     */
    static AugmentationRequest getDummyRanRequestNSSIData() {
        return new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("localDn").value(
                        "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1"))
                .addInputFieldsItem(new QueryField().name("measObjLdn").value("ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209"))
                .addInputFieldsItem(new QueryField().name(PLMNID).value("460-08"))
                .addInputFieldsItem(new QueryField().name(SNSSAI).value("2-3"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(TA))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(NSSI))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(NSI))
                .queryType("ran");
    }
}