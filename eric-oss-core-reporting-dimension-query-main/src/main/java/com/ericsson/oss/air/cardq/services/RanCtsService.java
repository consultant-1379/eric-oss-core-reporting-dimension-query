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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.ericsson.oss.air.cardq.client.cts.CtsRestClient;
import com.ericsson.oss.air.cardq.model.cts.ctw.NRCell;
import com.ericsson.oss.air.cardq.model.cts.ctw.NetworkSliceSubnet;
import com.ericsson.oss.air.cardq.model.cts.ctw.QosProfile;

/**
 * Cts service to fetch details using CtsRestClient
 */
@Service
public class RanCtsService {
    private static final String EXTERNAL_REF_DU = "ExternalRef::nrCellDuFDN";
    private static final String EXTERNAL_REF_CU = "ExternalRef::nrCellCuFDN";
    private static final String NRCELLCU = "NRCellCU";
    private static final String NRCELL = "nrcell";
    private static final String CTW = "ctw";
    private static final String DYN_ATTRS = "dynAttrs";
    private static final String ATTRS = "attrs";
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String NETWORKSLICESUBNET = "networkslicesubnet";
    private static final String QOSPROFILE = "qosprofile";
    private static final String FS = "fs";
    private static final String FS_QOS_PROFILES = "fs.qosProfiles";
    private static final String FS_5QISETS = "fs.fiveQISets";
    private static final String FS_5QISETS_5QIFLOWS = "fs.fiveQISets.fiveQIFlows";
    private static final String FS_GNBCUCP = "fs.gnbcucp";
    private static final String FS_GNBDU = "fs.gnbdu";
    private static final String FS_GNBDU_5QISETS = "fs.gnbdu.fiveQISets";
    private static final String FS_GNBDU_5QISETS_QOSPROFILE = "fs.gnbdu.fiveQISets.qosProfile";
    private static final String FS_GNBDU_5QISETS_NSS = "fs.gnbdu.fiveQISets.qosProfile.networkSliceSubnets";
    private static final String FS_GNBDU_5QISETS_NSS_NS = "fs.gnbdu.fiveQISets.qosProfile.networkSliceSubnets.networkSlice";
    private static final String FS_GNBDU_5QISETS_NSS_NS_CSP =
            "fs.gnbdu.fiveQISets.qosProfile.networkSliceSubnets.networkSlice.capabilityServiceProfile";
    private static final String FS_GNBDU_5QISETS_5QIFLOWS = "fs.gnbdu.fiveQISets.fiveQIFlows";
    private static final String FS_GNBDU_5QISETS_RESOURCEPARTITIONS = "fs.gnbdu.fiveQISets.resourcePartitions";
    private static final String FS_GNBDU_TO_RESOURCEPARTITIONMEMBERS =
            "fs.gnbdu.fiveQISets.resourcePartitions.resourcePartitionMembers";
    private static final String FS_GNBDU_TO_PLMNINFOLIST =
            "fs.gnbdu.fiveQISets.resourcePartitions.resourcePartitionMembers.plmnInfoList";
    private static final String FS_GNBDU_TO_SLICEPROFILES =
            "fs.gnbdu.fiveQISets.resourcePartitions.resourcePartitionMembers.plmnInfoList.sliceProfiles";
    private static final String FS_GNBDU_TO_SLICEPROFILES_NSSI =
            "fs.gnbdu.fiveQISets.resourcePartitions.resourcePartitionMembers.plmnInfoList.sliceProfiles.nssHasRequirement";
    private static final String FS_GNBDU_TO_SERVICEPROFILES =
            "fs.gnbdu.fiveQISets.resourcePartitions.resourcePartitionMembers.plmnInfoList.serviceProfiles";
    private static final String FS_GNBDU_TO_SERVICEPROFILES_NSI =
            "fs.gnbdu.fiveQISets.resourcePartitions.resourcePartitionMembers.plmnInfoList.serviceProfiles.networkSliceHasCapability";
    private static final String FS_NETWORKSLICEFORNRCELL = "fs.networkSliceForNrCell";
    private static final String FS_NETWORKSLICEFORNRCELL_SERVICEPROFILE = "fs.networkSliceForNrCell.requirementServiceProfiles";
    private static final String FS_NETWORKSLICEFORNRCELL_NSS = "fs.networkSliceForNrCell.netSliceSubnets";
    private static final String FS_NSS = "fs.networkSliceSubnetForNrCell";
    private static final String FS_NSS_SLICEPROFILE = "fs.networkSliceSubnetForNrCell.requirementSliceProfiles";
    private static final String FS_NSS_QOSPROFILES = "fs.networkSliceSubnetForNrCell.qosProfiles";
    private static final String FS_PLMNINFOLIST = "fs.plmnInfoList";
    private static final String FS_PLMNINFOLIST_RESOURCEPARTITIONMEMBERS = "fs.plmnInfoList.resourcePartitionMembers";
    private static final String FS_PLMNINFOLIST_TO_RESOURCEPARTITION = "fs.plmnInfoList.resourcePartitionMembers.resourcePartition";
    private static final String FS_PLMNINFOLIST_TO_5QISET = "fs.plmnInfoList.resourcePartitionMembers.resourcePartition.fiveQISet";
    private static final String FS_PLMNINFOLIST_TO_5QIFLOW = "fs.plmnInfoList.resourcePartitionMembers.resourcePartition.fiveQISet.fiveQIFlows";
    private static final String FS_PLMNINFOLIST_TO_RESOURCEPARTITIONSET =
            "fs.plmnInfoList.resourcePartitionMembers.resourcePartition.resourcePartitionSet";
    private static final String FS_PLMNINFOLIST_SERVICEPROFILES = "fs.plmnInfoList.serviceProfiles";
    private static final String FS_PLMNINFOLIST_SERVICEPROFILES_NETWORKSLICEHASCAPABILITY =
            "fs.plmnInfoList.serviceProfiles.networkSliceHasCapability";
    private static final String FS_PLMNINFOLIST_SLICEPROFILES = "fs.plmnInfoList.sliceProfiles";
    private static final String FS_PLMNINFOLIST_SLICEPROFILES_NSSHASREQUIREMENT =
            "fs.plmnInfoList.sliceProfiles.nssHasRequirement";

