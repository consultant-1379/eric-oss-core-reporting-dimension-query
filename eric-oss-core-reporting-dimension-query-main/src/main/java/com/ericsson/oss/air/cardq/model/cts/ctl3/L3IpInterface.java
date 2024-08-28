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
import com.ericsson.oss.air.cardq.model.cts.ctw.NetworkFunctionConnection;
import com.ericsson.oss.air.cardq.model.cts.ctw.WirelessNetworkFunction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctl3:l3IpInterface
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("java:S110")
public class L3IpInterface extends L3LogicalTerminationPoint {

    private List<Binding<NetworkFunctionConnection>> terminatedWirelessNFC = new ArrayList<>();

    private List<Binding<WirelessNetworkFunction>> wirelessNetworkFunc = new ArrayList<>();

    public List<NetworkFunctionConnection> getTerminatedWirelessNFC() {
        return Binding.getValues(terminatedWirelessNFC);
    }

    public List<WirelessNetworkFunction> getWirelessNetworkFunc() {
        return Binding.getValues(wirelessNetworkFunc);
    }
}
