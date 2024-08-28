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

package com.ericsson.oss.air.cardq.config;

import com.ericsson.oss.air.cardq.client.observability.CustomClientRequestObservationConvention;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "observability.client.enabled", havingValue = "true")
public class ClientRequestObservationConfig {

    @Bean
    public CustomClientRequestObservationConvention getCustomServerRequestObservation(@Value("${observability.client.add-query}") boolean addQuery) {
        return new CustomClientRequestObservationConvention(addQuery);
    }

}