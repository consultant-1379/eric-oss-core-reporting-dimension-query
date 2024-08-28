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
package com.ericsson.oss.air.cardq.model.cts.ctl3;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.ForwardingConstruct;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctl3:l3ForwardingConstruct
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class L3ForwardingConstruct extends ForwardingConstruct {

    private List<Binding<L3ForwardingConstruct>> higherLevelL3Fc = new ArrayList<>();

    private List<Binding<L3ForwardingConstruct>> lowerLevelL3FCs = new ArrayList<>();

    private List<Binding<L3IpLink>> ipLink = new ArrayList<>();

    private List<Binding<L3IpServiceLink>> ipSvcLinkEPs = new ArrayList<>();

    public List<L3ForwardingConstruct> getHigherLevelL3Fc() {
        return Binding.getValues(higherLevelL3Fc);
    }

    public List<L3ForwardingConstruct> getLowerLevelL3FCs() {
        return Binding.getValues(lowerLevelL3FCs);
    }

    public List<L3IpLink> getIpLink() {
        return Binding.getValues(ipLink);
    }

    public List<L3IpServiceLink> getIpSvcLinkEPs() {
        return Binding.getValues(ipSvcLinkEPs);
    }
}
