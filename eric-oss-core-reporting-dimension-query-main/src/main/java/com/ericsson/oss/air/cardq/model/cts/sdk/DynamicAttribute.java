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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Dynamic attribute
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicAttribute {
    private Integer attributeKey;
    private String groupName;
    private String attributeName;
    private Integer attributeDefinitionId;
    private DynamicAttributeType dynamicAttributeType;
    private String stringAttributeValue;
    private List<Object> listAttributeValue = new ArrayList<>();
    private Boolean booleanAttributeValue;
    private Integer longAttributeValue;
    private Map<String, Object> mapAttributeValue = Collections.emptyMap();
    private Object attributeValue;

    /**
     * Set Attribute value defined as STRING
     *
     * @param stringAttributeValue stringAttributeValue as String
     */
    public void setStringAttributeValue(final String stringAttributeValue) {
        this.stringAttributeValue = stringAttributeValue;
        this.attributeValue = stringAttributeValue;
        this.dynamicAttributeType = DynamicAttributeType.STRING;
    }

    /**
     * Set Attribute value defined as LIST
     *
     * @param listAttributeValue listAttributeValue as List
     */
    public void setListAttributeValue(final List<Object> listAttributeValue) {
        this.listAttributeValue = new ArrayList<>(listAttributeValue);
        this.attributeValue = new ArrayList<>(listAttributeValue);
        this.dynamicAttributeType = DynamicAttributeType.LIST;
    }

    /**
     * Set Attribute value defined as BOOLEAN
     *
     * @param booleanAttributeValue booleanAttributeValue as Boolean
     */
    public void setBooleanAttributeValue(final Boolean booleanAttributeValue) {
        this.booleanAttributeValue = booleanAttributeValue;
        this.attributeValue = booleanAttributeValue;
        this.dynamicAttributeType = DynamicAttributeType.BOOLEAN;
    }

    /**
     * Set Attribute value defined as MAP
     *
     * @param mapAttributeValue mapAttributeValue as Map
     */
    public void setMapAttributeValue(final Map<String, Object> mapAttributeValue) {
        this.mapAttributeValue = mapAttributeValue;
        this.attributeValue = mapAttributeValue;
        this.dynamicAttributeType = DynamicAttributeType.MAP;
    }
}