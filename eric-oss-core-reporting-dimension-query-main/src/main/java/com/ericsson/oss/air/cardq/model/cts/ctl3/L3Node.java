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
import com.ericsson.oss.air.cardq.model.cts.ctc.TopologyContainer;
import com.ericsson.oss.air.cardq.model.cts.ctg.GeographicSite;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctl3:l3Node
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class L3Node extends TopologyContainer {

    private List<Binding<GeographicSite>> geographicSite = new ArrayList<>();

    private List<Binding<L3IpInterface>> ipInterfaces = new ArrayList<>();

    public List<GeographicSite> getGeographicSite() {
        return Binding.getValues(geographicSite);
    }

    public List<L3IpInterface> getIpInterfaces() {
        return Binding.getValues(ipInterfaces);
    }
}
