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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctl3:l3IpServiceLink
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class L3IpServiceLink extends Link {

    private List<Binding<L3ForwardingConstruct>> l3fcs = new ArrayList<>();

    private List<Binding<L3IpServiceLinkEP>> ipSvcLinkEPs = new ArrayList<>();

    public List<L3ForwardingConstruct> getL3fcs() {
        return Binding.getValues(l3fcs);
    }
    public List<L3IpServiceLinkEP> getIpSvcLinkEPs() {
        return Binding.getValues(ipSvcLinkEPs);
    }
}
