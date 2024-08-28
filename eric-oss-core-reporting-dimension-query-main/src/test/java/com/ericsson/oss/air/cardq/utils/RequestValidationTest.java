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

package com.ericsson.oss.air.cardq.utils;

import static org.junit.jupiter.api.Assertions.*;

import static com.ericsson.oss.air.cardq.utils.Constants.TA;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.error.MandatoryParameterException;

class RequestValidationTest {

    /**
     * Tests that the validation process does not throw an exception when the provided AugmentationRequest parameters are valid.
     */
    @Test
    public void shouldNotThrowException_When_AugmentationRequestParameters_Are_Valid() {
        AugmentationRequest validAugmentationRequest = new AugmentationRequest()
                .inputFields(new ArrayList<>())
                .augmentationFields(new ArrayList<>())
                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf2"))
                .addInputFieldsItem(new QueryField().name("snssai").value("11-1"))
                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"));
        assertDoesNotThrow(() -> RequestValidation.validateRequestData(validAugmentationRequest));
    }

    /**
     * Parameterized test for validating different scenarios of AugmentationRequest incorrect parameters.
     *
     * @param augmentationRequest  The AugmentationRequest instance to be validated.
     * @param expectedException    The expected exception type
     * @param expectedErrorMessage The expected error message if an exception is expected
     */
    @ParameterizedTest
    @MethodSource("requestData")
    public void shouldThrowExceptionWithDescription_When_AugmentationRequestParametersHaveProblem(final AugmentationRequest augmentationRequest,
                                                                                                  final Class<? extends Throwable> expectedException,
                                                                                                  final String expectedErrorMessage) {
        Throwable exception = assertThrows(expectedException, () -> RequestValidation.validateRequestData(augmentationRequest));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    /**
     * Provides test data for the parameterized test.
     *
     * @return Stream of Arguments representing different scenarios of AugmentationRequest.
     */
    private static Stream<Arguments> requestData() {
        return Stream.of(
                // Invalid query type
                Arguments.of(
                        new AugmentationRequest()
                                .inputFields(new ArrayList<>())
                                .augmentationFields(new ArrayList<>())
                                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf2"))
                                .addInputFieldsItem(new QueryField().name("snssai").value("11-1"))
                                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                                .queryType("INVALID_TYPE"),
                        IllegalArgumentException.class,
                        "Invalid query type, INVALID_TYPE"
                ),
                // Missing mandatory fields
                Arguments.of(
                        new AugmentationRequest()
                                .inputFields(new ArrayList<>())
                                .augmentationFields(new ArrayList<>())
                                .addInputFieldsItem(new QueryField().name("snssai").value("11-1"))
                                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi")),
                        MandatoryParameterException.class,
                        "Missing Mandatory Parameter in inputsFields with format 'name': <value>. The missing parameter(s): [nodeFDN]"
                ),
                // Missing input fields
                Arguments.of(
                        new AugmentationRequest()
                                .inputFields(new ArrayList<>())
                                .augmentationFields(new ArrayList<>())
                                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi")),
                        IllegalArgumentException.class,
                        "Missing or empty query field: [inputFields]"
                ),
                // Missing augmentation fields
                Arguments.of(
                        new AugmentationRequest()
                                .inputFields(new ArrayList<>())
                                .augmentationFields(new ArrayList<>())
                                .addInputFieldsItem(new QueryField().name("snssai").value("11-1")),
                        IllegalArgumentException.class,
                        "Missing or empty query field: [augmentationFields]"
                ),
                // Duplicate input fields
                Arguments.of(
                        new AugmentationRequest()
                                .inputFields(new ArrayList<>())
                                .augmentationFields(new ArrayList<>())
                                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf1"))
                                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf2"))
                                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                                .queryType("core"),
                        IllegalArgumentException.class,
                        "Duplicate entry found in the inputFields: [nodeFDN]"
                ),
                // Invalid augmentation field values
                Arguments.of(
                        new AugmentationRequest()
                                .inputFields(new ArrayList<>())
                                .augmentationFields(new ArrayList<>())
                                .addInputFieldsItem(new QueryField().name("nodeFDN").value("pcc-amf2"))
                                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("nsi"))
                                .addAugmentationFieldsItem(new AugmentationFieldRequest().name("unknown")),
                        IllegalArgumentException.class,
                        "Invalid augmentationFields value, supported values are: [nsi, nssi, site, plmnId], but found: [unknown]"
                ),
                // Missing mandatory fields for RAN
                Arguments.of(
                        new AugmentationRequest()
                                .inputFields(new ArrayList<>())
                                .augmentationFields(new ArrayList<>())
                                .addInputFieldsItem(new QueryField().name("localDn").value(
                                        "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1"))
                                .addAugmentationFieldsItem(new AugmentationFieldRequest().name(TA))
                                .queryType("ran"),
                        MandatoryParameterException.class,
                        "Missing Mandatory Parameter in inputsFields with format 'name': <value>. The missing parameter(s): [measObjLdn]"
                )
        );
    }
}