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

package com.ericsson.oss.air.cardq.error;

public class MandatoryParameterException extends RuntimeException {
    private final String message;

    public MandatoryParameterException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
