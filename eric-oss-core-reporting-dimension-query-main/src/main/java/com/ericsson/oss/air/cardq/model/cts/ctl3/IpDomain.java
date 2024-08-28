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
import com.ericsson.oss.air.cardq.model.cts.ctc.TopologyDomain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctl3:ipDomain
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpDomain extends TopologyDomain {

    private List<Binding<TopologyContainer>> aggregatedTopoContainers = new ArrayList<>();

    private List<Binding<IpDomain>> higherLevelIpDomain = new ArrayList<>();

    private List<Binding<IpDomain>> lowerLevelIpDomains = new ArrayList<>();

    private List<Binding<L3IpLink>> aggregatedIpLinks = new ArrayList<>();

    private List<Binding<L3IpServiceLink>> aggregatedIpSvcLinks = new ArrayList<>();

    private List<Binding<L3LogicalTerminationPoint>> aggregatedL3Ltps = new ArrayList<>();

    public List<TopologyContainer> getAggregatedTopoContainers() {
        return Binding.getValues(aggregatedTopoContainers);
    }

    public List<L3IpLink> getAggregatedIpLinks() {
        return Binding.getValues(aggregatedIpLinks);
    }

    public List<L3IpServiceLink> getAggregatedIpSvcLinks() {
        return Binding.getValues(aggregatedIpSvcLinks);
    }

    public List<L3LogicalTerminationPoint> getAggregatedL3Ltps() {
        return Binding.getValues(aggregatedL3Ltps);
    }

    public List<IpDomain> getHigherLevelIpDomain() {
        return Binding.getValues(higherLevelIpDomain);
    }

    public List<IpDomain> getLowerLevelIpDomains() {
        return Binding.getValues(lowerLevelIpDomains);
    }
}
