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

package com.ericsson.oss.air.cardq.services;

import com.ericsson.oss.air.cardq.error.InternalRuntimeException;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

@Component
public class GraphServiceFactory {
    private static final EnumMap<GraphServiceType, GraphService> GRAPH_SERVICE_MAP = new EnumMap<>(GraphServiceType.class);

    @Autowired
    private List<GraphService> graphServices;

    @PostConstruct
    public void initServiceCache() {
        graphServices.forEach(service -> GRAPH_SERVICE_MAP.put(service.getType(), service));
    }

    public static GraphService getGraphService(GraphServiceType type) {
        Optional<GraphService> service = Optional.ofNullable(GRAPH_SERVICE_MAP.get(type));
        String errorMessage = String.format("Unknown query type: %s", type);
        return service.orElseThrow(() -> new InternalRuntimeException(errorMessage));
    }

    public static GraphServiceType getGraphServiceType(String queryType) {
        return GraphServiceType.getByString(queryType);
    }
}
