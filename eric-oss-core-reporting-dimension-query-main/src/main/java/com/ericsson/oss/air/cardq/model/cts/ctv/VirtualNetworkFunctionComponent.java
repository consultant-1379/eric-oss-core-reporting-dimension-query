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
package com.ericsson.oss.air.cardq.model.cts.ctv;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.HostedFunction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctv:vnfc
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirtualNetworkFunctionComponent extends HostedFunction {

    private List<Binding<VirtualContainer>> containers = new ArrayList<>();

    private List<Binding<Pod>> pods = new ArrayList<>();

    private List<Binding<VirtualMachine>> virtualMachine = new ArrayList<>();

    private List<Binding<VirtualNetworkFunction>> vnf = new ArrayList<>();

    public List<VirtualContainer> getContainers() {
        return Binding.getValues(containers);
    }
    public List<Pod> getPods() {
        return Binding.getValues(pods);
    }
    public List<VirtualMachine> getVirtualMachine() {
        return Binding.getValues(virtualMachine);
    }
    public List<VirtualNetworkFunction> getVnf() {
        return Binding.getValues(vnf);
    }
}
