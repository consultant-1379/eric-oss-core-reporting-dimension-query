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
package com.ericsson.oss.air.cardq.model.cts.ctg;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.EntityCollection;
import com.ericsson.oss.air.cardq.model.cts.ctl3.L3Node;
import com.ericsson.oss.air.cardq.model.cts.ctv.AvailabilityZone;
import com.ericsson.oss.air.cardq.model.cts.ctv.CISMCluster;
import com.ericsson.oss.air.cardq.model.cts.ctv.VimZone;
import com.ericsson.oss.air.cardq.model.cts.ctw.RFCell;
import com.ericsson.oss.air.cardq.model.cts.ctw.WirelessNetworkFunction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctg:geographicSite
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeographicSite extends Place {

    private List<Binding<L3Node>> l3Nodes = new ArrayList<>();

    private List<Binding<EntityCollection>> entityCollections = new ArrayList<>();

    private List<Binding<GeographicSite>> childSites = new ArrayList<>();

    private List<Binding<GeographicSite>> parentSite = new ArrayList<>();

    private List<Binding<RelatedParty>> relatedParties = new ArrayList<>();

    private List<Binding<AvailabilityZone>> availabilityZones = new ArrayList<>();

    private List<Binding<CISMCluster>> cismClusters = new ArrayList<>();

    private List<Binding<VimZone>> vimZones = new ArrayList<>();

    private List<Binding<RFCell>> rfCells = new ArrayList<>();

    private List<Binding<WirelessNetworkFunction>> wirelessNetFunctions = new ArrayList<>();

    public List<L3Node> getL3Nodes() {
        return Binding.getValues(l3Nodes);
    }

    public List<EntityCollection> getEntityCollections() {
        return Binding.getValues(entityCollections);
    }

    public List<GeographicSite> getChildSites() {
        return Binding.getValues(childSites);
    }

    public List<GeographicSite> getParentSite() {
        return Binding.getValues(parentSite);
    }

    public List<RelatedParty> getRelatedParties() {
        return Binding.getValues(relatedParties);
    }

    public List<AvailabilityZone> getAvailabilityZones() {
        return Binding.getValues(availabilityZones);
    }

    public List<CISMCluster> getCismClusters() {
        return Binding.getValues(cismClusters);
    }

    public List<VimZone> getVimZones() {
        return Binding.getValues(vimZones);
    }

    public List<RFCell> getRfCells() {
        return Binding.getValues(rfCells);
    }

    public List<WirelessNetworkFunction> getWirelessNetFunctions() {
        return Binding.getValues(wirelessNetFunctions);
    }
}
