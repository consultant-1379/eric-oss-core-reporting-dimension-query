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

import com.ericsson.oss.air.cardq.cache.Graph;
import com.ericsson.oss.air.cardq.cache.Vertex;
import com.ericsson.oss.air.cardq.model.cts.ctw.*;
import com.ericsson.oss.air.cardq.utils.CommonUtils;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.ericsson.oss.air.cardq.utils.CommonUtils.convertListsToSingleMaps;
import static com.ericsson.oss.air.cardq.utils.CommonUtils.transformMapToNameValueMap;
import static com.ericsson.oss.air.cardq.utils.Constants.*;


@Service
public class CoreGraphService implements GraphService {

    @Autowired
    private CoreCtsService coreCtsService;

    @Override
    public GraphServiceType getType() {
        return GraphServiceType.CORE;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreGraphService.class);
    private static final Map<String, String> TYPE_AUG_FIELDS_MAPPING = Map.of(CTW_PLMNINFO, PLMNID, CTW_NETWORKSLICE, NSI,
            CTW_NETWORKSLICESUBNET, NSSI,
            CTG_GEOGRAPHICSITE, SITE);

    public Graph createGraph(String nodeFdn) {
        Graph graph = new Graph();
        List<WirelessNetworkFunction> wirelessNetworkFunctions =
                coreCtsService.getWirelessNetworkFunctionByNodeFDN(nodeFdn);
        for (WirelessNetworkFunction wirelessNetworkFunction : wirelessNetworkFunctions) {
            Vertex networkFunctionVertex = new Vertex(wirelessNetworkFunction.getName(), wirelessNetworkFunction.getType());
            graph.addVertex(networkFunctionVertex);
            graph.addStartingVertex(networkFunctionVertex);

            wirelessNetworkFunction.getGeographicSites().forEach(geographicSite ->
                    updateGraphWithGeographicSite(networkFunctionVertex, new Vertex(geographicSite.getName(), geographicSite.getType()), graph));

            wirelessNetworkFunction.getSupportingNetSliceSubnets()
                    .forEach(networkSliceSubnet -> {
                        Vertex nssiVertex = new Vertex(networkSliceSubnet.getName(), networkSliceSubnet.getType());
                        updateWithGraphData(networkFunctionVertex, nssiVertex, graph, networkSliceSubnet.getId());
                    });
        }
        return graph;
    }

    /**
     * method to traverse graph and get augmented data in response
     *
     * @param attributes         input attributes from request
     * @param graph              graph to be traversed
     * @param augmentationFields augmented fields from request
     * @return response
     */
    public List<List<Map<String, String>>> navigateGraph(Map<String, String> attributes, Graph graph, List<String> augmentationFields) {
        List<List<Map<String, String>>> result = new ArrayList<>();
        Set<Vertex> verticesVisitedAlongPath = new HashSet<>();
        Map<String, String> sliceSelectionAttr = getSnssai(attributes);
        graph.getStartingVertices().forEach(vertex ->
                findAugmentationInfo(vertex, graph, sliceSelectionAttr, augmentationFields, verticesVisitedAlongPath, result, new HashMap<>()));
        return result;
    }

    /**
     * create and get snssai selection attributes from request param snssai
     *
     * @param attributes request attributes
     * @return snssai selection attributes
     */
    private static Map<String, String> getSnssai(final Map<String, String> attributes) {
        Map<String, String> sliceSelectionAttr = new HashMap<>();
        String snssai = attributes.get(SNSSAI);
        if (!snssai.isEmpty()) {
            String[] attrToMatch = snssai.split("-");
            sliceSelectionAttr.put(SNSSAI_SST, attrToMatch[0]);
            sliceSelectionAttr.put(SNSSAI_SD, attrToMatch[1]);
        }
        return sliceSelectionAttr;
    }

    /**
     * method to get the augmentation info and store as a response
     *
     * @param node                     node to be visited
     * @param graph                    graph to be traversed
     * @param sliceSelectionAttr       filter attributes to select plmn node with matched attributes
     * @param augmentationFields       augmentation fields from request
     * @param verticesVisitedAlongPath to track visited nodes
     * @param result                   to hold final response
     * @param augmentedMap             temporary map to hold augmented information
     */
    private void findAugmentationInfo(Vertex node,
                                      Graph graph,
                                      final Map<String, String> sliceSelectionAttr,
                                      final List<String> augmentationFields,
                                      final Set<Vertex> verticesVisitedAlongPath,
                                      final List<List<Map<String, String>>> result, Map<String, List<String>> augmentedMap) {

        if (!verticesVisitedAlongPath.contains(node)) {
            List<Vertex> neighbors = graph.getAdjVertices().get(node);

            boolean snssaiMatched = false;
            for (Vertex neighbor : neighbors) {
                if (CTW_PLMNINFO.equals(neighbor.getType())) {
                    boolean isServiceProfile = CTW_SERVICEPROFILE.equals(node.getType());
                    snssaiMatched = isSnssaiMatched(sliceSelectionAttr, augmentationFields, result, augmentedMap, neighbor, isServiceProfile);
                } else if (!CTW_SLICEPROFILE.equals(neighbor.getType())) {
                    String augmentationKey = TYPE_AUG_FIELDS_MAPPING.getOrDefault(neighbor.getType(), "");
                    updateAugmentedMap(augmentationKey, neighbor.getLabel(), augmentationFields, augmentedMap);
                    findAugmentationInfo(neighbor, graph, sliceSelectionAttr, augmentationFields, verticesVisitedAlongPath, result, augmentedMap);
                }

                if (snssaiMatched) {
                    break;
                }
            }
            if (!snssaiMatched) {
                augmentedMap.remove(NSSI);
                augmentedMap.remove(NSI);
            }
            verticesVisitedAlongPath.add(node);
        }
    }

