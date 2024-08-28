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
import com.ericsson.oss.air.cardq.model.cts.ctc.ForwardingConstruct;
import com.ericsson.oss.air.cardq.model.cts.ctg.RelatedParty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * ID: ctv:virtNetworkServ
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VirtualNetworkService extends ForwardingConstruct {

    private List<Binding<RelatedParty>> relatedParty = new ArrayList<>();

    private List<Binding<VirtualNetworkFunction>> vnfs = new ArrayList<>();

    public List<RelatedParty> getRelatedParty() {
        return Binding.getValues(relatedParty);
    }

    public List<VirtualNetworkFunction> getVnfs() {
        return Binding.getValues(vnfs);
    }
}
