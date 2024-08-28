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
import com.ericsson.oss.air.cardq.model.cts.sdk.ManagedObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Resource in Common Topology Core
 * ID: ctc:resource
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resource extends ManagedObject {
    private String href;
    private String externalId;
    private List<Binding<EntityCollection>> aggregateEntities = new ArrayList<>();

    public List<EntityCollection> getAggregateEntities() {
        return Binding.getValues(aggregateEntities);
    }
}
