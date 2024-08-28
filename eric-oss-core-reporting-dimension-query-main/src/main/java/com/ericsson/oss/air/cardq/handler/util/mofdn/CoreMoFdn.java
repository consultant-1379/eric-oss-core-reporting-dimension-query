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

import static com.ericsson.oss.air.cardq.error.ErrorCodes.MISSING_MANDATORY_FIELD;
import static com.ericsson.oss.air.cardq.utils.Constants.NODE_FDN;

import java.util.Map;
import java.util.stream.Collectors;

import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.error.MandatoryParameterException;
import org.springframework.util.ObjectUtils;

/**
 * The {@code CoreMoFdn} supplies the managed element name for a Core managed element.
 */
public class CoreMoFdn extends MoFdn {

    /**
     * Creates a {@link MoFdn} for the Core managed element in the provided augmentation request.
     *
     * @param augmentationRequest augmentation request for the managed element.
     */
    public CoreMoFdn(final AugmentationRequest augmentationRequest) {
        super(augmentationRequest.getInputFields().stream().collect(Collectors.toMap(QueryField::getName, QueryField::getValue)));
    }

    /**
     * Creates a {@link MoFdn} for the Core managed element in the provided query field map.
     *
     * @param queryFieldMap map with key value pairs for query fields.
     */
    public CoreMoFdn(final Map<String, String> queryFieldMap) {
        super(queryFieldMap);
    }

    @Override
    public String get() {

        final String errorMessage = String.format(MISSING_MANDATORY_FIELD.code(), NODE_FDN);
        final String nodeFdn = this.getQueryFieldMap().get(NODE_FDN);

        if (ObjectUtils.isEmpty(nodeFdn)) {
            throw new MandatoryParameterException(errorMessage);
        }

        return nodeFdn;
    }
}
