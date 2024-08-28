/*******************************************************************************
 * COPYRIGHT Ericsson 2022
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

package com.ericsson.oss.air.cardq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * CTS connection details.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cts")
public class CtsConnectionConfiguration {

    public static final String REST = "rest";
    public static final String OSS_CORE_WS = "oss-core-ws";
    private String url;
    private String user;
    private String password;
    private String databaseHost;
    private String databaseName;
}
