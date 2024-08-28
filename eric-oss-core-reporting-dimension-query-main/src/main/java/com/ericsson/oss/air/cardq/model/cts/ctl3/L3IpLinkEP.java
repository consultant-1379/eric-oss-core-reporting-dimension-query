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
import com.ericsson.oss.air.cardq.model.cts.ctc.LinkEndPoint;
import com.ericsson.oss.air.cardq.model.cts.ctw.NetworkFunctionConnection;
import com.ericsson.oss.air.cardq.model.cts.ctw.WirelessNetworkFunction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctl3:l3IpLinkEP
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class L3IpLinkEP extends LinkEndPoint {

    @JsonProperty("IpLink")
    private List<Binding<L3IpLink>> ipLink = new ArrayList<>();

    private List<Binding<L3LogicalTerminationPoint>> l3ltps = new ArrayList<>();

    private List<Binding<NetworkFunctionConnection>> terminatedWirelessNFConn = new ArrayList<>();

    private List<Binding<WirelessNetworkFunction>> wirelessNetworkFunction = new ArrayList<>();

    public List<L3IpLink> getIpLink() {
        return Binding.getValues(ipLink);
    }

    public List<L3LogicalTerminationPoint> getL3ltps() {
        return Binding.getValues(l3ltps);
    }

    public List<NetworkFunctionConnection> getTerminatedWirelessNFConn() {
        return Binding.getValues(terminatedWirelessNFConn);
    }

    public List<WirelessNetworkFunction> getWirelessNetworkFunction() {
        return Binding.getValues(wirelessNetworkFunction);
    }
}
