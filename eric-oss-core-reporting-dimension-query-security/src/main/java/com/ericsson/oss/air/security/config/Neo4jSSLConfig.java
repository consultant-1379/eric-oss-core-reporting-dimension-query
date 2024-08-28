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

package com.ericsson.oss.air.security.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;

import org.neo4j.driver.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.neo4j.ConfigBuilderCustomizer;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.ericsson.oss.air.security.utils.logging.AuditLogFactory;
import com.ericsson.oss.air.security.utils.logging.AuditLogger;

/**
 * Neo4j SSL configuration bean class
 */
@Configuration
@ConditionalOnProperty(value = "security.tls.enabled", havingValue = "true")
public class Neo4jSSLConfig {

    private static final AuditLogger LOGGER = AuditLogFactory.getLogger(Neo4jSSLConfig.class);
    @Value("${spring.neo4j.security.trustedCertAlias}")
    String trustedCertAlias;

    /**
     * Create a {@link ConfigBuilderCustomizer} bean with provided trust store alias
     *
     * @param sslStoreBundle ssl store bundle {@link SslStoreBundle}
     * @return ConfigBuilderCustomizer bean
     *
     * @throws IOException
     * @throws KeyStoreException
     * @throws CertificateEncodingException
     */
    @Bean("neo4jSSLCustomizer")
    @DependsOn("certificateSubscription")
    ConfigBuilderCustomizer neo4jSSLCustomizer(final SslStoreBundle sslStoreBundle)
            throws IOException, KeyStoreException, CertificateEncodingException {

        final KeyStore trustStore = sslStoreBundle.getTrustStore();
        LOGGER.warn("Fetching certificate with alias: " + trustedCertAlias);

        final byte[] rootCert = trustStore.getCertificate(trustedCertAlias).getEncoded();
        final Path trustedCertPath = Path.of("/tmp/root.crt");
        Files.write(trustedCertPath, rootCert);
        final File rootCertFile = trustedCertPath.toFile();

        return configBuilder ->
                configBuilder.withEncryption()
                        .withTrustStrategy(Config.TrustStrategy.trustCustomCertificateSignedBy(rootCertFile));

    }

}

