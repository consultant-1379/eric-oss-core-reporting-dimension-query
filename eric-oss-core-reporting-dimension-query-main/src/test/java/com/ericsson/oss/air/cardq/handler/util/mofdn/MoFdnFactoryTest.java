/*******************************************************************************
 * COPYRIGHT Ericsson 2024
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

package com.ericsson.oss.air.cardq.handler.util.mofdn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.oss.air.cardq.utils.GraphServiceType;
import org.junit.jupiter.api.Test;

import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;

class MoFdnFactoryTest {

    private final MoFdnFactory moFdnFactory = new MoFdnFactory();

    private final AugmentationRequest request = new AugmentationRequest();

    private final Map<String, String> parameters = new HashMap<>();

    @Test
    void getRanMoFdn_request() {
        request.setQueryType("ran");
        assertEquals(RanMoFdn.class, this.moFdnFactory.getMoFdn(request).getClass());
    }

    @Test
    void getCoreMoFdn_request() {
        request.setQueryType("core");
        assertEquals(CoreMoFdn.class, this.moFdnFactory.getMoFdn(request).getClass());
    }

    @Test
    void getDefaultMoFdn_request() {
        assertEquals(CoreMoFdn.class, this.moFdnFactory.getMoFdn(request).getClass());
    }

    @Test
    void getCoreMoFdn_serviceType_parameters() {
        assertEquals(CoreMoFdn.class, this.moFdnFactory.getMoFdn(GraphServiceType.CORE, parameters).getClass());
    }

    @Test
    void getRanMoFdn_serviceType_parameters() {
        assertEquals(RanMoFdn.class, this.moFdnFactory.getMoFdn(GraphServiceType.RAN, parameters).getClass());
    }

}