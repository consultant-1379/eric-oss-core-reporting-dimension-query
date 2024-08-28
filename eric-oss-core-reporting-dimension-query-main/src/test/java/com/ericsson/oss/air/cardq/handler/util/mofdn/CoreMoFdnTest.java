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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.error.MandatoryParameterException;
import org.junit.jupiter.api.Test;

class CoreMoFdnTest {

    @Test
    void get_request() {
        final AugmentationRequest request = new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("MeContext=PCC00011,ManagedElement=PCC00011"))
                .addInputFieldsItem(new QueryField().name("snssai").value("83-5"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("plmnId"));

        final CoreMoFdn moFdn = new CoreMoFdn(request);

        assertEquals("MeContext=PCC00011,ManagedElement=PCC00011", moFdn.get());
    }

    @Test
    void get_request_missingNodeFdn() {
        AugmentationRequest request = new AugmentationRequest()
                .addAugmentationFieldsItem(new AugmentationFieldRequest())
                .addInputFieldsItem(new QueryField().name("localDn")
                                            .value("SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1"))
                .addInputFieldsItem(new QueryField().name("measObjLdn").value("ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=2"));

        final CoreMoFdn moFdn = new CoreMoFdn(request);

        assertThrows(MandatoryParameterException.class, () -> moFdn.get());
    }

    @Test
    void get_parameters() {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("nodeFDN", "MeContext=PCC00011,ManagedElement=PCC00011");
        parameters.put("snssai", "83-5");

        final CoreMoFdn moFdn = new CoreMoFdn(parameters);

        assertEquals("MeContext=PCC00011,ManagedElement=PCC00011", moFdn.get());
    }

    @Test
    void get_parameters_missingNodeFdn() {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("localDn", "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1");
        parameters.put("measObjLdn", "ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=2");

        final CoreMoFdn moFdn = new CoreMoFdn(parameters);

        assertThrows(MandatoryParameterException.class, () -> moFdn.get());
    }

}