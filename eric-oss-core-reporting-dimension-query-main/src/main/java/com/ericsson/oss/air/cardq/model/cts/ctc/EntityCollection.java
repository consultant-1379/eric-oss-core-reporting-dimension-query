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
package com.ericsson.oss.air.cardq.model.cts.ctc;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity Collection is a Generic EAI topology model object
 * ID: ctc:entityCollection
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityCollection extends Resource {
    @JsonProperty("_type")
    private String resourceType;
    private List<Binding<Resource>> aggregatedResources = new ArrayList<>();

    /**
     * Get aggregatedResources
     *
     * @return aggregatedResources aggregatedResources
     */
    public List<Resource> getAggregatedResources() {
        return Binding.getValues(aggregatedResources);
    }
}