    /**
     * method verifies if snssai matches plmnInfo attributes
     *
     * @param sliceSelectionAttr filter attributes to select plmn node with matched attributes
     * @param augmentationFields augmentation fields from request
     * @param result             reference variable to hold final response
     * @param augmentedMap       temporary map to hold augmented data
     * @param node               node to traverse
     * @param isServiceProfile   boolean to prepare result if snssai matched from serviceProfile
     * @return
     */
    private boolean isSnssaiMatched(final Map<String, String> sliceSelectionAttr,
                                    final List<String> augmentationFields,
                                    final List<List<Map<String, String>>> result,
                                    final Map<String, List<String>> augmentedMap,
                                    final Vertex node, boolean isServiceProfile) {
        if (CommonUtils.containsAll(node.getAttributes(), sliceSelectionAttr) || sliceSelectionAttr.isEmpty()) {

            final String plmnId = node.getAttributes().get(PLMN_MCC)
                    .concat("-".concat(node.getAttributes().get(PLMN_MNC)));
            updateAugmentedMap(PLMNID, plmnId, augmentationFields, augmentedMap);

            if (isServiceProfile) {
                List<Map<String, String>> augmentedList = convertListsToSingleMaps(augmentedMap).stream().distinct().collect(Collectors.toList());
                result.addAll(transformMapToNameValueMap(augmentedList));
            }
            return true;
        }
        return false;
    }

    /**
     * method to update temporary map with augmented data
     *
     * @param key                augmented field as key such as nssi,nsi, plmnId, site
     * @param value              extracted data from node as value for key
     * @param augmentationFields augmentation fields from request
     * @param augmentedMap       temporary map to hold augmented data
     */
    private void updateAugmentedMap(String key, String value, List<String> augmentationFields, Map<String, List<String>> augmentedMap) {
        if (augmentationFields.contains(key)) {
            List<String> list = augmentedMap.getOrDefault(key, new ArrayList<>());
            list.add(value);
            augmentedMap.put(key, list);
        }
    }

    private void updateWithGraphData(Vertex source, Vertex destination, Graph graph, Long nssiId) {

        if (source.equals(destination) || (graph.isVertexExists(destination) && graph.hasEdge(source, destination))) {
            LOGGER.debug("Vertex already exists: {}", destination.getLabel());
            return;
        }
        graph.addVertexToGraph(source, destination);

        NetworkSliceSubnet networkSliceSubnet = coreCtsService.getNetworkSliceSubnetById(Long.toString(nssiId));
        addSliceProfileInfoTopograph(destination, networkSliceSubnet.getRequirementSliceProfiles(), graph);

        networkSliceSubnet.getNetworkSlice().forEach(
                networkSlice -> updateGraphWithNetworkSliceAndServiceProfiles(destination, networkSlice, graph));

        networkSliceSubnet.getSupportingNetworkSliceSubnets().forEach(
                supportingNssi -> {
                    Vertex nssiVertex = new Vertex(supportingNssi.getName(), supportingNssi.getType());
                    updateWithGraphData(destination, nssiVertex, graph, supportingNssi.getId());
                });
    }

    private void updateGraphWithGeographicSite(Vertex source, Vertex destination, Graph graph) {
        graph.addVertexToGraph(source, destination);
    }

    private void updateGraphWithNetworkSliceAndServiceProfiles(Vertex source, NetworkSlice networkSlice, Graph graph) {
        Vertex networkSliceVertex = new Vertex(networkSlice.getName(), networkSlice.getType());
        graph.addVertexToGraph(source, networkSliceVertex);
        List<ServiceProfile> serviceProfiles = coreCtsService.getServiceProfilesByNsiId(Long.toString(networkSlice.getId()));
        addServiceProfileInfoTopograph(networkSliceVertex, serviceProfiles, graph);
    }

    private void addServiceProfileInfoTopograph(Vertex networkSliceVertex, List<ServiceProfile> serviceProfiles, Graph graph) {
        for (ServiceProfile serviceProfile : serviceProfiles) {
            Vertex serviceProfileVertex = new Vertex(serviceProfile.getName(), serviceProfile.getType());
            graph.addVertexToGraph(networkSliceVertex, serviceProfileVertex);
            addPlmnInfoToGraph(serviceProfileVertex, serviceProfile.getPlmnInfoList(), graph);
        }
    }

    private void addSliceProfileInfoTopograph(Vertex node, List<SliceProfile> sliceProfiles, Graph graph) {
        for (SliceProfile sliceProfile : sliceProfiles) {
            Vertex sliceProfileVertex = new Vertex(sliceProfile.getName(), sliceProfile.getType());
            graph.addVertexToGraph(node, sliceProfileVertex);
            addPlmnInfoToGraph(sliceProfileVertex, sliceProfile.getPlmnInfoList(), graph);
        }
    }

    private void addPlmnInfoToGraph(Vertex node, List<PlmnInfo> plmnInfoList, Graph graph) {

        for (PlmnInfo plmnInfo : plmnInfoList) {
            Map<String, String> attributes = new HashMap<>();
            attributes.put("plmn_mcc", plmnInfo.getPlmnMcc());
            attributes.put("plmn_mnc", plmnInfo.getPlmnMnc());
            attributes.put("sNSSAI_SST", plmnInfo.getSnssaiSst());
            attributes.put("sNSSAI_SD", plmnInfo.getSnssaiSd());
            graph.addVertexToGraph(node, new Vertex(plmnInfo.getName(), plmnInfo.getType(), attributes));
        }
    }
}
