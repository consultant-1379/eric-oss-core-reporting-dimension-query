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

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.Resource;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 5QISet
 * ID: ctw:fiveqiset
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FiveQiSet extends Resource {
    @JsonProperty("fiveQIFlows")
    private List<Binding<FiveQiFlow>> fiveQiFlows = new ArrayList<>();
    @JsonProperty("qosProfile")
    private List<Binding<QosProfile>> qosProfileList = new ArrayList<>();
    private List<Binding<ResourcePartition>> resourcePartitions = new ArrayList<>();

    public List<FiveQiFlow> getFiveQiFlows() {
        return Binding.getValues(fiveQiFlows);
    }

    public List<QosProfile> getQosProfileList() {
        return Binding.getValues(qosProfileList);
    }

    public List<ResourcePartition> getResourcePartitions() {
        return Binding.getValues(resourcePartitions);
    }
}
