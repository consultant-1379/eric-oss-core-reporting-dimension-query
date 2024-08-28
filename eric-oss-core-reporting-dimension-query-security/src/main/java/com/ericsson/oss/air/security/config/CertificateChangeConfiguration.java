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
package com.ericsson.oss.air.security.config;

import com.ericsson.adp.security.certm.certificatewatcher.CertificateWatcherService;
import com.ericsson.adp.security.certm.certificatewatcher.TlsContext;
import com.ericsson.oss.air.security.CertificateEventChangeDetector;
import com.ericsson.oss.air.security.ChangeDetector;
import com.ericsson.oss.air.security.CustomSslStoreBundle;
import com.ericsson.oss.air.security.RestTemplateReloader;
import com.ericsson.oss.air.security.utils.exceptions.InternalRuntimeException;
import com.ericsson.oss.air.security.utils.logging.AuditLogFactory;
import com.ericsson.oss.air.security.utils.logging.AuditLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Objects;

import static com.ericsson.oss.air.security.config.CertificateId.SERVER;

@Configuration
@ConditionalOnProperty(value = "security.tls.enabled", havingValue = "true")
public class CertificateChangeConfiguration {

    private static final AuditLogger LOGGER = AuditLogFactory.getLogger(CertificateChangeConfiguration.class);

    @Bean
    public Flux<TlsContext> certificateSubscription(CertificateWatcherService certificateWatcherService, CustomSslStoreBundle sslStoreBundle) {
//      certificateId will be used as alias and should be the same as the stores relative directory
//      READ_PATH/certificateId/keystore.
//      eric-oss-core-reporting-dimension-query-main/src/main/resources/dev/security/cardq/truststore
        ArrayList<Flux<TlsContext>> fluxes = new ArrayList<>();
        for (CertificateId certificateId : CertificateId.values()) {
            // Turn to hot stream, publish and connect to upstream without closing or resetting the published stream
            Flux<TlsContext> tlsContextFlux = certificateWatcherService.observe(certificateId.getAlias()).publish().autoConnect();
            // Await until stores generated and emit the initial signal
            TlsContext tlsContext = Objects.requireNonNull(tlsContextFlux.blockFirst());
            checkServerCertificate(tlsContext);
            sslStoreBundle.setContext(tlsContext);
            fluxes.add(tlsContextFlux);
        }
        return Flux.merge(fluxes);
    }

    private static void checkServerCertificate(TlsContext tlsContext) {
        if (tlsContext.getName().equals(SERVER.getAlias()) && tlsContext.getKeyStore().isEmpty()) {
            LOGGER.info("Server certificate missing, restarting...");
            throw new InternalRuntimeException("Failed to set server certificate, check if server certificate generated properly by SIP-TLS. ");
        }
    }

    @Bean
    public ChangeDetector changeDetector(Flux<TlsContext> tlsContextFlux, CustomSslStoreBundle bundle, RestTemplateReloader restTemplateReloader) {
        return new CertificateEventChangeDetector(tlsContextFlux, bundle, restTemplateReloader);
    }
}