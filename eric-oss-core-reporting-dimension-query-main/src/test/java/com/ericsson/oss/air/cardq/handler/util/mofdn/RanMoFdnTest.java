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

import static com.ericsson.oss.air.cardq.utils.Constants.TA;
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

public class RanMoFdnTest {

    @Test
    void get_request() {

        final AugmentationRequest request = new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("localDn").value(
                        "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1"))
                .addInputFieldsItem(new QueryField().name("measObjLdn").value("ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(TA))
                .queryType("ran");

        final RanMoFdn moFdn = new RanMoFdn(request);

        assertEquals("SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209", moFdn.get());

    }

    @Test
    void get_request_missingLocalLdn() {

        final AugmentationRequest request = new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("measObjLdn").value("ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(TA))
                .queryType("ran");

        final RanMoFdn moFdn = new RanMoFdn(request);

        assertThrows(MandatoryParameterException.class, () -> moFdn.get());
    }

    @Test
    void get_request_missingMeasObjLdn() {

        final AugmentationRequest request = new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("localDn").value(
                        "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(TA))
                .queryType("ran");

        final RanMoFdn moFdn = new RanMoFdn(request);

        assertThrows(MandatoryParameterException.class, () -> moFdn.get());
    }

    @Test
    void get_pamameters() {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("localDn", "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1");
        parameters.put("measObjLdn", "ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209");

        final RanMoFdn moFdn = new RanMoFdn(parameters);

        assertEquals("SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209", moFdn.get());
    }

    @Test
    void get_pamameters_missingLocalLdn() {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("measObjLdn", "ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209");

        final RanMoFdn moFdn = new RanMoFdn(parameters);

        assertThrows(MandatoryParameterException.class, () -> moFdn.get());
    }

    @Test
    void get_pamameters_missingMeasObjLdn() throws Exception {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("localDn", "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1");

        final RanMoFdn moFdn = new RanMoFdn(parameters);

        assertThrows(MandatoryParameterException.class, () -> moFdn.get());
    }

}
