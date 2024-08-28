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

package com.ericsson.oss.air.cardq.handler;

import static com.ericsson.oss.air.cardq.services.GraphServiceFactory.getGraphService;
import static com.ericsson.oss.air.cardq.utils.Constants.PLMNID;
import static com.ericsson.oss.air.cardq.utils.Constants.QOS;
import static com.ericsson.oss.air.cardq.utils.Constants.SNSSAI;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationField;
import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.cache.Graph;
import com.ericsson.oss.air.cardq.handler.util.mofdn.MoFdn;
import com.ericsson.oss.air.cardq.handler.util.mofdn.MoFdnFactory;
import com.ericsson.oss.air.cardq.services.CacheService;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CTS-specific implementation of the {@link AugmentationRequestHandler}.  This class will fulfill the augmentation request by querying the
 * topology model in CTS and traversing the resulting subgraph to retrieve the requested augmentation field values.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "cts.enabled", havingValue = "true")
public class CtsAugmentationRequestHandler implements AugmentationRequestHandler {

    private final MoFdnFactory moFdnFactory;

    private final CacheService cacheService;

    @PostConstruct
    public void postInit() {
        log.info("The CTS mode is enabled");
    }

    @Override
    public Augmentation getAugmentation(final AugmentationRequest augmentationRequest) {

        final MoFdn moFdn = this.moFdnFactory.getMoFdn(augmentationRequest);

        log.info("Handling CTS augmentation request for {}", moFdn.get());

        final Graph graph = getGraph(GraphServiceType.getByString(augmentationRequest.getQueryType()), moFdn.get());

        final List<List<Map<String, String>>> fieldList = getAugmentationFields(graph, augmentationRequest);

        return createResponseBody(fieldList);
    }

    @Override
    public boolean getIsCached(final AugmentationRequest augmentationRequest) {

        final MoFdn moFdn = this.moFdnFactory.getMoFdn(augmentationRequest);

        return Objects.nonNull(this.cacheService.get(moFdn.get()));
    }

    Graph getGraph(final GraphServiceType graphServiceType, final String id) {

        Pair<Graph, LocalDateTime> cachedGraphPair = cacheService.get(id);
        if (Objects.isNull(cachedGraphPair)) {
            Graph graph = getGraphService(graphServiceType).createGraph(id);
            return cacheService.put(id, graph).getFirst();
        }
        return cachedGraphPair.getFirst();
    }

    public List<List<Map<String, String>>> getAugmentationFields(final Graph graph, final AugmentationRequest augmentationRequest) {

        List<String> augmentationFields = augmentationRequest.getAugmentationFields()
                .stream()
                .map(AugmentationFieldRequest::getName)
                .distinct()
                .toList();

        GraphServiceType graphServiceType = GraphServiceType.getByString(augmentationRequest.getQueryType());

        Map<String, String> attributes;
        if (graphServiceType == GraphServiceType.RAN) {
            attributes = createRanAttribute(augmentationRequest);
        } else {
            attributes = augmentationRequest.getInputFields().stream().filter(queryField -> SNSSAI.equals(queryField.getName()))
                    .collect(Collectors.toMap(queryField -> SNSSAI, QueryField::getValue));
            attributes.putIfAbsent(SNSSAI, "");
        }
        return getGraphService(graphServiceType).navigateGraph(attributes, graph, augmentationFields);
    }

    private Augmentation createResponseBody(final List<List<Map<String, String>>> fieldsList) {

        List<List<AugmentationField>> result = fieldsList.stream()
                .map(fields -> fields.stream()
                        .map(augFields -> new AugmentationField().name(augFields.get("name")).value(augFields.get("value")))
                        .toList())
                .toList();

        return new Augmentation().fields(result);
    }

    private static Map<String, String> createRanAttribute(final AugmentationRequest augmentationRequest) {
        Map<String, String> outPut = new HashMap<>();
        outPut.put(PLMNID, PLMNID);
        outPut.put(SNSSAI, SNSSAI);
        outPut.put(QOS, QOS);

        return augmentationRequest.getInputFields()
                .stream()
                .filter(queryField -> outPut.containsKey(queryField.getName()))
                .collect(Collectors.toMap(queryField -> outPut.get(queryField.getName()), QueryField::getValue));
    }
}
