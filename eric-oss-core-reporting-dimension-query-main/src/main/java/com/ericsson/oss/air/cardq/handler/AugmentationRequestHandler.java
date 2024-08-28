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

import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;

/**
 * This interface provides the API for retrieving augmentation data from the topology provider configured in the application configuration. Each
 * provider will have its own implementation of this API.
 */
public interface AugmentationRequestHandler {

    /**
     * Returns the results of the fulfilled augmentation request.
     *
     * @param augmentationRequest augmentation request to fulfill.
     * @return the results of the fulfilled augmentation request.
     */
    Augmentation getAugmentation(AugmentationRequest augmentationRequest);

    /**
     * Returns true if the request is fulfilled using cached topology data.
     *
     * @param augmentationRequest augmentation request to fulfill.
     * @return true if the request is fulfilled using cached topology data.
     */
    default boolean getIsCached(AugmentationRequest augmentationRequest) {
        return false;
    }
}
