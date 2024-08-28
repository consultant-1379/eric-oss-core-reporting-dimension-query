/*******************************************************************************
 * COPYRIGHT Ericsson 2023
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

import static com.ericsson.oss.air.cardq.utils.CommonUtils.createMappedItem;
import static com.ericsson.oss.air.cardq.utils.CommonUtils.transformMapToNameValueMap;
import static com.ericsson.oss.air.cardq.utils.Constants.CELLID;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_FIVEQIFLOW;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_FIVEQISET;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_GNDBU;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_NETWORKSLICE;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_NETWORKSLICESUBNET;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_PLMNINFO;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_QOSPROFILE;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_RESOURCEPARTITION;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_RESOURCEPARTITIONMEMBER;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_SERVICEPROFILE;
import static com.ericsson.oss.air.cardq.utils.Constants.CTW_SLICEPROFILE;
import static com.ericsson.oss.air.cardq.utils.Constants.FIVEQI;
import static com.ericsson.oss.air.cardq.utils.Constants.GNODEB;
import static com.ericsson.oss.air.cardq.utils.Constants.NRCELL;
import static com.ericsson.oss.air.cardq.utils.Constants.NSI;
import static com.ericsson.oss.air.cardq.utils.Constants.NSSI;
import static com.ericsson.oss.air.cardq.utils.Constants.PLMNID;
import static com.ericsson.oss.air.cardq.utils.Constants.QOS;
import static com.ericsson.oss.air.cardq.utils.Constants.SNSSAI;
import static com.ericsson.oss.air.cardq.utils.Constants.TA;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ericsson.oss.air.cardq.cache.Graph;
import com.ericsson.oss.air.cardq.cache.Vertex;
import com.ericsson.oss.air.cardq.model.cts.ctw.FiveQiFlow;
import com.ericsson.oss.air.cardq.model.cts.ctw.FiveQiSet;
import com.ericsson.oss.air.cardq.model.cts.ctw.GNBDU;
import com.ericsson.oss.air.cardq.model.cts.ctw.NRCell;
import com.ericsson.oss.air.cardq.model.cts.ctw.NetworkSlice;
import com.ericsson.oss.air.cardq.model.cts.ctw.NetworkSliceSubnet;
import com.ericsson.oss.air.cardq.model.cts.ctw.PlmnInfo;
import com.ericsson.oss.air.cardq.model.cts.ctw.QosProfile;
import com.ericsson.oss.air.cardq.model.cts.ctw.ResourcePartition;
import com.ericsson.oss.air.cardq.model.cts.ctw.ResourcePartitionMember;
import com.ericsson.oss.air.cardq.model.cts.ctw.ResourcePartitionSet;
import com.ericsson.oss.air.cardq.model.cts.ctw.ServiceProfile;
import com.ericsson.oss.air.cardq.model.cts.ctw.SliceProfile;
import com.ericsson.oss.air.cardq.utils.CommonUtils;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

@Service
public class RanGraphService implements GraphService {

    @Override
    public GraphServiceType getType() {
        return GraphServiceType.RAN;
    }

    @Autowired
    private RanCtsService ranCtsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RanGraphService.class);
    private static final List<String> MANDATORY_PLMNINFO_KEYS = List.of(PLMNID, SNSSAI);
    private static final List<String> AUGMENTATION_PLMNINFO_KEYS = List.of(PLMNID, SNSSAI);
    private static final List<String> GNBDU_AUG_KEYS = List.of(QOS);
    private static final List<String> NRCELL_BASE_ITEMS = List.of(TA, CELLID);
    private static final List<String> AUG_BASE_ITEMS = List.of(NSI, NSSI, TA, CELLID);

    public Graph createGraph(String nRCellExternalRef) {
        Graph graph = new Graph();
        List<NRCell> nrCells = ranCtsService.getNRCellByExternalRef(nRCellExternalRef);
        nrCells.stream().findFirst().ifPresentOrElse(
            nrCell -> addNRCellToGraph(graph, nrCell),
            () -> LOGGER.debug("NRCell {} not found", nRCellExternalRef)
        );
        return graph;
    }

    @Override
    public List<List<Map<String, String>>> navigateGraph(
            Map<String, String> attributes, Graph graph, List<String> augmentationFields) {

        List<List<Map<String, String>>> augmentedFields = new ArrayList<>();
        if (graph.getOrder() == 0) {
            return augmentedFields;
        }
        if (!augmentOnlyNrCellBaseItem(augmentationFields)) {
            augmentedFields.addAll(getListFromPlmnInfo(attributes, graph, augmentationFields));
            augmentedFields.addAll(getListFromGnbdu(attributes, graph, augmentationFields));
        }
        addNrcellBaseItems(graph, augmentedFields, augmentationFields);
        return augmentedFields.stream().distinct().collect(Collectors.toList());
    }

    private void addNrcellBaseItems(Graph graph, List<List<Map<String, String>>> augmentedFields, List<String>  augmentationFields) {
        String ta = getFirstTrackingArea(graph);
        String cellId = getFirstCellId(graph);
        if (augmentedFields.isEmpty()) {
            List<Map<String, String>> augmentationSet = new ArrayList<>();
            if (augmentationFields.contains(TA)) {
                augmentationSet.add(createMappedItem(TA, ta));
            }
            if (augmentationFields.contains(CELLID)) {
                augmentationSet.add(createMappedItem(CELLID, cellId));
            }
            augmentationFields.stream()
                    .filter(field -> !NRCELL_BASE_ITEMS.contains(field))
                    .map(field -> createMappedItem(field, ""))
                    .forEach(augmentationSet::add);
            augmentedFields.add(augmentationSet);
        } else {
            augmentedFields.forEach(augmentationSet -> {
                if (augmentationFields.contains(TA)) {
                    augmentationSet.add(createMappedItem(TA, ta));
                }
                if (augmentationFields.contains(CELLID)) {
                    augmentationSet.add(createMappedItem(CELLID, cellId));
                }
            });
        }
    }

    private String getFirstTrackingArea(Graph graph) {
        return graph.getStartingVertices().stream().map(
            vertex -> vertex.getAttributes().get(TA)).collect(Collectors.toList()).stream().findFirst().orElse("");
    }

    private String getFirstCellId(Graph graph) {
        return graph.getStartingVertices().stream().map(
            vertex -> vertex.getAttributes().get(CELLID)).collect(Collectors.toList()).stream().findFirst().orElse("");
    }

    private List<List<Map<String, String>>> getListFromPlmnInfo(Map<String, String> attributes, Graph graph,
                                                                List<String> augmentationFields) {
        List<Vertex> plmnInfoList;
        List<Map<String, String>> fullList = new LinkedList<>();

        if (attributesContainsOnlyPlmnInfo(attributes)) {
            plmnInfoList = graph.findVertexByAttributes(CTW_PLMNINFO, attributes);
        } else if (attributes.isEmpty() && augmentationFieldsContainsPlmnInfo(augmentationFields) &&
                !augFieldContainsGndbduField(augmentationFields)) {
            plmnInfoList = graph.findVertexByType(CTW_PLMNINFO);
        } else {
            return new ArrayList<>();
        }

        plmnInfoList.forEach(plmnInfo -> {
            Map<String, List<String>> augmentationSet = new HashMap<>();

            addSnssai(augmentationFields, augmentationSet, plmnInfo);
            addPlmnId(augmentationFields, augmentationSet, plmnInfo);
            addNssi(augmentationFields, augmentationSet, plmnInfo);
            addNsi(augmentationFields, augmentationSet, plmnInfo);
            addQos(augmentationFields, augmentationSet, plmnInfo);

            fullList.addAll(CommonUtils.convertListsToSingleMaps(augmentationSet));
        });
        return transformMapToNameValueMap(fullList.stream().distinct().collect(Collectors.toList()));
    }

    private boolean attributesContainsOnlyPlmnInfo(Map<String, String> attributes) {
        final AtomicBoolean containsPlmnInfo = new AtomicBoolean(true);

        if (attributes.size() != MANDATORY_PLMNINFO_KEYS.size()) {
            return false;
        }
        MANDATORY_PLMNINFO_KEYS.stream().filter(key -> !attributes.containsKey(key)).map(key -> false).forEach(containsPlmnInfo::set);
        return containsPlmnInfo.get();
    }

    private boolean attributesContainsPlmnInfo(Map<String, String> attributes) {
        final AtomicBoolean containsPlmnInfo = new AtomicBoolean(true);
        MANDATORY_PLMNINFO_KEYS.stream().filter(key -> !attributes.containsKey(key)).map(key -> false).forEach(containsPlmnInfo::set);
        return containsPlmnInfo.get();
    }

    private boolean augmentationFieldsContainsPlmnInfo(List<String> augmentationFields) {
        return augmentationFields.containsAll(AUGMENTATION_PLMNINFO_KEYS);
    }

    private List<List<Map<String, String>>> getListFromGnbdu(Map<String, String> attributes, Graph graph,
                                                             List<String> augmentationFields) {
        List<Map<String, String>> listOfAugmentationSets = new ArrayList<>();

        if (!attributesContainsOnlyPlmnInfo(attributes)) {
            List<Vertex> gNbduList = graph.findVertexByType(CTW_GNDBU);
            gNbduList.forEach(gNbdu -> {

                Collection<Vertex> fiveQISetList = gNbdu.getAdjVerticesByType(CTW_FIVEQISET);
                fiveQISetList.forEach(fiveQISet -> {
                    List<String> fiveQiValueList = new ArrayList<>();
                    final AtomicBoolean fiveQiValueMatchQos = new AtomicBoolean(false);
                    Map<String, List<String>> augmentationSet = new HashMap<>();

                    Collection<Vertex> fiveQiFlowList = fiveQISet.getAdjVerticesByType(CTW_FIVEQIFLOW);
                    fiveQiFlowList.forEach(fiveQiFlow -> {
                        String fiveQiValue = fiveQiFlow.getAttributes().get(FIVEQI);
                        fiveQiValueList.add(fiveQiValue);
                        verifyFiveQiValueMatches(fiveQiValueMatchQos, attributes, fiveQiValue);
                    });

                    getDataFromFiveQiSet(fiveQiValueMatchQos, attributes, fiveQISet, fiveQiValueList,
                                         augmentationFields, augmentationSet, listOfAugmentationSets);

                    getDataFromQoS(attributes, fiveQISet, fiveQiValueList, augmentationFields, augmentationSet, listOfAugmentationSets);
                });
            });
        }

        return transformMapToNameValueMap(listOfAugmentationSets);
    }

    void getDataFromFiveQiSet(AtomicBoolean fiveQiValueMatchQos, Map<String, String> attributes,
                              Vertex fiveQISet, List<String> fiveQiValueList, List<String> augmentationFields, Map<String,
                              List<String>> augmentationSet, List<Map<String, String>> listOfAugmentationSets) {

        if (isWantedFiveQiSet(fiveQiValueMatchQos, attributes, augmentationFields)) {
            Collection<Vertex> resourcepartitionList = fiveQISet.getAdjVerticesByType(CTW_RESOURCEPARTITION);
            resourcepartitionList.forEach(resourcepartition -> {

                Collection<Vertex> resourcepartitionMembersList = resourcepartition.getAdjVerticesByType(CTW_RESOURCEPARTITIONMEMBER);
                resourcepartitionMembersList.forEach(resourcePartitionMember -> {

                    Collection<Vertex> plmnInfoList = resourcePartitionMember.getAdjVerticesByType(CTW_PLMNINFO);
                    plmnInfoList.forEach(plmnInfo -> {
                        if (isWantedPlmnInfo(attributes, plmnInfo)) {
                            getDataFromPlmnInfo(plmnInfo, fiveQiValueList, augmentationFields,
                                augmentationSet, listOfAugmentationSets);
                        }
                    });
                });
            });
            fiveQiValueMatchQos.set(false);
        }
    }

    private boolean isWantedPlmnInfo(Map<String, String> attributes, Vertex plmnInfo) {
        boolean attrContainsPlmnId = attributesContainsPlmnInfo(attributes);
        boolean plmnIdMatch = false;
        boolean snsssaiMatch = false;
        if (attrContainsPlmnId) {
            plmnIdMatch = attributes.get(PLMNID).equals(plmnInfo.getAttributes().get(PLMNID));
            snsssaiMatch = attributes.get(SNSSAI).equals(plmnInfo.getAttributes().get(SNSSAI));
        }

        return ((!attrContainsPlmnId) || (plmnIdMatch && snsssaiMatch));
    }

    private void getDataFromPlmnInfo(Vertex plmnInfo, List<String> fiveQiValueList, List<String> augmentationFields,
                                     Map<String, List<String>> augmentationSet, List<Map<String, String>> listOfAugmentationSets) {

        addNssi(augmentationFields, augmentationSet, plmnInfo);
        addNsi(augmentationFields, augmentationSet, plmnInfo);
        addFiveQi(augmentationFields, augmentationSet, fiveQiValueList);
        addPlmnId(augmentationFields, augmentationSet, plmnInfo);
        addSnssai(augmentationFields, augmentationSet, plmnInfo);
        listOfAugmentationSets.addAll(CommonUtils.convertListsToSingleMaps(augmentationSet));
    }

    private void getDataFromQoS(Map<String, String> attributes, Vertex fiveQISet, List<String> fiveQiValueList,
                        List<String> augmentationFields, Map<String, List<String>> augmentationSet,
                        List<Map<String, String>> listOfAugmentationSets) {

        if (attributes.isEmpty() && augmentationFields.contains(QOS) && !augmentationFieldsContainsPlmnInfo(augmentationFields)) {
            Collection<Vertex> qosProfileList = fiveQISet.getAdjVerticesByType(CTW_QOSPROFILE);
            qosProfileList.forEach(qosProfile -> {

                Collection<Vertex> nss = qosProfile.getAdjVerticesByType(CTW_NETWORKSLICESUBNET);
                nss.forEach(nssi -> {

                    List<String> networkSliceSubnetList = new ArrayList<>();
                    List<String> networkSliceList = new ArrayList<>();

                    String nssiName = nssi.getAttributes().get(NSSI);
                    networkSliceSubnetList.add(nssiName);

                    Collection<Vertex> nsiList = nssi.getAdjVerticesByType(CTW_NETWORKSLICE);
                    nsiList.forEach(nsi -> {
                        String nsiName = nsi.getAttributes().get(NSI);
                        networkSliceList.add(nsiName);
                    });

                    if (augmentationFields.contains(NSSI)) {
                        augmentationSet.put(NSSI, networkSliceSubnetList);
                    }
                    if (augmentationFields.contains(NSI)) {
                        augmentationSet.put(NSI, networkSliceList);
                    }
                    addFiveQi(augmentationFields, augmentationSet, fiveQiValueList);

                    listOfAugmentationSets.addAll(CommonUtils.convertListsToSingleMaps(augmentationSet));
                });
            });
        }
    }
    private boolean augFieldContainsGndbduField(List<String>  augmentationFields) {
        return augmentationFields.stream().anyMatch(GNBDU_AUG_KEYS::contains);
    }

    private void verifyFiveQiValueMatches(AtomicBoolean fiveQiValueMatchQos, Map<String, String> attributes,
                                          String fiveQiValue) {
        if (attributes.containsKey(QOS) && attributes.get(QOS).equals(fiveQiValue)) {
            fiveQiValueMatchQos.set(true);
        }
    }

    private boolean isWantedFiveQiSet(final AtomicBoolean fiveQiValueMatchQos, Map<String, String> attributes,
                                      List<String> augmentationFields) {
        return (fiveQiValueMatchQos.get() || (attributes.isEmpty() && augmentationFields.contains(QOS)
                && augmentationFields.contains(PLMNID) && augmentationFields.contains(SNSSAI)))
                || (augmentOnlyAugBaseItem(augmentationFields) && !attributes.containsKey(QOS));
    }

    private boolean augmentOnlyAugBaseItem(List<String> augmentationFields) {

        final AtomicBoolean isOnlyBaseItem = new AtomicBoolean(true);
        final AtomicBoolean isBaseItemFound = new AtomicBoolean(false);
        augmentationFields.forEach(key -> {
            if (!AUG_BASE_ITEMS.contains(key)) {
                isOnlyBaseItem.set(false);
            } else {
                isBaseItemFound.set(true);
            }
        });
        return isOnlyBaseItem.get() && (isBaseItemFound.get() == isOnlyBaseItem.get());
    }
    private boolean augmentOnlyNrCellBaseItem(List<String> augmentationFields) {

        final AtomicBoolean isOnlyBaseItem = new AtomicBoolean(true);
        final AtomicBoolean isBaseItemFound = new AtomicBoolean(false);
        augmentationFields.forEach(key -> {
            if (!NRCELL_BASE_ITEMS.contains(key)) {
                isOnlyBaseItem.set(false);
            } else {
                isBaseItemFound.set(true);
            }
        });
        return isOnlyBaseItem.get() && (isBaseItemFound.get() == isOnlyBaseItem.get());
    }

    private void  addNssi(List<String>  augmentationFields, Map<String, List<String>> augmentationSet, Vertex plmnInfo) {
        if (augmentationFields.contains(NSSI)) {
            List<String> networkSliceSubnetList = new ArrayList<>();
            addToNetworkSliceSubnetListFromSliceProfile(plmnInfo, networkSliceSubnetList);
            augmentationSet.put(NSSI, networkSliceSubnetList);
        }
    }

    private void addToNetworkSliceSubnetListFromSliceProfile(Vertex parentVexter, List<String> networkSliceSubnetList) {
        Collection<Vertex> sliceProfiles = parentVexter.getAdjVerticesByType(CTW_SLICEPROFILE);
        sliceProfiles.forEach(sliceProfile -> {
            Collection<Vertex> sliceProfileList = sliceProfile.getAdjVerticesByType(CTW_NETWORKSLICESUBNET);
            sliceProfileList.forEach(nssi -> {
                String nssiName = nssi.getAttributes().get(NSSI);
                networkSliceSubnetList.add(nssiName);
            });
        });
    }

    private void  addNsi(List<String>  augmentationFields, Map<String, List<String>> augmentationSet, Vertex plmnInfo) {
        if (augmentationFields.contains(NSI)) {
            List<String> networkSliceList = new ArrayList<>();
            addToNetworkSliceList(plmnInfo, networkSliceList);
            augmentationSet.put(NSI, networkSliceList);
        }
    }

    private void  addQos(List<String>  augmentationFields, Map<String, List<String>> augmentationSet, Vertex plmnInfo) {
        List<String> fiveQiValueList = new ArrayList<>();
        if (augmentationFields.contains(QOS)) {
            Collection<Vertex> resourcepartitionMembersList = plmnInfo.getAdjVerticesByType(CTW_RESOURCEPARTITIONMEMBER);
            resourcepartitionMembersList.forEach(resourcepartitionMember -> {
                Collection<Vertex> resourcepartitionList = resourcepartitionMember.getAdjVerticesByType(CTW_RESOURCEPARTITION);
                resourcepartitionList.forEach(resourcepartition -> {
                    Collection<Vertex> fiveQISetList = resourcepartition.getAdjVerticesByType(CTW_FIVEQISET);
                    fiveQISetList.forEach(fiveQISet -> {
                        Collection<Vertex> fiveQiFlowList = fiveQISet.getAdjVerticesByType(CTW_FIVEQIFLOW);
                        fiveQiFlowList.forEach(fiveQiFlow -> {
                            String fiveQiValue = fiveQiFlow.getAttributes().get(FIVEQI);
                            fiveQiValueList.add(fiveQiValue);
                        });
                    });
                });
            });
            augmentationSet.put(QOS, fiveQiValueList);
        }
    }

    private void addToNetworkSliceList(Vertex plmnInfo, List<String> networkSliceList) {
        Collection<Vertex> serviceProfiles = plmnInfo.getAdjVerticesByType(CTW_SERVICEPROFILE);
        serviceProfiles.forEach(serviceProfile -> {
            Collection<Vertex> serviceProfileList = serviceProfile.getAdjVerticesByType(CTW_NETWORKSLICE);
            serviceProfileList.forEach(nsi -> {
                String nsiName = nsi.getAttributes().get(NSI);
                networkSliceList.add(nsiName);
            });
        });
    }

    private void  addPlmnId(List<String>  augmentationFields, Map<String, List<String>> augmentationSet, Vertex plmnInfo) {
        if (augmentationFields.contains(PLMNID)) {
            List<String> plmId = new ArrayList<>();
            plmId.add(plmnInfo.getAttributes().get(PLMNID));
            augmentationSet.put(PLMNID, plmId);
        }
    }

    private void  addSnssai(List<String>  augmentationFields, Map<String, List<String>> augmentationSet, Vertex plmnInfo) {
        if (augmentationFields.contains(SNSSAI)) {
            List<String> snassai = new ArrayList<>();
            snassai.add(plmnInfo.getAttributes().get(SNSSAI));
            augmentationSet.put(SNSSAI, snassai);
        }
    }

    private void addFiveQi(List<String>  augmentationFields, Map<String, List<String>> augmentationSet, List<String> fiveQiValueList) {
        if (augmentationFields.contains(QOS)) {
            augmentationSet.put(QOS, fiveQiValueList);
        }
    }



    private void addNRCellToGraph(Graph graph, NRCell nrCell) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(NRCELL, nrCell.getName());
        attributes.put(TA, nrCell.getTrackingAreaCode());
        attributes.put(CELLID, nrCell.getLocalCellIdNci());
        Vertex nrCellVertex = new Vertex(nrCell.getId().toString(), nrCell.getType(), attributes);
        graph.addVertex(nrCellVertex);
        graph.addStartingVertex(nrCellVertex);

        nrCell.getNetworkSliceList().forEach(networkSlice -> addNetworkSliceToGraph(graph, networkSlice, nrCellVertex));
        nrCell.getNssList().forEach(networkSliceSubnet -> addNssToGraph(graph, networkSliceSubnet, nrCellVertex));
        nrCell.getGnbduList().forEach(gnbdu -> addGnbduToGraph(graph, gnbdu, nrCellVertex));
        nrCell.getPlmnInfoList().forEach(plmnInfo -> addPlmnInfoToGraph(graph, plmnInfo, nrCellVertex));
    }

    private void addPlmnInfoToGraph(Graph graph, PlmnInfo plmnInfo, Vertex nrCellVertex) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(SNSSAI, plmnInfo.snssaiToString());
        attributes.put(PLMNID, plmnInfo.plmnIdToString());
        Vertex plmnInfoVertex = new Vertex(plmnInfo.getId().toString(), plmnInfo.getType(), attributes);
        graph.addVertexToGraph(nrCellVertex, plmnInfoVertex);

        plmnInfo.getServiceProfiles().forEach(serviceProfile -> addServiceProfileToGraph(graph, serviceProfile, plmnInfoVertex));
        plmnInfo.getSliceProfiles().forEach(sliceProfile -> addSliceProfileToGraph(graph, sliceProfile, plmnInfoVertex));
        plmnInfo.getResourcePartitionMembers().forEach(resourcePartitionMember ->
            addResourcePartitionMemberToGraph(graph, resourcePartitionMember, plmnInfoVertex));
    }

    private void addResourcePartitionMemberToGraph(Graph graph, ResourcePartitionMember resourcePartitionMember, Vertex plmnInfoVertex) {
        Vertex resourcePartitionMemberVertex = new Vertex(resourcePartitionMember.getId().toString(), resourcePartitionMember.getType());
        graph.addVertexToGraph(plmnInfoVertex, resourcePartitionMemberVertex);

        resourcePartitionMember.getResourcePartitionList().forEach(resourcePartition ->
            addResourcePartitionToGraph(graph, resourcePartition, resourcePartitionMemberVertex));
        resourcePartitionMember.getPlmnInfoList().forEach(plmnInfo ->
            addPlmnInfoToGraph(graph, plmnInfo, resourcePartitionMemberVertex));
    }

    private void addResourcePartitionToGraph(Graph graph, ResourcePartition resourcePartition, Vertex destination) {
        Vertex resourcePartitionVertex = new Vertex(resourcePartition.getId().toString(), resourcePartition.getType());
        graph.addVertexToGraph(destination, resourcePartitionVertex);

        resourcePartition.getResourcePartitionSetList().forEach(resourcePartitionSet ->
            addResourcePartitionSetToGraph(graph, resourcePartitionSet, resourcePartitionVertex));

        resourcePartition.getResourcePartitionMembers().forEach(resourcePartitionMember ->
            addResourcePartitionMemberToGraph(graph, resourcePartitionMember, resourcePartitionVertex));

        resourcePartition.getFiveQiSetList().forEach(fiveQiSet -> addFiveQiSetToGraph(graph, fiveQiSet, resourcePartitionVertex));
    }

    private void addResourcePartitionSetToGraph(Graph graph, ResourcePartitionSet resourcePartitionSet,
                                                Vertex resourcePartitionVertex) {
        Vertex resourcePartitionSetVertex = new Vertex(resourcePartitionSet.getId().toString(), resourcePartitionSet.getType());
        graph.addVertexToGraph(resourcePartitionVertex, resourcePartitionSetVertex);
    }

    private void addNetworkSliceToGraph(Graph graph, NetworkSlice networkSlice, Vertex nrCellVertex) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(NSI, networkSlice.getName());
        Vertex nsVertex = new Vertex(networkSlice.getId().toString(), networkSlice.getType(), attributes);
        graph.addVertexToGraph(nrCellVertex, nsVertex);

        networkSlice.getRequirementServiceProfiles().forEach(serviceProfile -> addServiceProfileToGraph(graph, serviceProfile, nsVertex));
        networkSlice.getNetSliceSubnets().forEach(nss -> addNssToGraph(graph, nss, nsVertex));
    }

    private void addServiceProfileToGraph(Graph graph, ServiceProfile serviceProfile, Vertex nsVertex) {
        Vertex serviceProfileVertex = new Vertex(serviceProfile.getId().toString(), serviceProfile.getType());
        graph.addVertexToGraph(nsVertex, serviceProfileVertex);
        serviceProfile.getNetworkSliceListC().forEach(networkSlice -> addNetworkSliceToGraph(graph, networkSlice, serviceProfileVertex));
    }

    private void addNssToGraph(Graph graph, NetworkSliceSubnet networkSliceSubnet, Vertex parentVertex) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(NSSI, networkSliceSubnet.getName());
        Vertex nssVertex = new Vertex(networkSliceSubnet.getId().toString(), networkSliceSubnet.getType(), attributes);
        graph.addVertexToGraph(parentVertex, nssVertex);

        networkSliceSubnet.getNetworkSlice().forEach(networkSlice -> addNetworkSliceToGraph(graph, networkSlice, nssVertex));
        networkSliceSubnet.getRequirementSliceProfiles().forEach(sliceProfile -> addSliceProfileToGraph(graph, sliceProfile, nssVertex));
        networkSliceSubnet.getQosProfiles().forEach(qosProfile -> addQosProfileToGraph(graph, qosProfile, nssVertex));
    }

    private void addSliceProfileToGraph(Graph graph, SliceProfile sliceProfile, Vertex nssVertex) {
        Vertex sliceProfileVertex = new Vertex(sliceProfile.getId().toString(), sliceProfile.getType());
        graph.addVertexToGraph(nssVertex, sliceProfileVertex);
        sliceProfile.getNssListR().forEach(nss -> addNssToGraph(graph, nss, sliceProfileVertex));
    }

    private void addGnbduToGraph(Graph graph, GNBDU gnbdu, Vertex nrCellVertex) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(GNODEB, gnbdu.getName());
        Vertex gnbduVertex = new Vertex(gnbdu.getId().toString(), gnbdu.getType(), attributes);
        graph.addVertexToGraph(nrCellVertex, gnbduVertex);

        gnbdu.getFiveQiSetList().forEach(fiveQiSet -> addFiveQiSetToGraph(graph, fiveQiSet, gnbduVertex));
    }

    private void addFiveQiSetToGraph(Graph graph, FiveQiSet fiveQiSet, Vertex gnbduVertex) {
        Vertex fiveQiSetVertex = new Vertex(fiveQiSet.getId().toString(), fiveQiSet.getType());
        graph.addVertexToGraph(gnbduVertex, fiveQiSetVertex);

        fiveQiSet.getFiveQiFlows().forEach(fiveQiFlow -> addFiveQiFlowToGraph(graph, fiveQiFlow, fiveQiSetVertex));
        fiveQiSet.getQosProfileList().forEach(qosProfile -> addQosProfileToGraph(graph, qosProfile, fiveQiSetVertex));
        fiveQiSet.getResourcePartitions().forEach(resourcePartition -> addResourcePartitionToGraph(graph, resourcePartition, fiveQiSetVertex));
    }

    private void addFiveQiFlowToGraph(Graph graph, FiveQiFlow fiveQiFlow, Vertex fiveQiSetVertex) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(FIVEQI, fiveQiFlow.getFiveQiValue());
        Vertex fiveQiFlowVertex = new Vertex(fiveQiFlow.getId().toString(), fiveQiFlow.getType(), attributes);
        graph.addVertexToGraph(fiveQiSetVertex, fiveQiFlowVertex);
    }

    private void addQosProfileToGraph(Graph graph, QosProfile qosProfile, Vertex fiveQiSetVertex) {
        Vertex qosProfileVertex = new Vertex(qosProfile.getId().toString(), qosProfile.getType());
        graph.addVertexToGraph(fiveQiSetVertex, qosProfileVertex);
        qosProfile.getNssList().forEach(nss -> addNssToGraph(graph, nss, qosProfileVertex));
    }

}
