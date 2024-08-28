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
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * CTS SDK Managed Object is the base class of EAI persisted objects.
 * ID: sdk:managedObject
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagedObject {
    @Setter
    @Getter
    Long id;

    @Setter
    @Getter
    private String type;

    @Setter
    @Getter
    private Boolean placeHolder;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    private List<DynamicAttribute> dynamicAttributes = new ArrayList<>();

    @Setter
    @Getter
    private List<TransientAttribute> transientAttributes = new ArrayList<>();

    @Setter
    @Getter
    private List<Object> bindings = new ArrayList<>();

    /**
     * TransientAttribute.
     */
    public static class TransientAttribute {
        private String attributeName;
        private Object attributeValue;

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(final String attributeName) {
            this.attributeName = attributeName;
        }

        public Object getAttributeValue() {
            return attributeValue;
        }

        public void setAttributeValue(final Object attributeValue) {
            this.attributeValue = attributeValue;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}