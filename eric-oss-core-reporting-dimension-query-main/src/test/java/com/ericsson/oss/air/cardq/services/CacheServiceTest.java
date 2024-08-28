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

package com.ericsson.oss.air.cardq.services;

import static com.ericsson.oss.air.cardq.cache.GraphTest.getGraphDummyObject;
import static com.ericsson.oss.air.cardq.config.MeterRegistryConfiguration.CACHE_SIZE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ericsson.oss.air.cardq.AbstractTestSetup;
import com.ericsson.oss.air.cardq.CoreApplication;
import com.ericsson.oss.air.cardq.cache.Graph;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { CoreApplication.class })
public class CacheServiceTest extends AbstractTestSetup {

    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private CacheService cacheService;

    @Test
    public void testIfNodeObjectDoesNotExistInCacheReturnNull() {
        Pair<Graph, LocalDateTime> response = cacheService.get("Test-VirtualFunction-3");
        assertThat(response).isNull();
    }

    @Test
    public void testIfNodeObjectDoesExistInCacheReturnObject() {
        cacheService.put("Test-VirtualFunction-4", new Graph());
        Pair<Graph, LocalDateTime> response = cacheService.get("Test-VirtualFunction-4");
        assertThat(response).isNotNull();
    }

    @Test
    public void testIfTimeIsValidForObject() {
        Boolean isTimeValid = cacheService.isValidTime(LocalDateTime.now());
        assertThat(isTimeValid).isTrue();
    }

    @Test
    public void testIfTimeIsNotValidForObject() {
        Boolean isTimeValid = cacheService.isValidTime(LocalDateTime.now().minusMinutes(16));
        assertThat(isTimeValid).isFalse();
    }

    @Test
    public void testIfRegistryCacheEmpty() {
        assertThat(meterRegistry.get(CACHE_SIZE).gauge().value()).isEqualTo(0);
    }

    @Test
    public void testIfRegistryCacheHasValue() {
        cacheService.put("Test-VirtualFunction-4", getGraphDummyObject());
        cacheService.put("Test-VirtualFunction-2", getGraphDummyObject());
        assertThat(meterRegistry.get(CACHE_SIZE).gauge().value()).isEqualTo(2);
    }

    @Test
    public void testIfRegistryCacheSizeIsCleared() {
        cacheService.put("Test-VirtualFunction-3", getGraphDummyObject());
        cacheService.put("Test-VirtualFunction-1", getGraphDummyObject());
        cacheService.initialiseCache();
        assertThat(meterRegistry.get(CACHE_SIZE).gauge().value()).isEqualTo(0);
    }
}
