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
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Supplies the FDN of the managed element in a given augmentation request.  There are graph type-specific implementations for Core and RAN
 * augmentation requests.
 */
@RequiredArgsConstructor
public abstract class MoFdn implements Supplier<String> {

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, String> queryFieldMap;
}
