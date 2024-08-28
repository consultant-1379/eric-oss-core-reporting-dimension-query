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

import com.ericsson.oss.air.cardq.client.cts.CtsRestClient;
import com.ericsson.oss.air.cardq.model.cts.ctw.NetworkSlice;
import com.ericsson.oss.air.cardq.model.cts.ctw.NetworkSliceSubnet;
import com.ericsson.oss.air.cardq.model.cts.ctw.ServiceProfile;
import com.ericsson.oss.air.cardq.model.cts.ctw.WirelessNetworkFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cts service to fetch details using CtsRestClient
 */
@Service
public class CoreCtsService {
    private static final String FS_GEOGRAPHIC_SITE = "fs.geographicSite";
    private static final String FS_SUPPORTING_NET_SLICE_SUBNETS = "fs.supportingNetSliceSubnets";
    private static final String FS_SUPPORTING_NETWORK_SLICE_SUBNET = "fs.supportingNetworkSliceSubnets";
    private static final String FS_NETWORK_SLICE = "fs.networkSlice";
    private static final String FS_SLICE_PROFILES = "fs.requirementSliceProfiles";
    private static final String FS_SLICE_PROFILES_PLMN_INFO_LIST = "fs.requirementSliceProfiles.plmnInfoList";
    private static final String FS_SERVICE_PROFILES = "fs.requirementServiceProfiles";
    private static final String FS_SERVICE_PROFILES_PLMN_INFO_LIST = "fs.requirementServiceProfiles.plmnInfoList";
    private static final String CTW = "ctw";
    private static final String NETWORK_SLICE_SUBNET = "networkslicesubnet";
    private static final String NETWORK_SLICE = "networkslice";
    private static final String WIRELESS_NET_FUNCTION = "wirelessnetfunction";
    private static final String NETWORK_SLICE_SUBNET_ID = "networkSliceSubnetId";

    @Autowired
    private CtsRestClient ctsRestClient;

    /**
     * Used to get the Wireless Functions (with associations to NSSIs)
     *
     * @param nodeFdnValue name used to find associated Wireless Network Function
     * @return List of Wireless Network Functions
     */
    public List<WirelessNetworkFunction> getWirelessNetworkFunctionByNodeFDN(String nodeFdnValue) {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        final var externalRef = "ExternalRef";
        final var nodeFDN = "nodeFDN";
        final var nodeFDNExternalRefKey = String.format("%s::%s", externalRef, nodeFDN);
        queryParams.add(FS_SUPPORTING_NET_SLICE_SUBNETS, "");
        queryParams.add(FS_GEOGRAPHIC_SITE, "");
        queryParams.add(nodeFDNExternalRefKey, nodeFdnValue);
        return ctsRestClient.getResourceList(WirelessNetworkFunction.class, queryParams, CTW, WIRELESS_NET_FUNCTION);
    }

    /**
     * Used to get Network Slice Subnet Instances (NSSI)
     * and supporting NSSIs
     *
     * @param nssiId id of nssi
     * @return NSSI with given id
     */
    public NetworkSliceSubnet getNetworkSliceSubnetById(String nssiId) {
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put(NETWORK_SLICE_SUBNET_ID, nssiId);
        final var networkSliceSubnetId = String.format("{%s}", NETWORK_SLICE_SUBNET_ID);
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(FS_SLICE_PROFILES, "");
        queryParams.add(FS_SLICE_PROFILES_PLMN_INFO_LIST, "");
        queryParams.add(FS_NETWORK_SLICE, "");
        queryParams.add(FS_SUPPORTING_NETWORK_SLICE_SUBNET, "");
        return ctsRestClient.getResource(NetworkSliceSubnet.class, queryParams, uriVariables, CTW, NETWORK_SLICE_SUBNET, networkSliceSubnetId);
    }

    /**
     * Used to get Network Slice (NSI)
     * with associations to Service Profiles with PLMN Info List
     *
     * @param nsiId id of NSI
     * @return NSI with the requested network slice id
     */
    public NetworkSlice getNetworkSliceById(String nsiId) {
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("networkSliceId", nsiId);
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(FS_SERVICE_PROFILES, "");
        queryParams.add(FS_SERVICE_PROFILES_PLMN_INFO_LIST, "");
        return ctsRestClient.getResource(NetworkSlice.class, queryParams, uriVariables, CTW, NETWORK_SLICE, "{networkSliceId}");
    }

    public List<ServiceProfile> getServiceProfilesByNsiId(String networkSliceId) {
        return new ArrayList<>(this.getNetworkSliceById(networkSliceId).getRequirementServiceProfiles());
    }
}
