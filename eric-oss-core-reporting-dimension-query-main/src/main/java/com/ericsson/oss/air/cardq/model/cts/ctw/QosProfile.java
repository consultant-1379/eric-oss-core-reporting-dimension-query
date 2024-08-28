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
 * QosProfile
 * ID: ctw:qosprofile
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QosProfile extends Resource {
    private List<Binding<FiveQiSet>> fiveQISets = new ArrayList<>();

    @JsonProperty("networkSliceSubnets")
    private List<Binding<NetworkSliceSubnet>> nssList = new ArrayList<>();

    public List<NetworkSliceSubnet> getNssList() {
        return Binding.getValues(nssList);
    }

    public List<FiveQiSet> getFiveQiSets() {
        return Binding.getValues(fiveQISets);
    }
}
