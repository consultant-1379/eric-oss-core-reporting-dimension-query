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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.as;


import static com.ericsson.oss.air.cardq.services.GraphServiceFactory.getGraphService;

import static com.ericsson.oss.air.cardq.utils.Constants.CELLID;
import static com.ericsson.oss.air.cardq.utils.Constants.FIVEQI;
import static com.ericsson.oss.air.cardq.utils.Constants.PLMNID;
import static com.ericsson.oss.air.cardq.utils.Constants.QOS;
import static com.ericsson.oss.air.cardq.utils.Constants.SNSSAI;
import static com.ericsson.oss.air.cardq.utils.Constants.TA;
import static com.ericsson.oss.air.cardq.utils.GraphServiceType.RAN;
import static com.ericsson.oss.air.cardq.utils.Constants.GNODEB;
import static com.ericsson.oss.air.cardq.utils.Constants.NRCELL;
import static com.ericsson.oss.air.cardq.utils.Constants.NSI;
import static com.ericsson.oss.air.cardq.utils.Constants.NSSI;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ericsson.oss.air.cardq.CoreApplication;
import com.ericsson.oss.air.cardq.StubRunnerTestSetup;
import com.ericsson.oss.air.cardq.cache.Graph;
import com.ericsson.oss.air.cardq.cache.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CoreApplication.class})
class RanGraphServiceTest extends StubRunnerTestSetup {

