/*******************************************************************************
 * COPYRIGHT Ericsson 2022
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

package com.ericsson.oss.air.cardq.client.cts;

import static com.ericsson.oss.air.cardq.config.CtsConnectionConfiguration.OSS_CORE_WS;
import static com.ericsson.oss.air.cardq.config.CtsConnectionConfiguration.REST;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ericsson.oss.air.cardq.config.CtsConnectionConfiguration;

/**
 * class used to send request to the CTS API
 */
@Component
public class CtsRestClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CtsRestClient.class);

    @Autowired
    private CtsConnectionConfiguration ctsConnectionConfiguration;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Get calls to CTS
     *
     * @param <T>          responseType
     * @param queryParams  queryParams
     * @param uriVariables uriVariables
     * @param path         path
     * @return body
     */
    @Retryable(
            retryFor = {ResourceAccessException.class},
            maxAttemptsExpression = "${cts.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${cts.retry.delay}"))
    public <T> T getResource(final Class<T> elementType,
                             final MultiValueMap<String, String> queryParams,
                             final Map<String, String> uriVariables,
                             final String... path) {
        final HttpHeaders headers = createHeaders();
        final HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        final URI eaiUri = UriComponentsBuilder.fromHttpUrl(ctsConnectionConfiguration.getUrl())
                .pathSegment(OSS_CORE_WS, REST)
                .pathSegment(path)
                .queryParams(queryParams)
                .buildAndExpand(uriVariables)
                .encode()
                .toUri();
        LOGGER.debug("EAI GET request: {}", eaiUri);
        return restTemplate.exchange(eaiUri, HttpMethod.GET, requestEntity, elementType).getBody();
    }

    /**
     * Get calls to CTS as list
     *
     * @param <T>         responseType
     * @param queryParams queryParams
     * @param path        path
     * @return body
     */
    @Retryable(
            retryFor = {ResourceAccessException.class},
            maxAttemptsExpression = "${cts.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${cts.retry.delay}"))
    public <T> List<T> getResourceList(final Class<T> elementType,
                                       final MultiValueMap<String, String> queryParams,
                                       final String... path) {
        final HttpHeaders headers = createHeaders();
        final HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        final URI eaiUri = UriComponentsBuilder.fromHttpUrl(ctsConnectionConfiguration.getUrl())
                .pathSegment(OSS_CORE_WS, REST)
                .pathSegment(path)
                .queryParams(queryParams)
                .encode()
                .buildAndExpand()
                .toUri();
        LOGGER.debug("EAI GET request: {}", eaiUri);
        final ParameterizedTypeReference<List<T>> typeReference = getTypeRefernce(elementType);
        return restTemplate.exchange(eaiUri, HttpMethod.GET, requestEntity, typeReference).getBody();
    }

    private <T> ParameterizedTypeReference<List<T>> getTypeRefernce(final Class<T> elementType) {
        return new ParameterizedTypeReference<>() {
            @Override
            public Type getType() {
                return TypeUtils.parameterize(List.class, elementType);
            }
        };
    }

    /**
     * Create headers required for accessing CTS.
     *
     * @return HttpHeaders
     */
    private HttpHeaders createHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(ctsConnectionConfiguration.getUser(), ctsConnectionConfiguration.getPassword());
        headers.add("GS-Database-Name", ctsConnectionConfiguration.getDatabaseName());
        headers.add("GS-Database-Host-Name", ctsConnectionConfiguration.getDatabaseHost());
        return headers;
    }
}
