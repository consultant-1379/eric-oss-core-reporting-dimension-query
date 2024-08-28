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


import com.ericsson.adp.security.certm.certificatewatcher.CertificateWatcherProperties;
import com.ericsson.adp.security.certm.certificatewatcher.TlsContext;
import com.ericsson.oss.air.security.config.SecurityProperties;
import com.ericsson.oss.air.security.config.ServerConfiguration;
import org.apache.catalina.connector.Connector;
import org.apache.commons.io.FileUtils;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

import static com.ericsson.oss.air.security.utils.SecurityTestUtils.*;
import static org.apache.tomcat.util.net.SSLHostConfig.CertificateVerification.REQUIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(classes = {CustomSslStoreBundle.class, CertificateWatcherProperties.class, DefaultSslBundleRegistry.class})
@EnableConfigurationProperties(SecurityProperties.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TomcatServerTlsTest extends AbstractTlsTestSetup {

    @SpyBean
    private SecurityProperties securityProperties;

    @SpyBean
    private CertificateWatcherProperties certificateWatcherProperties;

    @Autowired
    private CustomSslStoreBundle customSslStoreBundle;

    @Autowired
    private DefaultSslBundleRegistry sslBundleRegistry;

    @AfterAll
    public static void cleanup() throws IOException {
        File certDir = ResourceUtils.getFile("target/security");
        FileUtils.deleteDirectory(certDir);
    }

    @Test
    void checkIfServerStartWithCorrectCertificates() throws Exception {
        File source = ResourceUtils.getFile("src/test/resources/security/server");
        File dest = ResourceUtils.getFile("target/security/server");
        FileUtils.copyDirectory(source, dest);
        SecurityProperties.Tls tls = securityProperties.getTls();
        tls.setPort(8444);
        TlsContext tlsContext = createTlsContext("target/security/server", certificateWatcherProperties, securityProperties);
        customSslStoreBundle.setContext(tlsContext);
        doReturn(tls).when(securityProperties).getTls();

        WebServer webServer = getWebserver(securityProperties, certificateWatcherProperties, sslBundleRegistry, new ServerProperties());
        webServer.start();
        Http11NioProtocol protocol = (Http11NioProtocol) ((TomcatWebServer) webServer).getTomcat().getConnector().getProtocolHandler();
        SSLHostConfig sslHostConfig = getSslHostConfig(protocol);
        assertThat(protocol.getSecure()).isTrue();
        assertThat(sslHostConfig.getCertificateVerification().name()).isEqualTo(REQUIRED.toString());
        assertThat(sslHostConfig)
                .matches(config -> config.getCertificateVerification().name().equals(REQUIRED.toString()))
                .extracting(getServerCertChainFunction(SERVER_ALIAS))
                .satisfies(x509Certificates -> assertCert(x509Certificates, true, SN_SERVER))
                .satisfies(x509Certificates -> assertCert(x509Certificates, false, SN_ROOT));
        assertThat(sslHostConfig)
                .extracting(getClientCertsFunction())
                .satisfies(x509Certificates -> assertCert(x509Certificates, true, SN_SERVER, SN_CLIENT))
                .satisfies(x509Certificates -> assertCert(x509Certificates, false, SN_ROOT, SN_ROOT));
        webServer.stop();
    }

    @Test
    void checkIfServerStartWithCorrectCertificatesWithNonSecurePort() throws Exception {
        File source = ResourceUtils.getFile("src/test/resources/security/server");
        File dest = ResourceUtils.getFile("target/security/server");
        FileUtils.copyDirectory(source, dest);
        SecurityProperties.Tls tls = securityProperties.getTls();
        tls.setPort(8442);
        TlsContext tlsContext = createTlsContext("target/security/server", certificateWatcherProperties, securityProperties);
        customSslStoreBundle.setContext(tlsContext);
        doReturn(tls).when(securityProperties).getTls();
        ServerProperties serverProperties = new ServerProperties();
        serverProperties.setPort(8080);

        WebServer webServer = getWebserver(securityProperties, certificateWatcherProperties, sslBundleRegistry, serverProperties);
        webServer.start();
        Http11NioProtocol protocol = (Http11NioProtocol) ((TomcatWebServer) webServer).getTomcat().getConnector().getProtocolHandler();
        SSLHostConfig sslHostConfig = getSslHostConfig(protocol);
        assertThat(protocol.getSecure()).isTrue();
        assertThat(sslHostConfig)
                .matches(config -> config.getCertificateVerification().name().equals(REQUIRED.toString()))
                .extracting(getServerCertChainFunction(SERVER_ALIAS))
                .satisfies(x509Certificates -> assertCert(x509Certificates, true, SN_SERVER))
                .satisfies(x509Certificates -> assertCert(x509Certificates, false, SN_ROOT));
        assertThat(sslHostConfig)
                .extracting(getClientCertsFunction())
                .satisfies(x509Certificates -> assertCert(x509Certificates, true, SN_SERVER, SN_CLIENT))
                .satisfies(x509Certificates -> assertCert(x509Certificates, false, SN_ROOT, SN_ROOT));
        assertThat(((TomcatWebServer) webServer).getTomcat().getService().findConnectors())
                .extracting(Connector::getPort)
                .containsExactlyInAnyOrder(8080, 8442);
        assertThat(((TomcatWebServer) webServer).getTomcat().getService().findConnectors())
                .extracting(Connector::getScheme)
                .containsExactlyInAnyOrder("http", "https");
        webServer.stop();
    }

    @Test
    void serverFailsWithMissingKeystore() {
        SecurityProperties.Tls tls = securityProperties.getTls();
        tls.setPort(8445);
        doReturn(tls).when(securityProperties).getTls();
        customSslStoreBundle.setContext(TlsContext.builder().build());
        assertThatThrownBy(() ->
                getWebserver(securityProperties, certificateWatcherProperties, sslBundleRegistry, new ServerProperties())
                        .start())
                .isInstanceOf(WebServerException.class)
                .hasMessageContaining("Unable to start embedded Tomcat server")
                .rootCause()
                .hasMessageContaining("No aliases for private keys found in key store");
    }

    @Test
    void serverFailsWithEmptyKeystore() throws IOException {
        File source = ResourceUtils.getFile("src/test/resources/security/server-empty/keystore.p12");
        File target = new File("target/security/server/keystore.p12");
        FileUtils.copyFile(source, target);
        SecurityProperties.Tls tls = securityProperties.getTls();
        tls.setPort(8446);
        doReturn(tls).when(securityProperties).getTls();
        assertThatThrownBy(() ->
                getWebserver(securityProperties, certificateWatcherProperties, sslBundleRegistry, new ServerProperties())
                        .start())
                .isInstanceOf(WebServerException.class)
                .hasMessageContaining("Unable to start embedded Tomcat server")
                .rootCause()
                .hasMessageContaining("No aliases for private keys found in key store");
    }

    private static WebServer getWebserver(SecurityProperties securityProperties,
                                          CertificateWatcherProperties certificateWatcherProperties,
                                          DefaultSslBundleRegistry sslBundleRegistry,
                                          ServerProperties serverProperties) {
        ServerConfiguration serverConfiguration = new ServerConfiguration();
        final TomcatServletWebServerFactory servletWebServerFactory =
                serverConfiguration.servletContainer(securityProperties, sslBundleRegistry, serverProperties, certificateWatcherProperties);
        return servletWebServerFactory.getWebServer();
    }
}