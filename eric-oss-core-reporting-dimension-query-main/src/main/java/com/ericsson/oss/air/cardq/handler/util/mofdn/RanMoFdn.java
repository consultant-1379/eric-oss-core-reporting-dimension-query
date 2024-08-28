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
import static com.ericsson.oss.air.cardq.utils.Constants.LOCAL_DN;
import static com.ericsson.oss.air.cardq.utils.Constants.MEAS_OBJ_LDN;

import java.util.Map;
import java.util.stream.Collectors;

import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.error.MandatoryParameterException;
import org.springframework.util.ObjectUtils;

/**
 * The {@code RanMoFdn} supplies the managed element name for a RAN managed element.
 */
public class RanMoFdn extends MoFdn {
    private static final int IDX_SHORTENED_MEASOBJLDN = 1;
    private static final int MEASOBJLDN_SPLIT_CNT = 2;

    /**
     * Creates a {@link MoFdn} for the RAN managed element in the provided augmentation request.
     *
     * @param augmentationRequest augmentation request for the managed element.
     */
    public RanMoFdn(final AugmentationRequest augmentationRequest) {
        super(augmentationRequest.getInputFields().stream().collect(Collectors.toMap(QueryField::getName, QueryField::getValue)));
    }

    /**
     * Creates a {@link MoFdn} for the RAN managed element in the provided query field map.
     *
     * @param queryFieldMap map with key value pairs for query fields.
     */
    public RanMoFdn(final Map<String, String> queryFieldMap) {
        super(queryFieldMap);
    }

    @Override
    public String get() {

        final String errorMessageLocalDN = String.format(MISSING_MANDATORY_FIELD.code(), LOCAL_DN);
        final String errorMessageMeasObjLdn = String.format(MISSING_MANDATORY_FIELD.code(), MEAS_OBJ_LDN);
        final String localDn = this.getQueryFieldMap().get(LOCAL_DN);

        if (ObjectUtils.isEmpty(localDn)) {
            throw new MandatoryParameterException(errorMessageLocalDN);
        }

        final String measObjLdn = this.getQueryFieldMap().get(MEAS_OBJ_LDN);

        if (ObjectUtils.isEmpty(measObjLdn)) {
            throw new MandatoryParameterException(errorMessageMeasObjLdn);
        }

        final String[] splittedMeasObjLdn = measObjLdn.split(",", MEASOBJLDN_SPLIT_CNT);

        return localDn + "," + splittedMeasObjLdn[IDX_SHORTENED_MEASOBJLDN];
    }
}
