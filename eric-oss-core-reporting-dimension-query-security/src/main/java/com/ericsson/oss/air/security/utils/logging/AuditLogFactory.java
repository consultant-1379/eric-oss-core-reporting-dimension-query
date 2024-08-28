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

package com.ericsson.oss.air.security.utils.logging;

public final class AuditLogFactory {
    private AuditLogFactory() {
    }

    /**
     * Return the AuditLogger instance
     *
     * @param clazz The class that is calling the logger
     * @return Return the AuditLogger for the class
     */
    public static AuditLogger getLogger(final Class<?> clazz) {
        return new AuditLogger(clazz);
    }
}