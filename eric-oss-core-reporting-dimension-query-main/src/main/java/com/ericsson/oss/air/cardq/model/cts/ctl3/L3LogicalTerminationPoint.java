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
import com.ericsson.oss.air.cardq.model.cts.ctc.LogicalTerminationPoint;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctl3:l3LogicalTerminationPoint
 */

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class L3LogicalTerminationPoint extends LogicalTerminationPoint {

    private List<Binding<IpProtocol>> ipProtocol = new ArrayList<>();

    private List<Binding<L3IpLinkEP>> l3IpLinkEPs = new ArrayList<>();

    private List<Binding<L3IpServiceLinkEP>> l3IpServiceLinkEPs = new ArrayList<>();

    private List<Binding<L3LogicalTerminationPoint>> clientL3Ltps = new ArrayList<>();

    private List<Binding<L3LogicalTerminationPoint>> serverL3Ltp = new ArrayList<>();

    private List<Binding<L3TunnelEP>> l3TunnelEPs = new ArrayList<>();

    public List<L3TunnelEP> getL3TunnelEPs() {
        return Binding.getValues(l3TunnelEPs);
    }

    public List<L3LogicalTerminationPoint> getServerL3Ltp() {
        return Binding.getValues(serverL3Ltp);
    }

    public List<L3LogicalTerminationPoint> getClientL3Ltps() {
        return Binding.getValues(clientL3Ltps);
    }

    public List<IpProtocol> getIpProtocol() {
        return Binding.getValues(ipProtocol);
    }

    public List<L3IpLinkEP> getL3IpLinkEPs() {
        return Binding.getValues(l3IpLinkEPs);
    }

    public List<L3IpServiceLinkEP> getL3IpServiceLinkEPs() {
        return Binding.getValues(l3IpServiceLinkEPs);
    }
}
