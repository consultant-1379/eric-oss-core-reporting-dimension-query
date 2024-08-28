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
package com.ericsson.oss.air.cardq.model.cts.ctw;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.ericsson.oss.air.cardq.model.cts.ctc.Resource;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * NRCell
 * ID: ctw:nrcell
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NRCell  extends Resource {
    private List<Binding<PlmnInfo>> plmnInfoList = new ArrayList<>();
    @JsonProperty("gnbdu")
    private List<Binding<GNBDU>> gnbduList = new ArrayList<>();
    @JsonProperty("networkSliceSubnetForNrCell")
    private List<Binding<NetworkSliceSubnet>> nssList = new ArrayList<>();
    @JsonProperty("networkSliceForNrCell")
    private List<Binding<NetworkSlice>> networkSliceList = new ArrayList<>();

    @Setter
    @Getter
    private String trackingAreaCode;

    @Setter
    @Getter
    private String localCellIdNci;

    public List<PlmnInfo> getPlmnInfoList() {
        return Binding.getValues(plmnInfoList);
    }

    public List<GNBDU> getGnbduList() {
        return Binding.getValues(gnbduList);
    }

    public List<NetworkSliceSubnet> getNssList() {
        return Binding.getValues(nssList);
    }

    public List<NetworkSlice> getNetworkSliceList() {
        return Binding.getValues(networkSliceList);
    }
}
