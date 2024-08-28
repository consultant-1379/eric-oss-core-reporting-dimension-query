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
package com.ericsson.oss.air.cardq.model.cts.ctw;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.Link;
import com.ericsson.oss.air.cardq.model.cts.ctl3.L3IpInterface;
import com.ericsson.oss.air.cardq.model.cts.ctl3.L3IpLinkEP;
import com.ericsson.oss.air.cardq.model.cts.ctl3.L3IpServiceLinkEP;
import com.ericsson.oss.air.cardq.model.cts.sdk.ManagedObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctw:netFunctionCon
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkFunctionConnection extends Link {

    private List<Binding<L3IpInterface>> ipInts = new ArrayList<>();

    private List<Binding<L3IpLinkEP>> logicalEndPts = new ArrayList<>();

    private List<Binding<L3IpServiceLinkEP>> logicalSvcEndPts = new ArrayList<>();

    private List<Binding<ManagedObject>> wirelessNetFunctions = new ArrayList<>();

    public List<L3IpInterface> getIpInts() {
        return Binding.getValues(ipInts);
    }

    public List<L3IpLinkEP> getLogicalEndPts() {
        return Binding.getValues(logicalEndPts);
    }

    public List<L3IpServiceLinkEP> getLogicalSvcEndPts() {
        return Binding.getValues(logicalSvcEndPts);
    }

    public List<ManagedObject> getWirelessNetFunctions() {
        return Binding.getValues(wirelessNetFunctions);
    }
}
