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

package com.ericsson.oss.air.cardq.utils;

import com.ericsson.oss.air.cardq.api.model.AugmentationFieldRequest;
import com.ericsson.oss.air.cardq.api.model.AugmentationRequest;
import com.ericsson.oss.air.cardq.api.model.QueryField;
import com.ericsson.oss.air.cardq.error.ErrorCodes;
import com.ericsson.oss.air.cardq.error.MandatoryParameterException;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ericsson.oss.air.cardq.error.ErrorCodes.*;
import static com.ericsson.oss.air.cardq.services.GraphServiceFactory.getGraphServiceType;
import static com.ericsson.oss.air.cardq.utils.CommonUtils.findDuplicates;
import static com.ericsson.oss.air.cardq.utils.Constants.*;

@UtilityClass
public class RequestValidation {

    private static final List<String> MANDATORY_CORE_INPUT_FIELDS_VALUES = List.of(NODE_FDN);
    private static final List<String> MANDATORY_RAN_INPUT_FIELDS_VALUES = List.of(LOCAL_DN, MEAS_OBJ_LDN);
    private static final List<String> CORE_AUGMENTATION_FIELD_VALUES = List.of(NSI, NSSI, SITE, PLMNID);
    private static final List<String> RAN_AUGMENTATION_FIELD_VALUES = List.of(TA, NSI, NSSI, CELLID, QOS, PLMNID, SNSSAI);
    private static final String AUGMENTATION_FIELDS = "augmentationFields";
    private static final String INPUT_FIELDS = "inputFields";
    private static final String SNSSAI_REGEX = "\\d+-\\d+";

    public static void validateRequestData(AugmentationRequest request) throws MandatoryParameterException, IllegalArgumentException {
        String receivedQueryType = request.getQueryType();
        if (receivedQueryType != null && GraphServiceType.getByString(receivedQueryType) == null) {
            String errorMessage = String.format(INVALID_QUERY_TYPE.code(), receivedQueryType);
            throw new IllegalArgumentException(errorMessage);
        }

        checkMissingQueryFields(request);
        checkDuplicateInputFields(request);
        checkAugmentationFieldValues(request);
        checkMandatoryInputFields(request);
        checkSnssai(request);
    }

    private static void checkMandatoryInputFields(AugmentationRequest request) {
        Map<String, String> inputFields = request.getInputFields()
                .stream()
                .collect(Collectors.toMap(QueryField::getName, QueryField::getValue));

        List<String> missingMandatoryValues = new ArrayList<>();
        GraphServiceType graphServiceType = getGraphServiceType(request.getQueryType());

        List<String> mandatoryInputFields = new ArrayList<>();
        if (graphServiceType == GraphServiceType.CORE) {
            mandatoryInputFields = MANDATORY_CORE_INPUT_FIELDS_VALUES;
        } else if (graphServiceType == GraphServiceType.RAN) {
            mandatoryInputFields = MANDATORY_RAN_INPUT_FIELDS_VALUES;
        }
        for (String value : mandatoryInputFields) {
            if (!inputFields.containsKey(value)) {
                missingMandatoryValues.add(value);
            }
        }

        if (!missingMandatoryValues.isEmpty()) {
            String errorMessage = String.format(MISSING_MANDATORY_FIELD.code(), missingMandatoryValues);
            throw new MandatoryParameterException(errorMessage);
        }
    }

    private static void checkSnssai(AugmentationRequest request) {
        Map<String, String> inputFields = request.getInputFields()
                .stream()
                .collect(Collectors.toMap(QueryField::getName, QueryField::getValue));
        if (inputFields.containsKey(SNSSAI) && !inputFields.get(SNSSAI).matches(SNSSAI_REGEX)) {
            String message = SNSSAI.concat(" format should be '<SNSSAI_SST>-<SNSSAI_SD>'. ie. 70-2.");
            throw new IllegalArgumentException(String.format(ErrorCodes.INVALID_INPUT_FIELD_VALUE.code(), message));
        }
    }

    public static void checkMissingQueryFields(final AugmentationRequest request) {
        List<String> missingQueryFieldList = new ArrayList<>();
        if (CollectionUtils.isEmpty(request.getAugmentationFields())) {
            missingQueryFieldList.add(AUGMENTATION_FIELDS);
        }
        if (CollectionUtils.isEmpty(request.getInputFields())) {
            missingQueryFieldList.add(INPUT_FIELDS);
        }
        if (!missingQueryFieldList.isEmpty()) {
            throw new IllegalArgumentException(String.format(MISSING_QUERY_FIELD.code(), missingQueryFieldList));
        }
    }

    public static void checkDuplicateInputFields(final AugmentationRequest request) {
        Set<String> duplicates = findDuplicates(request.getInputFields(), QueryField::getName);
        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException(String.format(DUPLICATE_INPUT_FIELD.code(), duplicates));
        }
    }

    public static void checkAugmentationFieldValues(final AugmentationRequest request) {
        final List<String> validAugmentationFields;
        GraphServiceType graphServiceType = getGraphServiceType(request.getQueryType());
        if (graphServiceType == GraphServiceType.RAN) {
            validAugmentationFields = RAN_AUGMENTATION_FIELD_VALUES;
        } else {
            validAugmentationFields = CORE_AUGMENTATION_FIELD_VALUES;
        }
        List<String> invalidAugmentationValues =
                request.getAugmentationFields().stream().distinct()
                        .map(AugmentationFieldRequest::getName)
                        .filter(field -> !validAugmentationFields.contains(field))
                        .toList();
        if (!invalidAugmentationValues.isEmpty()) {
            throw new IllegalArgumentException(String.format(INVALID_AUGMENTATION_FIELD_VALUE.code(),
                    validAugmentationFields,
                    invalidAugmentationValues));
        }
    }
}
