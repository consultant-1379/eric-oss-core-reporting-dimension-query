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
import com.ericsson.oss.air.cardq.model.cts.ctc.LogicalResource;
import com.ericsson.oss.air.cardq.model.cts.ctg.RelatedParty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctv:virtualMachine
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirtualMachine extends LogicalResource {

    private List<Binding<RelatedParty>> relatedParty = new ArrayList<>();

    private List<Binding<AvailabilityZone>> availabilityZone = new ArrayList<>();

    private List<Binding<CISMCluster>> supportedCismCluster = new ArrayList<>();

    private List<Binding<VirtualContainer>> containers = new ArrayList<>();

    private List<Binding<VimZone>> vimZone = new ArrayList<>();

    private List<Binding<VirtualMachine>> virtualMachineAffinity = new ArrayList<>();

    private List<Binding<VirtualMachine>> virtualMachineAntiaffinity = new ArrayList<>();

    private List<Binding<VirtualNetworkFunction>> vnf = new ArrayList<>();

    private List<Binding<VirtualNetworkFunctionComponent>> vnfComponents = new ArrayList<>();

    public List<AvailabilityZone> getAvailabilityZone() {
        return Binding.getValues(availabilityZone);
    }

    public List<RelatedParty> getRelatedParty() {
        return Binding.getValues(relatedParty);
    }

    public List<CISMCluster> getSupportedCismCluster() {
        return Binding.getValues(supportedCismCluster);
    }

    public List<VirtualContainer> getContainers() {
        return Binding.getValues(containers);
    }

    public List<VimZone> getVimZone() {
        return Binding.getValues(vimZone);
    }

    public List<VirtualMachine> getVirtualMachineAffinity() {
        return Binding.getValues(virtualMachineAffinity);
    }

    public List<VirtualMachine> getVirtualMachineAntiaffinity() {
        return Binding.getValues(virtualMachineAntiaffinity);
    }

    public List<VirtualNetworkFunction> getVnf() {
        return Binding.getValues(vnf);
    }

    public List<VirtualNetworkFunctionComponent> getVnfComponents() {
        return Binding.getValues(vnfComponents);
    }
}
