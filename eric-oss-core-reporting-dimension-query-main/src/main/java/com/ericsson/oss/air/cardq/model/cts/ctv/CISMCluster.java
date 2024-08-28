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
import com.ericsson.oss.air.cardq.model.cts.ctg.GeographicSite;
import com.ericsson.oss.air.cardq.model.cts.ctg.RelatedParty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctv:cismCluster
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("java:S110")
public class CISMCluster extends VirtualResourceManager {

    private List<Binding<GeographicSite>> geographicSite = new ArrayList<>();

    private List<Binding<RelatedParty>> relatedParties = new ArrayList<>();

    private List<Binding<VimZone>> vimZone = new ArrayList<>();

    private List<Binding<VirtualNetworkFunction>> virtualNetFunctions = new ArrayList<>();

    public List<GeographicSite> getGeographicSite() {
        return Binding.getValues(geographicSite);
    }

    public List<RelatedParty> getRelatedParties() {
        return Binding.getValues(relatedParties);
    }

    public List<VimZone> getVimZone() {
        return Binding.getValues(vimZone);
    }

    public List<VirtualNetworkFunction> getVirtualNetFunctions() {
        return Binding.getValues(virtualNetFunctions);
    }
}
