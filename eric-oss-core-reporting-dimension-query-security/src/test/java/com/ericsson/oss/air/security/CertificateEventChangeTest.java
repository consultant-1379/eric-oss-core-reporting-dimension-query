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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.ericsson.oss.air.security.utils.SecurityTestUtils.SERVER_ALIAS;
import static com.ericsson.oss.air.security.utils.SecurityTestUtils.createTlsContext;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.coyote.http11.Http11NioProtocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.neo4j.core.Neo4jClient;

import com.ericsson.adp.security.certm.certificatewatcher.CertificateWatcherProperties;
import com.ericsson.adp.security.certm.certificatewatcher.CertificateWatcherService;
import com.ericsson.adp.security.certm.certificatewatcher.TlsContext;
import com.ericsson.oss.air.security.config.SecurityProperties;
import com.ericsson.oss.air.security.utils.exceptions.InternalRuntimeException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(classes = {CertificateWatcherService.class, CustomSslStoreBundle.class, DefaultSslBundleRegistry.class})
@EnableConfigurationProperties({CertificateWatcherProperties.class, SecurityProperties.class, ServerProperties.class})
class CertificateEventChangeTest extends AbstractTlsTestSetup {
    private static final String SERVER_PATH = "src/test/resources/security/server";
    private static final String SERVER_UPDATED_PATH = "src/test/resources/security/server-updated";

    @SpyBean
    private SecurityProperties securityProperties;

    @SpyBean
    private CertificateWatcherProperties certificateWatcherProperties;

    @SpyBean
    private CustomSslStoreBundle sslStoreBundle;

    private RestTemplateReloader restTemplateReloader;

    private Logger log;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    public void setup() {
        restTemplateReloader = mock(RestTemplateReloader.class);

        this.log = (Logger) LoggerFactory.getLogger(CertificateEventChangeDetector.class);
        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        this.log.addAppender(this.listAppender);
    }

