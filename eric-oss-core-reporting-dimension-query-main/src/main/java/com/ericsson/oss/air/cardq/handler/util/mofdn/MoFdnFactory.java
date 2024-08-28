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

package com.ericsson.oss.air.cardq.handler.util.mofdn;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

import lombok.NoArgsConstructor;

/**
 * This factory returns a {@link MoFdn} instance representing the managed element in the provided augmentation request.
 */
@Component
@NoArgsConstructor
public class MoFdnFactory {

    /**
     * Returns a {@link MoFdn} instance representing the managed element in the provided augmentation request.
     *
     * @param augmentationRequest augmentation request for the target managed element.
     * @return {@code MoFdn} representing the managed element in the provided augmentation request.
     */
    public MoFdn getMoFdn(final AugmentationRequest augmentationRequest) {
        return switch (GraphServiceType.getByString(augmentationRequest.getQueryType())) {
            case RAN -> new RanMoFdn(augmentationRequest);
            case CORE -> new CoreMoFdn(augmentationRequest);
        };
    }

    /**
     * Returns a {@link MoFdn} instance representing the managed element in the provided service type with query field map.
     *
     * @param graphServiceType graph service type.
     * @param queryFieldMap map with key value pairs for query fields.
     * @return {@code MoFdn} representing the managed element in the provided augmentation request.
     */
    public MoFdn getMoFdn(final GraphServiceType graphServiceType, final Map<String, String> queryFieldMap) {
        return switch (graphServiceType) {
            case RAN -> new RanMoFdn(queryFieldMap);
            case CORE -> new CoreMoFdn(queryFieldMap);
        };
    }
}
