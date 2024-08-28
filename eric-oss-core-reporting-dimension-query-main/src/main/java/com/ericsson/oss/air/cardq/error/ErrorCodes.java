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

package com.ericsson.oss.air.cardq.error;

/**
 * Error codes and messages for augmentationApiController.
 */
public enum ErrorCodes {
    MISSING_MANDATORY_FIELD("Missing Mandatory Parameter in inputsFields with format 'name': <value>. The missing parameter(s): %s"),
    MISSING_QUERY_FIELD("Missing or empty query field: %s"),
    DUPLICATE_INPUT_FIELD("Duplicate entry found in the inputFields: %s"),
    INVALID_AUGMENTATION_FIELD_VALUE("Invalid augmentationFields value, supported values are: %s, but found: %s"),
    INVALID_INPUT_FIELD_VALUE("Invalid inputFields value: %s"),

    INVALID_QUERY_TYPE("Invalid query type, %s");

    final String code;

    ErrorCodes(final String code) {
        this.code = code;
    }

    // Use instead of name() to use lowercase string
    public String code() {
        return code;
    }
}
