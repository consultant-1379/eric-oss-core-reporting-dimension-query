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

package com.ericsson.oss.air.cardq.client.observability;

import com.ericsson.oss.air.security.utils.exceptions.InternalRuntimeException;
import io.micrometer.common.KeyValues;
import org.springframework.http.client.observation.ClientRequestObservationContext;
import org.springframework.http.client.observation.DefaultClientRequestObservationConvention;

import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.http.client.observation.ClientHttpObservationDocumentation.HighCardinalityKeyNames.HTTP_URL;
import static org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames.URI;

/**
 * Custom Observability Implementation to add full URI path as a metric tag
 */
public class CustomClientRequestObservationConvention extends DefaultClientRequestObservationConvention {

    private final boolean addQuery;

    /**
     * addQuery boolean enables either adding URL with query part included or only URL path as a metric tag
     * Example: <br>
     * With Query: uri="/oss-core-ws/rest/ctw/networkslicesubnet?name=ManualDataSet-NetworkSliceSubnet-1B&fs.networkSlice=" <br>
     * Without Query: uri="/oss-core-ws/rest/ctw/networkslicesubnet"
     *
     * @param addQuery boolean to select URL path with or without query
     */
    public CustomClientRequestObservationConvention(boolean addQuery) {
        this.addQuery = addQuery;
    }

    @Override
    public KeyValues getLowCardinalityKeyValues(ClientRequestObservationContext context) {
        KeyValues lowCardinalityKeyValues = super.getLowCardinalityKeyValues(context);
        boolean uriNone = lowCardinalityKeyValues.stream()
                .anyMatch(keyValue -> keyValue.getKey().equals(URI.asString()) && keyValue.getValue().equals("none"));
        if (uriNone) {
            return getHighCardinalityKeyValues(context)
                    .stream()
                    .filter(keyValue -> keyValue.getKey().equals(HTTP_URL.asString()))
                    .findFirst()
                    .map(keyValue -> lowCardinalityKeyValues.and(URI.asString(), getPath(addQuery, keyValue.getValue())))
                    .orElse(lowCardinalityKeyValues);
        }
        return lowCardinalityKeyValues;
    }

    private String getPath(boolean addQuery, String url) {
        try {
            if (addQuery) {
                return new URL(url).getFile();
            }
            return new URL(url).getPath();
        } catch (MalformedURLException e) {
            throw new InternalRuntimeException(String.format("Invalid URL :: %s", url), e);
        }
    }
}