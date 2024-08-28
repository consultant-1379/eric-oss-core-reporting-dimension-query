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
package com.ericsson.oss.air.cardq.model.cts.ctw;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.Resource;
import com.ericsson.oss.air.cardq.model.cts.sdk.ManagedObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Network Slice Subnet
 * ID: ctw:networkSliceSubnet
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkSliceSubnet extends Resource {
    private List<Binding<ManagedObject>> wirelessNetFunctions = new ArrayList<>();
    private List<Binding<SliceProfile>> requirementSliceProfiles = new ArrayList<>();
    private List<Binding<NetworkSliceSubnet>> supportingNetworkSliceSubnets = new ArrayList<>();
    private List<Binding<NetworkSlice>> networkSlice = new ArrayList<>();
    private List<Binding<QosProfile>> qosProfiles = new ArrayList<>();

    public List<ManagedObject> getWirelessNetFunctions() {
        return Binding.getValues(wirelessNetFunctions);
    }

    public List<SliceProfile> getRequirementSliceProfiles() {
        return Binding.getValues(requirementSliceProfiles);
    }

    public List<NetworkSliceSubnet> getSupportingNetworkSliceSubnets() {
        return Binding.getValues(supportingNetworkSliceSubnets);
    }

    public List<NetworkSlice> getNetworkSlice() {
        return Binding.getValues(networkSlice);
    }

    public List<QosProfile> getQosProfiles() {
        return Binding.getValues(qosProfiles);
    }
}
