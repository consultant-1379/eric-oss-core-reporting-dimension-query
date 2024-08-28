/*******************************************************************************
 * COPYRIGHT Ericsson 2021
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
package com.ericsson.oss.air.cardq.services;

import com.ericsson.oss.air.cardq.CoreApplication;
import com.ericsson.oss.air.cardq.StubRunnerTestSetup;
import com.ericsson.oss.air.cardq.cache.Graph;
import com.ericsson.oss.air.cardq.cache.Vertex;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.ericsson.oss.air.cardq.services.GraphServiceFactory.getGraphService;
import static com.ericsson.oss.air.cardq.utils.Constants.*;
import static com.ericsson.oss.air.cardq.utils.GraphServiceType.CORE;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CoreApplication.class})
public class CoreGraphServiceTest extends StubRunnerTestSetup {


    private static final Map<String, String> PLMN_INFO_100_101_3_9_ATTRIBUTES = new HashMap<>() {
        {
            put("sNSSAI_SST", "9");
            put("plmn_mcc", "100");
            put("sNSSAI_SD", "3");
            put("plmn_mnc", "101");
        }
    };

    private static final Map<String, String> PLMN_INFO_100_101_5_83_ATTRIBUTES = new HashMap<>() {
        {
            put("sNSSAI_SST", "83");
            put("plmn_mcc", "100");
            put("sNSSAI_SD", "5");
            put("plmn_mnc", "101");
        }
    };

    private static final Map<String, String> PLMN_INFO_100_101_1_5_ATTRIBUTES = new HashMap<>() {
        {
            put("sNSSAI_SST", "5");
            put("plmn_mcc", "100");
            put("sNSSAI_SD", "1");
            put("plmn_mnc", "101");
        }
    };

    private static final Map<String, String> PLMN_INFO_100_101_1_9_ATTRIBUTES = new HashMap<>() {
        {
            put("sNSSAI_SST", "9");
            put("plmn_mcc", "100");
            put("sNSSAI_SD", "1");
            put("plmn_mnc", "101");
        }
    };

    @Test
    void testCtsResponsePCC00012() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        assertThat(graph.getOrder()).isEqualTo(9);
        assertThat(graph.getStartingVertices())
                .containsOnly(new Vertex("WirelessNetworkFunction:PCC00012-AMF", "ctw/amf"));

        assertThat(graph.getAdjVertices().get(new Vertex("WirelessNetworkFunction:PCC00012-AMF", "ctw/amf")))
                .containsOnly(new Vertex("NetworkSliceSubnet:NSSI-C11", "ctw/networkslicesubnet"),
                        new Vertex("GeographicSite:DataCenter2", "ctg/geographicsite"));

        assertThat(graph.getAdjVertices("NetworkSliceSubnet:NSSI-C11", "ctw/networkslicesubnet"))
                .containsOnly(new Vertex("SliceProfile:SP-C11", "ctw/sliceprofile"),
                        new Vertex("NetworkSliceSubnet:NSSI-C1", "ctw/networkslicesubnet"));

        assertThat(graph.getAdjVertices("SliceProfile:SP-C11", "ctw/sliceprofile"))
                .containsOnly(new Vertex("PlmnInfo:100-101|3-9", "ctw/plmninfo", PLMN_INFO_100_101_3_9_ATTRIBUTES),
                        new Vertex("PlmnInfo:100-101|5-83", "ctw/plmninfo", PLMN_INFO_100_101_5_83_ATTRIBUTES));

        assertThat(graph.getAdjVertices("NetworkSliceSubnet:NSSI-C1", "ctw/networkslicesubnet"))
                .containsOnly(new Vertex("NetworkSlice:NSI-C", "ctw/networkslice"));

        assertThat(graph.getAdjVertices("NetworkSlice:NSI-C", "ctw/networkslice"))
                .containsOnly(new Vertex("ServiceProfile:SP-C", "ctw/serviceprofile"));

        assertThat(graph.getAdjVertices("ServiceProfile:SP-C", "ctw/serviceprofile"))
                .containsOnly(new Vertex("PlmnInfo:100-101|3-9", "ctw/plmninfo", PLMN_INFO_100_101_3_9_ATTRIBUTES),
                        new Vertex("PlmnInfo:100-101|5-83", "ctw/plmninfo", PLMN_INFO_100_101_5_83_ATTRIBUTES));
    }

    @Test
    public void testTransformGraphWithValidRequestWithAllAugmentationFields() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, NSSI, SITE,
                PLMNID));
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", "NetworkSlice:NSI-C"),
                        Map.of("name", NSSI, "value", "NetworkSliceSubnet:NSSI-C11"),
                        Map.of("name", PLMNID, "value", "100-101"),
                        Map.of("name", SITE, "value", "GeographicSite:DataCenter2")
                ),
                List.of(Map.of("name", NSI, "value", "NetworkSlice:NSI-C"),
                        Map.of("name", NSSI, "value", "NetworkSliceSubnet:NSSI-C1"),
                        Map.of("name", PLMNID, "value", "100-101"),
                        Map.of("name", SITE, "value", "GeographicSite:DataCenter2")
                )
        ));
    }

    @Test
    public void testTransformGraphWithNonMatchedPlmnAttributes() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=multi,ManagedElement=multi");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "50-1"), graph, List.of(NSI, NSSI, SITE,
                PLMNID));
        Assertions.assertThat(augmentation).isEmpty();
    }

    @Test
    public void testTransformGraphMethodWithRequestForPCC00012() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, NSSI, SITE,
                PLMNID));
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2"))
        ));
    }

    @Test
    void testCtsResponseNoNetworkFunctions() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC0999,ManagedElement=PCC0999");
        assertThat(graph.getOrder()).isEqualTo(0);
    }

    @Test
    void testCtsResponseMultipleNetworkFunctions() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=multi,ManagedElement=multi");
        assertThat(graph.getOrder()).isEqualTo(17);
        assertThat(graph.getStartingVertices())
                .containsOnly(new Vertex("WirelessNetworkFunction:PCC00020-SMF", "ctw/smf"),
                        new Vertex("WirelessNetworkFunction:PCC00021-SMF", "ctw/smf"),
                        new Vertex("WirelessNetworkFunction:PCG00031-UPF", "ctw/upf"));
        assertThat(graph.getAdjVertices("WirelessNetworkFunction:PCC00020-SMF", "ctw/smf"))
                .containsOnly(new Vertex("GeographicSite:DataCenter1", "ctg/geographicsite"),
                        new Vertex("NetworkSliceSubnet:NSSI-A12", "ctw/networkslicesubnet"));
        assertThat(graph.getAdjVertices("WirelessNetworkFunction:PCC00021-SMF", "ctw/smf"))
                .containsOnly(new Vertex("GeographicSite:DataCenter2", "ctg/geographicsite"),
                        new Vertex("NetworkSliceSubnet:NSSI-B12", "ctw/networkslicesubnet"));
        assertThat(graph.getAdjVertices("WirelessNetworkFunction:PCG00031-UPF", "ctw/upf"))
                .containsOnly(new Vertex("GeographicSite:DataCenter2", "ctg/geographicsite"),
                        new Vertex("NetworkSliceSubnet:NSSI-B12", "ctw/networkslicesubnet"));
    }

    @Test
    void testCtsResponseNoSliceProfiles() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=no-slice-profile,ManagedElement=no-slice-profile");
        assertThat(graph.getOrder()).isEqualTo(8);
        assertThat(graph.getStartingVertices())
                .contains(new Vertex("WirelessNetworkFunction:no-slice-profile", "ctw/smf"));

        assertThat(graph.getAdjVertices().get(new Vertex("NetworkSliceSubnet:no-slice-profile-2", "ctw/networkslicesubnet")))
                .containsOnly(new Vertex("NetworkSlice:no-slice-profile", "ctw/networkslice"));

        assertThat(graph.getAdjVertices().get(new Vertex("NetworkSliceSubnet:no-slice-profile-1", "ctw/networkslicesubnet")))
                .containsOnly(new Vertex("NetworkSliceSubnet:no-slice-profile-2", "ctw/networkslicesubnet"));

        assertThat(graph.getAdjVertices("ServiceProfile:SP-B", "ctw/serviceprofile"))
                .containsOnly(new Vertex("PlmnInfo:100-101|1-5", "ctw/plmninfo", PLMN_INFO_100_101_1_5_ATTRIBUTES),
                        new Vertex("PlmnInfo:100-101|1-9", "ctw/plmninfo", PLMN_INFO_100_101_1_9_ATTRIBUTES));

        assertThat(graph.getAdjVertices("NetworkSlice:no-slice-profile", "ctw/networkslice"))
                .containsOnly(new Vertex("ServiceProfile:SP-B", "ctw/serviceprofile"));

        assertThat(graph.getAdjVertices("WirelessNetworkFunction:no-slice-profile", "ctw/smf"))
                .containsOnly(new Vertex("NetworkSliceSubnet:no-slice-profile-1", "ctw/networkslicesubnet"),
                        new Vertex("GeographicSite:DataCenter2", "ctg/geographicsite"));

    }

    @Test
    void testCtsResponseNoServiceProfiles() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=no-service-profile,ManagedElement=no-service-profile");
        assertThat(graph.getOrder()).isEqualTo(8);

        assertThat(graph.getStartingVertices())
                .containsOnly(new Vertex("WirelessNetworkFunction:no-service-profile", "ctw/upf"));

        assertThat(graph.getAdjVertices("NetworkSliceSubnet:no-service-profile-2", "ctw/networkslicesubnet"))
                .containsOnly(new Vertex("NetworkSlice:no-service-profile", "ctw/networkslice"));

        assertThat(graph.getAdjVertices("WirelessNetworkFunction:no-service-profile", "ctw/upf"))
                .containsOnly(new Vertex("GeographicSite:DataCenter2", "ctg/geographicsite"),
                        new Vertex("NetworkSliceSubnet:no-service-profile-1", "ctw/networkslicesubnet"));

        assertThat(graph.getAdjVertices("SliceProfile:SP-C13", "ctw/sliceprofile"))
                .containsOnly(new Vertex("PlmnInfo:100-101|5-83", "ctw/plmninfo", PLMN_INFO_100_101_5_83_ATTRIBUTES),
                        new Vertex("PlmnInfo:100-101|3-9", "ctw/plmninfo", PLMN_INFO_100_101_3_9_ATTRIBUTES));

        assertThat(graph.getAdjVertices("NetworkSliceSubnet:no-service-profile-1", "ctw/networkslicesubnet"))
                .containsOnly(new Vertex("SliceProfile:SP-C13", "ctw/sliceprofile"),
                        new Vertex("NetworkSliceSubnet:no-service-profile-2", "ctw/networkslicesubnet"));

        assertThat(graph.getAdjVertices("PlmnInfo:100-101|3-9", "ctw/plmninfo")).isEmpty();

        assertThat(graph.getAdjVertices("PlmnInfo:100-101|5-83", "ctw/plmninfo")).isEmpty();

        assertThat(graph.getAdjVertices("GeographicSite:DataCenter2", "ctg/geographicsite")).isEmpty();

        assertThat(graph.getAdjVertices("NetworkSlice:no-service-profile", "ctw/networkslice")).isEmpty();

    }

    @Test
    public void testTransformGraphWithValidRequestWithOnlyNsi() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"))
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithOnlyNssi() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSSI));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11")),
                List.of(Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"))
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithOnlySite() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(SITE));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2"))
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithOnlyPlmnId() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(PLMNID));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, PLMNID, VALUE, "100-101"))
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNsiAndNssi() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, NSSI));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"))
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNsiSiteAndPlmnId() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                )
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNssiSiteAndPlmnId() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                ),
                List.of(Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                )
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNsiAndSite() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, SITE));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2"))
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNsiAndPlmnId() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, PLMNID));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"))
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNssiAndSite() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSSI, SITE));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                ),
                List.of(Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                )
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNssiAndPlmnId() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSSI, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101")
                ),
                List.of(Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101")
                )
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithSiteAndPlmnId() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2"))
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNsiNssiAndSite() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, NSSI, SITE));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                )
        ));
    }

    @Test
    public void testTransformGraphWithValidRequestWithNsiNssiAndPlmnId() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, NSSI, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101")
                )
        ));
    }

    public static Stream<Arguments> plmnInfo_1_1_and_3_1() {
        return Stream.of(
                Arguments.of("1-1"),
                Arguments.of("3-1")
        );
    }

    public static Stream<Arguments> plmnInfo_5_1_and_9_1() {
        return Stream.of(
                Arguments.of("5-1"),
                Arguments.of("9-1")
        );
    }

    public static Stream<Arguments> plmnInfo_9_3_and_83_5() {
        return Stream.of(
                Arguments.of("9-3"),
                Arguments.of("83-5")
        );
    }

    @ParameterizedTest
    @MethodSource("plmnInfo_5_1_and_9_1")
    public void testTransformGraphWithValidRequestWithAllAugmentationFieldsPCC00010(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00010,ManagedElement=PCC00010");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("plmnInfo_9_3_and_83_5")
    public void testTransformGraphWithValidRequestWithAllAugmentationFieldsPCC00012(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2"))
        ));
    }

    @ParameterizedTest
    @MethodSource("plmnInfo_1_1_and_3_1")
    public void testTransformGraphWithValidRequestWithAllAugmentationFieldsPCC00020(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00020,ManagedElement=PCC00020");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-A"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-A12"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-A"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-A1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("plmnInfo_5_1_and_9_1")
    public void testTransformGraphWithValidRequestWithAllAugmentationFieldsPCC00021(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00021,ManagedElement=PCC00021");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B12"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("plmnInfo_9_3_and_83_5")
    public void testTransformGraphWithValidRequestWithAllAugmentationFieldsPCC00022(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00022,ManagedElement=PCC00022");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C12"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("plmnInfo_1_1_and_3_1")
    public void testTransformGraphWithValidRequestWithAllAugmentationFieldsPCG00030(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCG00030,ManagedElement=PCG00030");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-A"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-A12"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-A"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-A1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("plmnInfo_5_1_and_9_1")
    public void testTransformGraphWithValidRequestWithAllAugmentationFieldsPCG00031(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCG00031,ManagedElement=PCG00031");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B12"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                )
        ));
    }

    @ParameterizedTest
    @MethodSource("plmnInfo_9_3_and_83_5")
    public void testTransformGraphWithValidRequestWithAllAugmentationFieldsPCG00032(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCG00032,ManagedElement=PCG00032");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C13"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                ),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")
                )
        ));
    }

    /**
     * Edge case scenario - snssai value has not been provided.
     * Since the snssai value is regarded as an optional value in request
     * there can be scenarios in which no snssai is provided.
     * In these scenarios we will return all networkSlice(nsi) values based on the nodefdn that was provided in the request.
     */
    @Test
    public void testTransformGraphMethodWithNoSnssaiProvidedPCC00010() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00010,ManagedElement=PCC00010");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, ""), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(4);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-A"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-A1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-A"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-A11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1"))
        ));
    }

    @Test
    public void testTransformGraphMethodWithNoSnssaiProvidedMulti() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=multi,ManagedElement=multi");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, ""), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(4);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-B"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-B12"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-A"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-A1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-A"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-A12"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter1"))
        ));
    }

    // Edge case scenario - Vertex with additional attributes
    @Test
    public void testTransformGraphMethodWithRequestForPCC00012WithAdditionalAttributes() {
        Graph graph = getGraphService(CORE).createGraph("MeContext=PCC00011,ManagedElement=PCC00011");
        Vertex entity = graph.getVertex("PlmnInfo:100-101|5-83", "ctw/plmninfo");
        entity.getAttributes().put("testAttribute1", "value1");
        entity.getAttributes().put("testAttribute2", "value2");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, "83-5"), graph, List.of(NSI, NSSI, SITE, PLMNID, "attr1", "attr2"));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:NSI-C"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:NSSI-C11"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2"))
        ));
    }

    /**
     * Edge case scenario - No service profile - when service profile information is not available with NSI
     * Augmentation response will be empty since PlmnInfo is collected from Service Profile
     */
    @ParameterizedTest
    @MethodSource("plmnInfo_9_3_and_83_5")
    void testTransformGraphMethodNoServiceProfiles(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=no-service-profile,ManagedElement=no-service-profile");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).isEmpty();
    }

    /**
     * Edge case scenario - No slice profile - when slice profile information is not available with NSSI
     * PlmnInfo is collected from Service Profile
     */
    @ParameterizedTest
    @MethodSource("plmnInfo_5_1_and_9_1")
    void testTransformGraphMethodNoSliceProfiles(String plmnInfo) {
        Graph graph = getGraphService(CORE).createGraph("MeContext=no-slice-profile,ManagedElement=no-slice-profile");
        List<List<Map<String, String>>> augmentation = new CoreGraphService().navigateGraph(Map.of(SNSSAI, plmnInfo), graph, List.of(NSI, NSSI, SITE, PLMNID));
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:no-slice-profile"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:no-slice-profile-1"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2")),
                List.of(Map.of(NAME, NSI, VALUE, "NetworkSlice:no-slice-profile"),
                        Map.of(NAME, NSSI, VALUE, "NetworkSliceSubnet:no-slice-profile-2"),
                        Map.of(NAME, PLMNID, VALUE, "100-101"),
                        Map.of(NAME, SITE, VALUE, "GeographicSite:DataCenter2"))
        ));
    }
}