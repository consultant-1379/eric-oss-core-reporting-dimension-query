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
package com.ericsson.oss.air.cardq.model.cts.sdk;

/**
 * Dynamic attribute types.
 */
public enum DynamicAttributeType {
    STRING("stringAttributeValue"),
    BOOLEAN("booleanAttributeValue"),
    LIST("listAttributeValue"),
    MAP("mapAttributeValue");

    private final String type;

    /**
     * Constructor.
     *
     * @param type type
     */
    DynamicAttributeType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}