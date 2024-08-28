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

package com.ericsson.oss.air.cardq.controller.augmentation;

import static com.ericsson.oss.air.cardq.config.MeterRegistryConfiguration.CACHED;
import static com.ericsson.oss.air.cardq.config.MeterRegistryConfiguration.CARDQ_AUGMENTATION_RESPONSE;
import static com.ericsson.oss.air.cardq.config.MeterRegistryConfiguration.RESPONSE_TYPE;
import static com.ericsson.oss.air.cardq.config.MeterRegistryConfiguration.UNCACHED;
import static com.ericsson.oss.air.cardq.utils.RequestValidation.validateRequestData;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ericsson.oss.air.cardq.api.ArdqAugmentationApi;
import com.ericsson.oss.air.cardq.api.model.Augmentation;
import com.ericsson.oss.air.cardq.api.model.AugmentationQueryTypeResponse;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.handler.AugmentationRequestHandler;
import com.ericsson.oss.air.cardq.utils.GraphServiceType;

import io.micrometer.core.instrument.MeterRegistry;

@RestController
public class AugmentationApiController implements ArdqAugmentationApi {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private AugmentationRequestHandler augmentationRequestHandler;

    @Override
    public ResponseEntity<Augmentation> recordAugmentationRequest(AugmentationRequest augmentationRequest) {
        validateRequestData(augmentationRequest);

        Supplier<ResponseEntity<Augmentation>> responseEntitySupplier = () -> {
            Augmentation augmentation = augmentationRequestHandler.getAugmentation(augmentationRequest);
            return ResponseEntity.ok().body(augmentation);
        };

        return recordMetrics(augmentationRequest, responseEntitySupplier);
    }

    private ResponseEntity<Augmentation> recordMetrics(final AugmentationRequest augmentationRequest,
                                                       final Supplier<ResponseEntity<Augmentation>> responseEntitySupplier) {
        boolean isCached = this.augmentationRequestHandler.getIsCached(augmentationRequest);
        String cacheTag = isCached ? CACHED : UNCACHED;
        return meterRegistry.get(CARDQ_AUGMENTATION_RESPONSE)
                .tag(RESPONSE_TYPE, cacheTag)
                .timer()
                .record(responseEntitySupplier);
    }

    @Override
    public ResponseEntity<List<AugmentationQueryTypeResponse>> getDimensioningQueryTypes() {
        List<AugmentationQueryTypeResponse> response = Arrays.stream(GraphServiceType.values())
                .map(queryType -> new AugmentationQueryTypeResponse().queryType(queryType.getServiceType()))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(response);
    }
}
