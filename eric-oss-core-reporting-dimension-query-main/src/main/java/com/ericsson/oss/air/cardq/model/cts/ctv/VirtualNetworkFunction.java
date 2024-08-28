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
import com.ericsson.oss.air.cardq.model.cts.ctg.RelatedParty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctv:vnf
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirtualNetworkFunction extends HostedFunction {

    private List<Binding<CISMCluster>> cismCluster = new ArrayList<>();

    private List<Binding<RelatedParty>> relatedParties = new ArrayList<>();

    private List<Binding<AvailabilityZone>> availabilityZone = new ArrayList<>();

    private List<Binding<VirtualContainer>> containers = new ArrayList<>();

    private List<Binding<VimZone>> vimZone = new ArrayList<>();

    private List<Binding<VirtualNetworkService>> virtualNetServices = new ArrayList<>();

    private List<Binding<VirtualMachine>> virtualMachines = new ArrayList<>();

    private List<Binding<VirtualNetworkFunctionComponent>> vnfcs = new ArrayList<>();

    private List<Binding<Pod>> pods = new ArrayList<>();

    public List<Pod> getPods() {
        return Binding.getValues(pods);
    }

    public List<CISMCluster> getCismCluster() {
        return Binding.getValues(cismCluster);
    }

    public List<RelatedParty> getRelatedParties() {
        return Binding.getValues(relatedParties);
    }

    public List<AvailabilityZone> getAvailabilityZone() {
        return Binding.getValues(availabilityZone);
    }

    public List<VirtualContainer> getContainers() {
        return Binding.getValues(containers);
    }

    public List<VimZone> getVimZone() {
        return Binding.getValues(vimZone);
    }

    public List<VirtualNetworkService> getVirtualNetServices() {
        return Binding.getValues(virtualNetServices);
    }

    public List<VirtualMachine> getVirtualMachines() {
        return Binding.getValues(virtualMachines);
    }

    public List<VirtualNetworkFunctionComponent> getVnfcs() {
        return Binding.getValues(vnfcs);
    }
}
