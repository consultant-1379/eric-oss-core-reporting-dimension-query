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

package com.ericsson.oss.air.cardq.client.cts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.ericsson.oss.air.cardq.AbstractTestSetup;
import com.ericsson.oss.air.cardq.model.cts.ctc.EntityCollection;
import com.ericsson.oss.air.cardq.model.cts.ctw.AMF;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class CtsRestClientTest extends AbstractTestSetup {

    @SpyBean
    private CtsRestClient ctsRestClient;

    @MockBean
    private RestTemplate restTemplate;

    @Captor
    ArgumentCaptor<HttpEntity<?>> entity;

    @Captor
    ArgumentCaptor<ParameterizedTypeReference<?>> typeGetList;

    @Captor
    ArgumentCaptor<Class<?>> typeGet;

    final ArgumentCaptor<URI> uri = ArgumentCaptor.forClass(URI.class);
    final ArgumentCaptor<HttpMethod> method = ArgumentCaptor.forClass(HttpMethod.class);

    @Test
    public void shouldPassCorrectParametersToGetRequests() {
        final Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("networkSliceSubnetId", "2");
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("fs", "derivedAttrs,inaccessibleObjs");
        queryParams.add("fs.aggregateEntities", "");

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity(HttpStatus.OK));
        ctsRestClient.getResource(AMF.class, queryParams, uriVariables, "ctw", "amf", "{networkSliceSubnetId}");

        verify(restTemplate).exchange(uri.capture(), method.capture(), entity.capture(), typeGet.capture());

        assertThat(uri.getValue().toString()).isEqualTo(
                "http://localhost:10101/oss-core-ws/rest/ctw/amf/2?fs=derivedAttrs,inaccessibleObjs&fs.aggregateEntities=");
        assertThat(method.getValue()).isEqualTo(HttpMethod.GET);
        assertThat(entity.getValue())
                .matches(httpEntity -> MediaType.APPLICATION_JSON.equals(httpEntity.getHeaders().getContentType()))
                .extracting(HttpEntity::getHeaders)
                .extracting(HttpHeaders::entrySet)
                .satisfies(entries -> assertThat(entries)
                        .contains(entry("Authorization", List.of("Basic c3lzYWRtOg==")))
                        .contains(entry("GS-Database-Name", List.of("eai_install")))
                        .contains(entry("GS-Database-Host-Name", List.of("localhost"))));
        assertThat(typeGet.getValue()).isEqualTo(AMF.class);
    }

    @Test
    public void shouldPassCorrectParametersToGetListRequests() {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("filter", "aggregatedResources[?(@.objectInstId==10)]");
        queryParams.add("fs", "derivedAttrs");

        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(HttpStatus.OK));
        ctsRestClient.getResourceList(EntityCollection.class, queryParams, "ctc", "entitycollection");

        verify(restTemplate).exchange(uri.capture(), method.capture(), entity.capture(), typeGetList.capture());

        assertThat(uri.getValue().toString()).isEqualTo(
                "http://localhost:10101/oss-core-ws/rest/ctc/entitycollection?filter=aggregatedResources%5B?(@"
                        + ".objectInstId%3D%3D10)%5D&fs=derivedAttrs");
        assertThat(method.getValue()).isEqualTo(HttpMethod.GET);
        assertThat(entity.getValue())
                .matches(httpEntity -> MediaType.APPLICATION_JSON.equals(httpEntity.getHeaders().getContentType()))
                .extracting(HttpEntity::getHeaders)
                .extracting(HttpHeaders::entrySet)
                .satisfies(entries -> assertThat(entries)
                        .contains(entry("Authorization", List.of("Basic c3lzYWRtOg==")))
                        .contains(entry("GS-Database-Name", List.of("eai_install")))
                        .contains(entry("GS-Database-Host-Name", List.of("localhost"))));
        assertThat(typeGetList.getValue().getType().getTypeName()).isEqualTo("java.util.List<com.ericsson.oss.air.cardq.model.cts.ctc"
                                                                                     + ".EntityCollection>");
    }

    @Test
    public void shouldRetryIfConnectionFails() {
        doThrow(ResourceAccessException.class)
                .when(restTemplate)
                .exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class));
        try {
            ctsRestClient.getResourceList(EntityCollection.class, new LinkedMultiValueMap<>(), "ctc", "entitycollection");
        } catch (Exception ignored) {
        }
        verify(ctsRestClient, times(5)).getResourceList(EntityCollection.class, new LinkedMultiValueMap<>(), "ctc", "entitycollection");
    }

    @Test
    public void shouldRetryTwiceIfConnectionFails() {
        doThrow(ResourceAccessException.class).doThrow(ResourceAccessException.class).doReturn(new ResponseEntity<>(HttpStatus.OK))
                .when(restTemplate)
                .exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class));
        try {
            ctsRestClient.getResourceList(EntityCollection.class, new LinkedMultiValueMap<>(), "ctc", "entitycollection");
        } catch (Exception ignored) {
        }
        verify(ctsRestClient, times(3)).getResourceList(EntityCollection.class, new LinkedMultiValueMap<>(), "ctc", "entitycollection");
    }
}