    @Autowired
    private CtsRestClient ctsRestClient;

    /**
     * Used to get the NRCell
     *
     * @param externalRef used to find associated NRCell
     * @return List of NRCells
     */
    public List<NRCell> getNRCellByExternalRef(String externalRef) {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(getExterRefParam(externalRef), externalRef);
        queryParams.add(FS, DYN_ATTRS);
        queryParams.add(FS_GNBCUCP, DYN_ATTRS);
        queryParams.add(FS_GNBDU, ATTRS);
        queryParams.add(FS_GNBDU_5QISETS, ATTRS);
        queryParams.add(FS_GNBDU_5QISETS_QOSPROFILE, ATTRS);
        queryParams.add(FS_GNBDU_5QISETS_NSS, KEY);
        queryParams.add(FS_GNBDU_5QISETS_NSS_NS, ATTRS);
        queryParams.add(FS_GNBDU_5QISETS_NSS_NS_CSP, KEY);
        queryParams.add(FS_GNBDU_5QISETS_5QIFLOWS, ATTRS);
        queryParams.add(FS_GNBDU_5QISETS_RESOURCEPARTITIONS, KEY);
        queryParams.add(FS_GNBDU_TO_RESOURCEPARTITIONMEMBERS, KEY);
        queryParams.add(FS_GNBDU_TO_PLMNINFOLIST, KEY);
        queryParams.add(FS_GNBDU_TO_SLICEPROFILES, KEY);
        queryParams.add(FS_GNBDU_TO_SLICEPROFILES_NSSI, ATTRS);
        queryParams.add(FS_GNBDU_TO_SERVICEPROFILES, KEY);
        queryParams.add(FS_GNBDU_TO_SERVICEPROFILES_NSI, ATTRS);
        queryParams.add(FS_NETWORKSLICEFORNRCELL, ATTRS);
        queryParams.add(FS_NETWORKSLICEFORNRCELL_SERVICEPROFILE, ATTRS);
        queryParams.add(FS_NETWORKSLICEFORNRCELL_NSS, ATTRS);
        queryParams.add(FS_NSS, ATTRS);
        queryParams.add(FS_NSS_SLICEPROFILE, ATTRS);
        queryParams.add(FS_NSS_QOSPROFILES, ATTRS);
        queryParams.add(FS_PLMNINFOLIST, ATTRS);
        queryParams.add(FS_PLMNINFOLIST_RESOURCEPARTITIONMEMBERS, ATTRS);
        queryParams.add(FS_PLMNINFOLIST_TO_RESOURCEPARTITION, ATTRS);
        queryParams.add(FS_PLMNINFOLIST_TO_5QISET, ATTRS);
        queryParams.add(FS_PLMNINFOLIST_TO_5QIFLOW, ATTRS);
        queryParams.add(FS_PLMNINFOLIST_TO_RESOURCEPARTITIONSET, ATTRS);
        queryParams.add(FS_PLMNINFOLIST_SERVICEPROFILES, KEY);
        queryParams.add(FS_PLMNINFOLIST_SLICEPROFILES, KEY);
        queryParams.add(FS_PLMNINFOLIST_SERVICEPROFILES_NETWORKSLICEHASCAPABILITY, ATTRS);
        queryParams.add(FS_PLMNINFOLIST_SLICEPROFILES_NSSHASREQUIREMENT, KEY);

        return ctsRestClient.getResourceList(NRCell.class, queryParams, CTW, NRCELL);
    }

    /**
     * Used to select the right externalRef parameter. Defaulting to EXTERNAL_REF_DU
     *
     * @return externalRef parameter to use in the CTS query
     */
    private static String getExterRefParam(String nRCellExternalRef) {
        String externalRefparam = EXTERNAL_REF_DU;
        String lastElement = nRCellExternalRef.substring(nRCellExternalRef.lastIndexOf(',') + 1);
        String[] lastElementSplit = lastElement.split("=");

        if (lastElementSplit[0].equals(NRCELLCU)) {
            externalRefparam = EXTERNAL_REF_CU;
        }

        return externalRefparam;
    }


    /**
     * Used to get one NetworkSliceSubnet with QosProfiles by name
     * @param name of NetworkSliceSubnet used to find associated NetworkSliceSubnet
     *
     * @return List of NetworkSliceSubnet
     */
    public List<NetworkSliceSubnet> getNetworkSliceSubnetsWithQoSProfilesByName(String name) {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(NAME, name);
        queryParams.add(FS_QOS_PROFILES, "");
        return ctsRestClient.getResourceList(NetworkSliceSubnet.class, queryParams, CTW, NETWORKSLICESUBNET);
    }


    /**
     * Used to get the QosProfile with 5QISets by id
     *
     * @return List of QosProfile
     */
    public List<QosProfile> getQosProfilesWith5QISetsById(String id) {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(ID, id);
        queryParams.add(FS_5QISETS, DYN_ATTRS);
        queryParams.add(FS_5QISETS_5QIFLOWS, DYN_ATTRS);
        return ctsRestClient.getResourceList(QosProfile.class, queryParams, CTW, QOSPROFILE);
    }
}
