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
 * Slice Profile
 * ID: ctw:sliceProfile
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SliceProfile extends ManagedObject {
    private List<Binding<PlmnInfo>> plmnInfoList = new ArrayList<>();
    @JsonProperty("nssHasRequirement")
    private List<Binding<NetworkSliceSubnet>> nssListR = new ArrayList<>();

    @JsonProperty("nssHasCapability")
    private List<Binding<NetworkSliceSubnet>> nssListC = new ArrayList<>();

    public List<PlmnInfo> getPlmnInfoList() {
        return Binding.getValues(plmnInfoList);
    }

    public List<NetworkSliceSubnet> getNssListR() {
        return Binding.getValues(nssListR);
    }

    public List<NetworkSliceSubnet> getNssListC() {
        return Binding.getValues(nssListC);
    }
    public List<NetworkSliceSubnet> getNssListAll() {
        List<Binding<NetworkSliceSubnet>> nssListAll = new ArrayList<>();
        nssListAll.addAll(nssListR);
        nssListAll.addAll(nssListC);
        return Binding.getValues(nssListAll.stream().distinct().collect(Collectors.toList()));
    }
}
