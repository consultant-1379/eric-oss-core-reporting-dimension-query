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
package com.ericsson.oss.air.cardq.model.cts;

/**
 * The operational state indicates if the 5G Wireless object is installed and partially or fully operable (Enabled) or the resource is not
 * installed or not operable (Disabled).
 */
public enum OperationalState {
    ENABLED,
    DISABLED,
}