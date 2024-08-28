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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationField;
import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.repository.TopologyAugmentationDao;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ATH-specific implementation of the {@link AugmentationRequestHandler}.  This class will fulfill the augmentation request by querying the
 * topology model in ATH and retrieve the requested augmentation field values.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "ath.enabled", havingValue = "true")
@Primary
public class AthAugmentationRequestHandler implements AugmentationRequestHandler {

    private final TopologyAugmentationDao topologyAugmentationDao;

    @PostConstruct
    public void postInit() {
        log.info("The ATH mode is enabled");
    }

    @Override
    public Augmentation getAugmentation(final AugmentationRequest augmentationRequest) {

        final Set<String> requestAugmentationFieldsName = augmentationRequest.getAugmentationFields()
                .stream()
                .map(AugmentationFieldRequest::getName)
                .collect(Collectors.toSet());

        final GraphServiceType graphServiceType = GraphServiceType.getByString(augmentationRequest.getQueryType());

        final Map<String, String> queryParameters = augmentationRequest.getInputFields()
                .stream()
                .collect(Collectors.toMap(QueryField::getName, QueryField::getValue));

        final List<List<AugmentationField>> augmentationFieldList = this.topologyAugmentationDao.findByQueryService(graphServiceType,
                                                                                                                    queryParameters);

        final Augmentation augmentation = new Augmentation(new ArrayList<>());

        // Only return required augmentation fields and duplicated entries will be removed
        augmentationFieldList.stream()
                .map(augmentationFields -> augmentationFields.stream()
                        .filter(augmentationField -> requestAugmentationFieldsName.contains(augmentationField.getName()))
                        .sorted(Comparator.comparing(AugmentationField::getName))
                        .toList()
                )
                .distinct()
                .forEach(augmentation::addFieldsItem);

        return augmentation;
    }
}
