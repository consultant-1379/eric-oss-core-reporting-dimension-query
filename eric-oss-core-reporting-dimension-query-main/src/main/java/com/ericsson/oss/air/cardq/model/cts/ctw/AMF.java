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

import com.ericsson.oss.air.cardq.model.cts.Binding;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * The Access and Mobility Management Function (AMF) manages registration and mobility of UEs in the 5G System (GS).
 * ID: ctw:amf
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AMF extends NGCoreNetworkFunction {

    private List<Binding<PlmnInfo>> plmnInfoList = new ArrayList<>();

    public List<PlmnInfo> getPlmnInfoList() {
        return Binding.getValues(plmnInfoList);
    }
}
