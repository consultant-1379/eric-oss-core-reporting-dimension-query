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

package com.ericsson.oss.air.cardq.services;

import com.ericsson.oss.air.cardq.config.CacheCollectionConfiguration;
import com.ericsson.oss.air.cardq.cache.Graph;
import com.fasterxml.jackson.databind.util.LRUMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


@Service
public class CacheService {

    @Autowired
    private CacheCollectionConfiguration cacheCollectionConfiguration;
    private LRUMap<String, Pair<Graph, LocalDateTime>> cache;

    @PostConstruct
    public void initialiseCache() {
        cache = new LRUMap<>(cacheCollectionConfiguration.getMaxEntries() / 5, cacheCollectionConfiguration.getMaxEntries());
    }

    public Pair<Graph, LocalDateTime> get(String nodeFDN) {
        Pair<Graph, LocalDateTime> pair = cache.get(nodeFDN);
        if (Objects.isNull(pair) || !isValidTime(pair.getSecond())) {
            return null;
        }
        return pair;
    }

    public Pair<Graph, LocalDateTime> put(String nodeFDN, Graph graph) {
        Pair<Graph, LocalDateTime> pair = get(nodeFDN);
        if (Objects.isNull(pair)) {
            cache.put(nodeFDN, Pair.of(graph, LocalDateTime.now()));
            return cache.get(nodeFDN);
        }
        return pair;
    }

    public boolean isValidTime(LocalDateTime time) {
        return ChronoUnit.SECONDS.between(time, LocalDateTime.now()) <= cacheCollectionConfiguration.getExpiryTimeSeconds();
    }

    public int getSize() {
        return cache.size();
    }
}
