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

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;

class AugmentationRequestHandlerTest {

    @Test
    void getIsCached() {

        AugmentationRequestHandler actual = new AugmentationRequestHandler() {
            @Override
            public Augmentation getAugmentation(final AugmentationRequest augmentationRequest) {
                return null;
            }
        };

        assertFalse(actual.getIsCached(null));
    }
}