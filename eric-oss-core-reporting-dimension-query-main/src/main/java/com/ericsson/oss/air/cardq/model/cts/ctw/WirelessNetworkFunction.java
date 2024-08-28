/*******************************************************************************
 * COPYRIGHT Ericsson 2021
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

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.Resource;
import com.ericsson.oss.air.cardq.model.cts.ctg.GeographicSite;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Wireless Network Function is an abstract class that represents 5G network functions.
 * The Wireless Network Function abstract class is further extended to support NG-RAN Node and NG Core Network Function abstract classes.
 * ID: ctw:wirelessNetFunction
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WirelessNetworkFunction extends Resource {
    private List<Binding<NetworkSliceSubnet>> supportingNetSliceSubnets = new ArrayList<>();

    private List<Binding<GeographicSite>> geographicSite = new ArrayList<>();

    public List<NetworkSliceSubnet> getSupportingNetSliceSubnets() {
        return Binding.getValues(supportingNetSliceSubnets);
    }

    public List<GeographicSite> getGeographicSites() {
        return Binding.getValues(geographicSite);
    }
}
