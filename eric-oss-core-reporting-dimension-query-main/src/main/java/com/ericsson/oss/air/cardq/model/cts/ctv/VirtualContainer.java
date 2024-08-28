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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctv:container
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirtualContainer extends LogicalResource {

    private List<Binding<RelatedParty>> relatedParty = new ArrayList<>();

    private List<Binding<AvailabilityZone>> availabilityZone = new ArrayList<>();

    private List<Binding<CISMCluster>> cismCluster = new ArrayList<>();

    @JsonProperty("ContainerAntiaffinity")
    private List<Binding<VirtualContainer>> containerAntiaffinity = new ArrayList<>();

    private List<Binding<VirtualContainer>> containerAffinity = new ArrayList<>();

    private List<Binding<Pod>> pod = new ArrayList<>();

    private List<Binding<VirtualMachine>> virtualMachine = new ArrayList<>();

    private List<Binding<VirtualNetworkFunction>> virtualNetworkFunction = new ArrayList<>();

    private List<Binding<VirtualNetworkFunctionComponent>> vnfc = new ArrayList<>();

    public List<AvailabilityZone> getAvailabilityZone() {
        return Binding.getValues(availabilityZone);
    }

    public List<RelatedParty> getRelatedParty() {
        return Binding.getValues(relatedParty);
    }

    public List<CISMCluster> getCismCluster() {
        return Binding.getValues(cismCluster);
    }

    public List<VirtualContainer> getContainerAntiaffinity() {
        return Binding.getValues(containerAntiaffinity);
    }

    public List<VirtualContainer> getContainerAffinity() {
        return Binding.getValues(containerAffinity);
    }

    public List<Pod> getPod() {
        return Binding.getValues(pod);
    }

    public List<VirtualMachine> getVirtualMachine() {
        return Binding.getValues(virtualMachine);
    }

    public List<VirtualNetworkFunction> getVirtualNetworkFunction() {
        return Binding.getValues(virtualNetworkFunction);
    }

    public List<VirtualNetworkFunctionComponent> getVnfc() {
        return Binding.getValues(vnfc);
    }

}
