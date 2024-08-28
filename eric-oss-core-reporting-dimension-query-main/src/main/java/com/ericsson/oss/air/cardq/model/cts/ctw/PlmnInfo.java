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
import com.ericsson.oss.air.cardq.model.cts.sdk.ManagedObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * PLMN Information
 * A list of entries where identifying a PLMN and optionally a slice identifier
 * ID: ctw:plmnInfo
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlmnInfo extends ManagedObject {

    @JsonProperty("plmn_mcc")
    private String plmnMcc;

    @JsonProperty("plmn_mnc")
    private String plmnMnc;

    @JsonPropertyDescription("S-NSSAI Slice/Service Type")
    @JsonProperty("sNSSAI_SST")
    private String snssaiSst;

    @JsonPropertyDescription("S-NSSAI Slice Differentiator")
    @JsonProperty("sNSSAI_SD")
    private String snssaiSd;

    private List<Binding<ServiceProfile>> serviceProfiles = new ArrayList<>();
    private List<Binding<SliceProfile>> sliceProfiles = new ArrayList<>();
    private List<Binding<ResourcePartitionMember>> resourcePartitionMembers = new ArrayList<>();

    public String snssaiToString() {
        return snssaiSst + "-" + snssaiSd;
    }

    public String plmnIdToString() {
        return plmnMcc + "-" + plmnMnc;
    }

    public List<ServiceProfile> getServiceProfiles() {
        return Binding.getValues(serviceProfiles);
    }

    public List<SliceProfile> getSliceProfiles() {
        return Binding.getValues(sliceProfiles);
    }

    public List<ResourcePartitionMember> getResourcePartitionMembers() {
        return Binding.getValues(resourcePartitionMembers);
    }
}
