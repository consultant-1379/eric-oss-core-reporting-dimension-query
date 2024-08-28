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

import java.util.HashMap;
import java.util.Map;

public enum GraphServiceType {
    CORE("core"),
    RAN("ran");

    private final String serviceType;

    public String getServiceType() {
        return serviceType;
    }

    static Map<String, GraphServiceType> lookup = new HashMap<>();

    static {
        for (GraphServiceType type : GraphServiceType.values()) {
            lookup.put(type.getServiceType(), type);
        }
    }

    GraphServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public static GraphServiceType getByString(String serviceType) {
        return lookup.get(serviceType);
    }
}
