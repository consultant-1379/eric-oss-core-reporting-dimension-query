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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Network Slice
 * ID: ctw:networkSlice
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkSlice extends Resource {
    private List<Binding<ServiceProfile>> requirementServiceProfiles = new ArrayList<>();
    private List<Binding<NetworkSliceSubnet>> netSliceSubnets = new ArrayList<>();

    public List<ServiceProfile> getRequirementServiceProfiles() {
        return Binding.getValues(requirementServiceProfiles);
    }

    public List<NetworkSliceSubnet> getNetSliceSubnets() {
        return Binding.getValues(netSliceSubnets);
    }
}
