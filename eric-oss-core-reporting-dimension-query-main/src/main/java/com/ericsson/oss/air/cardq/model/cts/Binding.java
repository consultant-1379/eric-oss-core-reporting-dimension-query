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
package com.ericsson.oss.air.cardq.model.cts;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * CTS Association to capture linked CTS resource values
 *
 * @param <T> resource type
 */
public class Binding<T> {
    private Mode mode;
    private T value;

    /**
     * Mode values.
     */
    public enum Mode {
        LOADED,
        ADDED,
    }

    /**
     * CtsAssociation Constructor
     */
    public Binding() {
        // default constructor
    }

    /**
     * CtsAssociation Constructor
     *
     * @param mode  mode
     * @param value value
     */
    public Binding(final Mode mode, final T value) {
        this.mode = mode;
        this.value = value;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(final Mode mode) {
        this.mode = mode;
    }

    public T getValue() {
        return value;
    }

    public void setValue(final T value) {
        this.value = value;
    }

    public static <T> List<T> getValues(final List<Binding<T>> bindings) {
        return bindings.stream().map(Binding::getValue).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}


