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
import java.util.stream.Collectors;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.sdk.ManagedObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Service Profile
 * ID: ctw:serviceProfile
 */

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceProfile extends ManagedObject {
    private List<Binding<PlmnInfo>> plmnInfoList = new ArrayList<>();
    @JsonProperty("networkSliceHasCapability")
    private List<Binding<NetworkSlice>> networkSliceListC = new ArrayList<>();

    @JsonProperty("networkSliceHasRequirement")
    private List<Binding<NetworkSlice>> networkSliceListR = new ArrayList<>();

    public List<PlmnInfo> getPlmnInfoList() {
        return Binding.getValues(plmnInfoList);
    }

    public List<NetworkSlice> getNetworkSliceListR() {
        return Binding.getValues(networkSliceListR);
    }

    public List<NetworkSlice> getNetworkSliceListC() {
        return Binding.getValues(networkSliceListC);
    }

    public List<NetworkSlice> getNetworkSliceListAll() {
        List<Binding<NetworkSlice>> networkSliceListAll = new ArrayList<>();
        networkSliceListAll.addAll(networkSliceListR);
        networkSliceListAll.addAll(networkSliceListC);
        return Binding.getValues(networkSliceListAll.stream().distinct().collect(Collectors.toList()));
    }

}
