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
import com.ericsson.oss.air.cardq.model.cts.ctc.Link;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctl3:l3IpLink
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class L3IpLink extends Link {

    private List<Binding<L3ForwardingConstruct>> l3fcs = new ArrayList<>();

    @JsonProperty("L3IpLinkEPs")
    private List<Binding<L3IpLinkEP>> l3IpLinkEPs = new ArrayList<>();

    public List<L3ForwardingConstruct> getL3fcs() {
        return Binding.getValues(l3fcs);
    }

    public List<L3IpLinkEP> getL3IpLinkEPs() {
        return Binding.getValues(l3IpLinkEPs);
    }
}
