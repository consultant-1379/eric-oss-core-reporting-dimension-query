/*******************************************************************************
 * COPYRIGHT Ericsson 2024
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

package com.ericsson.oss.air.cardq.config;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ericsson.oss.air.cardq.services.CacheService;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Configuration
public class MeterRegistryConfiguration {

    private static final List<Integer> DURATION_TIMES = List.of(5, 10, 25, 50, 75, 100, 250, 500, 750, 1000, 2500, 5000, 7500, 10000);
    public static final String CARDQ_AUGMENTATION_RESPONSE = "cardq.augmentation.response";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String UNCACHED = "uncached";
    public static final String CACHED = "cached";
    public static final String CACHE_SIZE = "cardq.augmentation.cached.count";
    public static final String NEO4J_ERROR_COUNT = "cardq.augmentation.neo4j.error_count";

    @Bean
    MeterRegistryCustomizer<MeterRegistry> customMetrics(CacheService cacheService) {
        Duration[] durations = DURATION_TIMES.stream().map(Duration::ofMillis).toArray(Duration[]::new);
        return registry -> {
            Timer.builder(CARDQ_AUGMENTATION_RESPONSE)
                    .sla(durations)
                    .tags(RESPONSE_TYPE, UNCACHED)
                    .register(registry);

            Timer.builder(CARDQ_AUGMENTATION_RESPONSE)
                    .sla(durations)
                    .tags(RESPONSE_TYPE, CACHED)
                    .register(registry);

            Gauge.builder(CACHE_SIZE, cacheService, service -> cacheService.getSize())
                    .strongReference(true)
                    .register(registry);

            Counter.builder(NEO4J_ERROR_COUNT)
                    .register(registry);
        };
    }
}