    @Test
    void verifyCertificateChangeEvents() throws FileNotFoundException {
        TlsContext tlsContext = createTlsContext(SERVER_PATH, certificateWatcherProperties, securityProperties);
        TlsContext tlsContextUpdate = createTlsContext(SERVER_UPDATED_PATH, certificateWatcherProperties, securityProperties);
        Flux<TlsContext> merged = Flux.merge(Flux.just(), Flux.just(tlsContext, tlsContextUpdate));

        // Set context for stores
        Http11NioProtocol protocol = mock(Http11NioProtocol.class);
        sslStoreBundle.setProtocol(protocol);
        RestTemplateReloader restTemplateReloader = mock(RestTemplateReloader.class);

        CertificateEventChangeDetector certificateEventChangeDetector =
                new CertificateEventChangeDetector(merged, sslStoreBundle, restTemplateReloader);
        certificateEventChangeDetector.init();
        Flux<TlsContext> tlsContextFluxFromDetector = certificateEventChangeDetector.getTlsContextFlux();

        StepVerifier.create(tlsContextFluxFromDetector.take(2))
                .expectSubscription()
                .assertNext(context -> {
                    assertThat(context.getName()).isEqualTo(SERVER_ALIAS);
                    assertThat(context.getKeyStore()).isPresent();
                    assertThat(context.getKeyStore().get().getPath()).endsWith(Paths.get("server/keystore.p12"));
                    assertThat(context.getTrustStore()).isPresent();
                    assertThat(context.getTrustStore().get().getPath()).endsWith(Paths.get("server/truststore.p12"));
                })
                .assertNext(context -> {
                    assertThat(context.getName()).isEqualTo(SERVER_ALIAS);
                    assertThat(context.getKeyStore()).isPresent();
                    assertThat(context.getKeyStore().get().getPath()).endsWith(Paths.get("server-updated/keystore.p12"));
                    assertThat(context.getTrustStore()).isPresent();
                    assertThat(context.getTrustStore().get().getPath()).endsWith(Paths.get("server-updated/truststore.p12"));
                })
                .verifyComplete();

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(protocol, times(2)).reloadSslHostConfig(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isEqualTo("_default_");
    }

    @Test
    void skipFluxSignalWhenFailureSetContextFails() {
        TlsContext tlsContext = TlsContext.builder().name("Test").build();
        Http11NioProtocol protocol = mock(Http11NioProtocol.class);
        CustomSslStoreBundle sslContext = mock(CustomSslStoreBundle.class);
        RestTemplateReloader restTemplateReloader = mock(RestTemplateReloader.class);
        when(sslContext.setContext(any(TlsContext.class))).thenThrow(InternalRuntimeException.class);
        Flux<TlsContext> tlsContextFlux = Flux.just(tlsContext);
        CertificateEventChangeDetector eventChangeDetector = new CertificateEventChangeDetector(tlsContextFlux, sslContext, restTemplateReloader);
        eventChangeDetector.init();
        Flux<TlsContext> tlsContextFluxFromDetector = eventChangeDetector.getTlsContextFlux();

        StepVerifier.create(tlsContextFluxFromDetector)
                .expectNextCount(1)
                .expectComplete()
                .verify();
        verify(protocol, times(0)).reloadSslHostConfig(anyString());
    }

    @Test
    void shouldContinueWhenReloadSslHostConfigFails() {
        CustomSslStoreBundle sslContext = mock(CustomSslStoreBundle.class);
        Http11NioProtocol protocol = spy(Http11NioProtocol.class);
        sslContext.setProtocol(protocol);
        RestTemplateReloader restTemplateReloader = mock(RestTemplateReloader.class);

        Flux<TlsContext> just = Flux.just(TlsContext.builder().name("server").build());
        when(sslContext.setContext(any(TlsContext.class))).thenReturn(true);

        CertificateEventChangeDetector certificateEventChangeDetector =
                new CertificateEventChangeDetector(just, sslContext, restTemplateReloader);
        certificateEventChangeDetector.init();
        Flux<TlsContext> tlsContextFluxFromDetector = certificateEventChangeDetector.getTlsContextFlux();
        doThrow(InternalRuntimeException.class).when(protocol).reloadSslHostConfig(anyString());

        StepVerifier.create(tlsContextFluxFromDetector)
                .expectNextCount(1)
                .verifyComplete();
        verify(protocol, times(0)).reloadSslHostConfig(anyString());
    }

    @Test
    void shouldNotReloadTomcatWhenClientSignals() {
        CustomSslStoreBundle sslContext = mock(CustomSslStoreBundle.class);
        Http11NioProtocol protocol = spy(Http11NioProtocol.class);
        sslContext.setProtocol(protocol);
        RestTemplateReloader restTemplateReloader = mock(RestTemplateReloader.class);

        Flux<TlsContext> just = Flux.just(TlsContext.builder().name("client").build());
        when(sslContext.setContext(any(TlsContext.class))).thenReturn(true);

        CertificateEventChangeDetector certificateEventChangeDetector =
                new CertificateEventChangeDetector(just, sslContext, restTemplateReloader);
        certificateEventChangeDetector.init();
        Flux<TlsContext> tlsContextFluxFromDetector = certificateEventChangeDetector.getTlsContextFlux();

        StepVerifier.create(tlsContextFluxFromDetector)
                .expectNextCount(1)
                .verifyComplete();
        verify(protocol, times(0)).reloadSslHostConfig(anyString());
    }

    @Test
    void reloadNeo4jClientTest() {
        CustomSslStoreBundle sslContext = Mockito.mock(CustomSslStoreBundle.class);
        Http11NioProtocol protocol = spy(Http11NioProtocol.class);
        sslContext.setProtocol(protocol);

        Flux<TlsContext> just = Mockito.mock(Flux.class);
        ApplicationContext applicationContext = Mockito.spy(ApplicationContext.class);
        AutowireCapableBeanFactory registry = new DefaultListableBeanFactory();
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(registry);
        when(applicationContext.getBean("neo4jClient")).thenReturn(Mockito.mock(Neo4jClient.class));
        CertificateEventChangeDetector certificateEventChangeDetector =
                new CertificateEventChangeDetector(just, sslContext, restTemplateReloader);
        certificateEventChangeDetector.setApplicationContext(applicationContext);

        certificateEventChangeDetector.reloadNeo4jClient();

        verify(applicationContext, times(1)).getBean("neo4jClient");
        verify(applicationContext, times(1)).publishEvent(any(Neo4jClient.class));

        assertFalse(this.listAppender.list.isEmpty());
        final List<String> infoMessages = this.listAppender.list.stream()
                .filter(log -> log.getLevel() == Level.WARN)
                .map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());

        assertTrue(infoMessages.contains("Reloading Neo4jClient"));
        assertTrue(infoMessages.contains("Destroyed existing Neo4jClient"));
        assertTrue(infoMessages.contains("Creating new Neo4jClient"));
        assertTrue(infoMessages.contains("Registered new Neo4jClient"));
    }

}