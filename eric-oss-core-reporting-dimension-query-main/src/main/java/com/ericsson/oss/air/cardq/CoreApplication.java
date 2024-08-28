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

package com.ericsson.oss.air.cardq;

import com.ericsson.oss.air.security.utils.logging.AuditLogFactory;
import com.ericsson.oss.air.security.utils.logging.AuditLogger;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Core Application, the starting point of the application.
 */
@SpringBootApplication
@ComponentScan("com.ericsson")
@EnableRetry
public class CoreApplication {

    private static final AuditLogger AUDIT_LOGGER = AuditLogFactory.getLogger(CoreApplication.class);

    /**
     * Main entry point of the application.
     *
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    /**
     * Logs the startup event of the application
     */
    @PostConstruct
    public void logStartup() {
        AUDIT_LOGGER.info("Application is starting...");
    }

    /**
     * Logs the shutdown event of the application
     */
    @PreDestroy
    public void logShutdown() {
        AUDIT_LOGGER.info("Application is shutting down");
    }

    /**
     * Configuration bean for Web MVC.
     *
     * @return WebMvcConfigurer
     */
    @Bean
    public WebMvcConfigurer webConfigurer() {
        return new WebMvcConfigurer() {
        };
    }

    /**
     * Making a RestTemplate, using the RestTemplateBuilder, to use for consumption of RESTful interfaces.
     *
     * @param restTemplateBuilder RestTemplateBuilder instance
     *
     * @return RestTemplate
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }
}
