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
package com.ericsson.oss.air.security;

import static com.ericsson.oss.air.security.utils.SecurityUtil.reloadSSLHostConfig;

import java.util.function.Consumer;

import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.core.Neo4jClient;

import com.ericsson.adp.security.certm.certificatewatcher.TlsContext;
import com.ericsson.oss.air.security.config.CertificateId;
import com.ericsson.oss.air.security.utils.logging.AuditLogFactory;
import com.ericsson.oss.air.security.utils.logging.AuditLogger;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.Setter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class CertificateEventChangeDetector implements ChangeDetector {

    private static final AuditLogger LOG = AuditLogFactory.getLogger(CertificateEventChangeDetector.class);
    private final CustomSslStoreBundle sslStoreBundle;
    @Getter
    private final Flux<TlsContext> tlsContextFlux;
    private Disposable disposable;
    private final RestTemplateReloader restTemplateReloader;

    @Setter
    private ApplicationContext applicationContext;

    public CertificateEventChangeDetector(Flux<TlsContext> tlsContextFlux,
                                          CustomSslStoreBundle sslStoreBundle,
                                          RestTemplateReloader restTemplateReloader) {
        this.tlsContextFlux = tlsContextFlux;
        this.sslStoreBundle = sslStoreBundle;
        this.restTemplateReloader = restTemplateReloader;
    }

    @PostConstruct
    public void init() {
        disposable = subscribe(tlsContextFlux, getTlsContextConsumer());
    }

    @Override
    public Disposable subscribe(Flux<TlsContext> tlsContextFlux, Consumer<TlsContext> tlsContextConsumer) {
        return tlsContextFlux
                .doOnEach(tlsContextSignal -> LOG.debug("Signal received before filter: {}", tlsContextSignal.get()))
                .onErrorContinue((throwable, context) ->
                        LOG.error("Failed to process TlsContext, skipping the signal {}, due to {}", context, throwable.getMessage()))
                .subscribe(tlsContextConsumer);
    }

    @Override
    @PreDestroy
    public void shutdown() {
        disposable.dispose();
    }


    private Consumer<TlsContext> getTlsContextConsumer() {
        return tlsData -> {
            boolean isUpdated = sslStoreBundle.setContext(tlsData);
            if (isUpdated) {
                LOG.debug("SSL context updated with new keystore and/or truststore for:: {}", tlsData.getName());
                reloadSSLHostConfig(sslStoreBundle.getProtocol());
                restTemplateReloader.reload();

                if (CertificateId.ROOT.getAlias()
                        .equalsIgnoreCase(tlsData.getName())) {
                    //Reload Neo4j driver
                    reloadNeo4jClient();
                }
            } else {
                LOG.debug("Skipping reload, context not changed:: {}", tlsData.getName());
            }
        };
    }

    /**
     * Reload neo4j client
     */
    void reloadNeo4jClient() {
        LOG.warn("Reloading Neo4jClient");
        final DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) applicationContext.getAutowireCapableBeanFactory();
        registry.destroySingleton("neo4jDriver");
        registry.destroySingleton("neo4jClient");
        registry.destroySingleton("neo4jSSLCustomizer");
        LOG.warn("Destroyed existing Neo4jClient");
        LOG.warn("Creating new Neo4jClient");
        applicationContext.getBean("neo4jSSLCustomizer");
        final Neo4jClient neo4jClient = (Neo4jClient) applicationContext.getBean("neo4jClient");
        LOG.warn("Registered new Neo4jClient");
        applicationContext.publishEvent(neo4jClient);
    }

}



