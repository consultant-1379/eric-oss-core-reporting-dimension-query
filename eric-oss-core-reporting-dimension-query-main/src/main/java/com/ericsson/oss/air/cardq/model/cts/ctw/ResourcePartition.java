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
package com.ericsson.oss.air.cardq.model.cts.ctw;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.Resource;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourcePartition extends Resource {
    @JsonProperty("resourcePartitionSet")
    private List<Binding<ResourcePartitionSet>> resourcePartitionSetList = new ArrayList<>();
    private List<Binding<ResourcePartitionMember>> resourcePartitionMembers = new ArrayList<>();

    @JsonProperty("fiveQISet")
    private List<Binding<FiveQiSet>> fiveQiSetList = new ArrayList<>();

    public List<ResourcePartitionSet> getResourcePartitionSetList() {
        return Binding.getValues(resourcePartitionSetList);
    }

    public List<ResourcePartitionMember> getResourcePartitionMembers() {
        return Binding.getValues(resourcePartitionMembers);
    }

    public List<FiveQiSet> getFiveQiSetList() {
        return Binding.getValues(fiveQiSetList);
    }
}