    @Test
    void testCtsResponse209() {
        String externalRef = "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        assertThat(graph.getOrder()).isEqualTo(1);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(NRCELL, "NR01gNodeBRadio00013-3");
        attributes.put(TA, "999");
        attributes.put(CELLID, "213031");
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put("id", externalRef);
        assertThat(graph.getStartingVertices())
                .containsOnly(new Vertex("2009", "ctw/nrcell", attributes));
        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, List.of(TA));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", TA, "value", "999"))
        ));
    }

    @Test
    void testStartingVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        // check several vertices were created
        assertThat(graph.getOrder()).isGreaterThan(5);
        // check correct starting vertex was created
        assertThat(graph.getStartingVertices())
            .singleElement()
            .hasFieldOrPropertyWithValue("type", "ctw/nrcell")
            .hasFieldOrPropertyWithValue("label", "8")
            .extracting("attributes", as(InstanceOfAssertFactories.MAP))
            .containsKeys(TA, NRCELL);
    }

    @Test
    void testNrCellAdjacency() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        // check vertices adjacent to nrcell
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(NRCELL, "CBC_5G_NR_1A");
        nrcellAttributes.put(TA, "20");
        List<Vertex> nrCellChildren = graph.getAdjVertices("8", "ctw/nrcell", nrcellAttributes);
        assertThat(nrCellChildren)
            // check nrcell is linked to NetworkSlice
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/networkslice")
                    .hasFieldOrPropertyWithValue("label", "775")
                    .extracting("attributes", as(InstanceOfAssertFactories.MAP))
                    .containsKeys(NSI);
            })
            // check nrcell is linked to NetworkSliceSubnet
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/networkslicesubnet")
                    .hasFieldOrPropertyWithValue("label", "769")
                    .extracting("attributes", as(InstanceOfAssertFactories.MAP))
                    .containsKeys(NSSI);
            })
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/networkslicesubnet")
                    .hasFieldOrPropertyWithValue("label", "269")
                    .extracting("attributes", as(InstanceOfAssertFactories.MAP))
                    .containsKeys(NSSI);
            })
            // check nrcell is linked to gnbdu
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/gnbdu")
                    .hasFieldOrPropertyWithValue("label", "6")
                    .extracting("attributes", as(InstanceOfAssertFactories.MAP))
                    .containsKeys(GNODEB);
            })
            // check nrcell is linked to plmnInfo
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/plmninfo")
                    .hasFieldOrPropertyWithValue("label", "1")
                    .extracting("attributes", as(InstanceOfAssertFactories.MAP))
                    .containsKeys(PLMNID, SNSSAI);
            });
    }

    @Test
    void testNetworkSliceVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nsAttributes = new HashMap<>();
        nsAttributes.put(NSI, "nsi-1-1");
        List<Vertex> children = graph.getAdjVertices("775", "ctw/networkslice", nsAttributes);
        assertThat(children)
            // check networkSlice is linked to ServiceProfile
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/serviceprofile")
                    .hasFieldOrPropertyWithValue("label", "27");
            })
            // check networkSlice is linked to NetworkSliceSubnet
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/networkslicesubnet")
                    .hasFieldOrPropertyWithValue("label", "269");
            });
    }

    @Test
    void testNetworkSliceSubnetVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nssAttributes = new HashMap<>();
        nssAttributes.put(NSSI, "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi");
        List<Vertex> children = graph.getAdjVertices("269", "ctw/networkslicesubnet", nssAttributes);
        assertThat(children)
            // check NetworkSliceSubnet is linked to SliceProfile
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/sliceprofile")
                    .hasFieldOrPropertyWithValue("label", "22");
            })
            // check NetworkSliceSubnet is linked to qosProfile
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/qosprofile")
                    .hasFieldOrPropertyWithValue("label", "4");
            });

    }

    @Test
    void testGnbduVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> gnbduAttributes = new HashMap<>();
        gnbduAttributes.put(GNODEB, "TD3B297342");
        List<Vertex> children = graph.getAdjVertices("6", "ctw/gnbdu", gnbduAttributes);
        assertThat(children)
            // check gnbdu is linked to fiveqiset
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/fiveqiset");
            });
    }

    @Test
    void testFiveQiSetVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        List<Vertex> children = graph.getAdjVertices("9", "ctw/fiveqiset");
        assertThat(children)
            // check fiveqiset is linked to fiveqiflow
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/fiveqiflow")
                    .extracting("attributes", as(InstanceOfAssertFactories.MAP))
                    .containsKeys(FIVEQI);
            })
            // check fiveqiset is linked to resourcePartition
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/resourcepartition");
            })
            // check fiveqiset is linked to qosProfile
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/qosprofile");
            });
    }

    @Test
    void testPlmnInfoVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> plmnInfoAttributes = new HashMap<>();
        plmnInfoAttributes.put(PLMNID, "460-08");
        plmnInfoAttributes.put(SNSSAI, "2-3");
        Vertex plmnInfoVertex = graph.findVertexByAttributes("ctw/plmninfo", plmnInfoAttributes).get(0);
        List<Vertex> children = plmnInfoVertex.getAdjVertices();
        assertThat(children)
            // check plmnInfo is linked to sliceProfile
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/sliceprofile");
                assertThat(vertex.getAdjVertices()).anySatisfy(child -> {
                    assertThat(child).hasFieldOrPropertyWithValue("type", "ctw/networkslicesubnet");
                });
            })
            // check plmnInfo is linked to serviceProfile
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/serviceprofile");
            })
            // check plmnInfo is linked to resourcePartitionMember
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/resourcepartitionmember");
            });
    }

    @Test
    void testSliceProfileVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        List<Vertex> children = graph.getAdjVertices("22", "ctw/sliceprofile");
        assertThat(children)
            // check SliceProfile is linked to NetworkSliceSubnet
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/networkslicesubnet");
            });
    }

    @Test
    void testServiceProfileVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        List<Vertex> children = graph.getAdjVertices("27", "ctw/serviceprofile");
        assertThat(children)
            // check ServiceProfile is linked to NetworkSlice
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/networkslice");
            });
    }

    @Test
    void testResourcePartitionMemberVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        List<Vertex> children = graph.getAdjVertices("12", "ctw/resourcepartitionmember");
        assertThat(children)
            // check resourcePartitionMember is linked to resourcePartition
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/resourcepartition");
            });
    }

    @Test
    void testResourcePartitionVertex() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        List<Vertex> children = graph.getAdjVertices("11", "ctw/resourcepartition");
        assertThat(children)
            // check resourcePartitionMember is linked to resourcePartition
            .anySatisfy(vertex -> {
                assertThat(vertex)
                    .hasFieldOrPropertyWithValue("type", "ctw/resourcepartitionset");
            });
    }

    @Test
    void testSimpleTaAugmentation() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put("id", externalRef);

        // check augmentation of ta
        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, List.of(TA));
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", TA, "value", "20"))
        ));
    }
    @Test
    void testNsiNssiAugmentation() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(PLMNID, "460-08");
        nrcellAttributes.put(SNSSAI, "2-3");

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, List.of(NSSI, NSI));

        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"))
        ));
    }

    @Test
    void testNsiNssiQosAugmentationWithPLmnInfoInput() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(PLMNID, "460-08");
        nrcellAttributes.put(SNSSAI, "1-1");

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, List.of(QOS, NSSI, NSI, TA,
                                                                                                                           CELLID));

        Assertions.assertThat(augmentation).hasSize(3);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", QOS, "value", "5"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", QOS, "value", "51004"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void testNsiNssiAugmentationWithQosAndPLmnInfoInput() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B2973429,GNBDUFunction=1,NRCellCU=CBC_5G_NR_1A9";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(PLMNID, "460-08");
        nrcellAttributes.put(SNSSAI, "1-3");
        nrcellAttributes.put(QOS, "6");

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, List.of(NSSI, NSI, TA, CELLID));

        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void testNssiAndNoNsiFoundAugmentation() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(PLMNID, "460-08");
        nrcellAttributes.put(SNSSAI, "1-3");

        List<List<Map<String, String>>>  augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, List.of(NSSI, NSI));


        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"))
        ));
    }

    @Test
    void testPlmnInfoNotFoundAugmentation() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(PLMNID, "460-08");
        nrcellAttributes.put(SNSSAI, "2-333333");

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, List.of(NSSI, NSI));
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSSI, "value", ""),
                        Map.of("name", NSI, "value", "")
                        )
        ));
    }

    @Test
     void testNsiNssiAugmentationNoPlmnInfoOrOtherAttributesWithPlmnAugmentation() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        List augmentationList = List.of(PLMNID, SNSSAI, NSSI, NSI, TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);

        Assertions.assertThat(augmentation).hasSize(4);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "1-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", ""),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "1-2"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "1-1"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void test2Nsi2NssiAugmentationNoPlmnInfoOrOtherAttributesWithPlmnAugmentation() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B2973429,GNBDUFunction=1,NRCellCU=CBC_5G_NR_1A9";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        List augmentationList = List.of(PLMNID, SNSSAI, NSSI, NSI, TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);

        Assertions.assertThat(augmentation).hasSize(8);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "1-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", ""),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "1-2"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "1-1"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi-2"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi-2"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi-2"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi-2"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi-2"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", SNSSAI, "value", "1-1"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void testNsiNssiAugmentationNoPlmnInfoOrOtherAttributesWithPlmnAugmentationAndFiveQi() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        List augmentationList = List.of(QOS, PLMNID, SNSSAI, NSSI, NSI, TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);

        Assertions.assertThat(augmentation).hasSize(3);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "1"),
                        Map.of("name", SNSSAI, "value", "1-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", SNSSAI, "value", "1-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void test2Nsi2NssiAugmentationNoPlmnInfoOrOtherAttributesWithPlmnAugmentationAndFiveQi() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B2973429,GNBDUFunction=1,NRCellCU=CBC_5G_NR_1A9";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        List augmentationList = List.of(QOS, PLMNID, SNSSAI, NSSI, NSI, TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);

        Assertions.assertThat(augmentation).hasSize(6);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "1"),
                        Map.of("name", SNSSAI, "value", "1-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", SNSSAI, "value", "1-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi-2"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi-2"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi-2"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi-2"),
                        Map.of("name", PLMNID, "value", "460-08"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", SNSSAI, "value", "2-3"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void test2Nsi2NssiAugmentationNoPlmnInfoOrOtherAttributesWitNoAugmentation() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B2973429,GNBDUFunction=1,NRCellCU=CBC_5G_NR_1A9";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        List augmentationList = List.of(NSSI, NSI, TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);

        Assertions.assertThat(augmentation).hasSize(5);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi-2"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi-2"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi-2"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi-2"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void testNsiNssiAugmentationNoPlmnInfoOrOtherAttributesWithFiveQiAugmentationOnly() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        List augmentationList = List.of(QOS, NSSI, NSI, TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);

        Assertions.assertThat(augmentation).hasSize(4);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", "macNsiDedicated20230728x1543_qosh96mac01_nsi"),
                        Map.of("name", NSSI, "value", "macNsiDedicated20230728x1543_qosh96mac01_nssi"),
                        Map.of("name", QOS, "value", "1"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "macNsiDedicated20230728x1543_qosh96mac01_nsi"),
                        Map.of("name", NSSI, "value", "macNsiDedicated20230728x1543_qosh96mac01_nssi"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "nssiQosh96mac02"),
                        Map.of("name", QOS, "value", "1"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "nssiQosh96mac02"),
                        Map.of("name", QOS, "value", "6"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void testWithFiveQIValueAsInput() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(QOS, "6");
        List augmentationList = List.of(NSSI, NSI, TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);
        Assertions.assertThat(augmentation).hasSize(2);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", NSI, "value", ""),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_nssi_for_gaming"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305")),
                List.of(Map.of("name", NSI, "value", "esdn_r1_ts11_tc01_ranServiceProfilesService_SC_QoS041_nsi"),
                        Map.of("name", NSSI, "value", "esdn_r1_ts11_tc01_ranSliceProfilesService_SC_QoS041_nssi"),
                        Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }

    @Test
    void testWithNonExistantFiveQIValueAsInput() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(QOS, "123456");
        List augmentationList = List.of(NSSI, NSI, TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"),
                        Map.of("name", NSSI, "value", ""),
                        Map.of("name", NSI, "value", ""))
        ));
    }

    @Test
    void testWithFiveQIValueAsInputOnlyBaseAug() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        Graph graph = getGraphService(RAN).createGraph(externalRef);
        Map<String, String> nrcellAttributes = new HashMap<>();
        nrcellAttributes.put(QOS, "6");
        List augmentationList = List.of(TA, CELLID);

        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);
        Assertions.assertThat(augmentation).hasSize(1);
        Assertions.assertThat(augmentation).containsExactlyInAnyOrderElementsOf(List.of(
                List.of(Map.of("name", TA, "value", "20"),
                        Map.of("name", CELLID, "value", "8220770305"))
        ));
    }
    @Test
    void testCtsResponseNoNrcell() {
        Graph graph = getGraphService(RAN).createGraph("SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,"
                                                               + "NRCellDU=EMPTY");
        List augmentationList = List.of(NSSI, NSI, TA, CELLID);
        Map<String, String> nrcellAttributes = new HashMap<>();
        List<List<Map<String, String>>> augmentation = getGraphService(RAN).navigateGraph(nrcellAttributes, graph, augmentationList);
        assertThat(graph.getOrder()).isEqualTo(0);
        Assertions.assertThat(augmentation).hasSize(0);
    }
}
