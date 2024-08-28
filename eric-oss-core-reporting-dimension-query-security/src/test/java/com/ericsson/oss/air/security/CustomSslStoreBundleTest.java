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
import com.ericsson.adp.security.certm.certificatewatcher.KeyStoreItem;
import com.ericsson.adp.security.certm.certificatewatcher.TlsContext;
import com.ericsson.adp.security.certm.certificatewatcher.TrustStoreItem;
import com.ericsson.oss.air.security.config.SecurityProperties;
import com.ericsson.oss.air.security.utils.exceptions.InternalRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.ericsson.oss.air.security.utils.SecurityTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CustomSslStoreBundle.class, DefaultSslBundleRegistry.class})
@EnableConfigurationProperties({CertificateWatcherProperties.class, SecurityProperties.class, ServerProperties.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomSslStoreBundleTest extends AbstractTlsTestSetup {
    private static final String SERVER_PATH = "src/test/resources/security/server";
    private static final String SERVER_PATH_INVALID = "src/test/resources/security/server-missing-ext";

    @SpyBean
    private SecurityProperties securityProperties;

    @SpyBean
    private CertificateWatcherProperties certificateWatcherProperties;

    @SpyBean
    private CustomSslStoreBundle customSslStoreBundle;

    @Test
    void shouldSetSSLContext() throws KeyStoreException, FileNotFoundException {
        TlsContext tlsContext = createTlsContext(SERVER_PATH, certificateWatcherProperties, securityProperties);
        boolean updated = customSslStoreBundle.setContext(tlsContext);
        assertThat(updated).isTrue();
        Enumeration<String> aliases = customSslStoreBundle.getTrustStore().aliases();
        List<X509Certificate> clientCertificates = new ArrayList<>();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate certificate = customSslStoreBundle.getTrustStore().getCertificate(alias);
            clientCertificates.add((X509Certificate) certificate);
        }
        assertCert(clientCertificates.toArray(X509Certificate[]::new), true, SN_SERVER, SN_CLIENT);
        X509Certificate[] certificateChain = (X509Certificate[]) customSslStoreBundle.getKeyStore().getCertificateChain(SERVER_ALIAS);
        assertCert(certificateChain, true, SN_SERVER);
    }

    @Test
    void shouldSkipSSLContext() throws FileNotFoundException {
        TlsContext tlsContext = createTlsContext(SERVER_PATH, certificateWatcherProperties, securityProperties);
        boolean updated = customSslStoreBundle.setContext(tlsContext);
        assertThat(updated).isTrue();
        updated = customSslStoreBundle.setContext(tlsContext);
        assertThat(updated).isFalse();
    }

    @Test
    void shouldFailToSetSSLContextTrustStore() {
        KeyStore keyStore = mock(KeyStore.class);
        TrustStoreItem trustStoreItem = mock(TrustStoreItem.class);
        when(trustStoreItem.getTrustStore()).thenReturn(keyStore);
        TlsContext tlsContext = TlsContext.builder().name("Test").trustStore(trustStoreItem).build();
        assertThatThrownBy(() -> customSslStoreBundle.setContext(tlsContext))
                .isInstanceOf(InternalRuntimeException.class)
                .hasMessageContaining("Failed to add certificates for alias:")
                .rootCause()
                .hasMessageContaining("Uninitialized keystore");
    }

    @Test
    void shouldFailToSetSSLContextKeyStore() {
        KeyStore keyStore = mock(KeyStore.class);
        KeyStoreItem keyStoreItem = mock(KeyStoreItem.class);
        when(keyStoreItem.getKeyStore()).thenReturn(keyStore);
        TlsContext tlsContext = TlsContext.builder().name("Test").keyStore(keyStoreItem).build();
        assertThatThrownBy(() -> customSslStoreBundle.setContext(tlsContext))
                .isInstanceOf(InternalRuntimeException.class)
                .hasMessageContaining("Failed to add/set keystore key pair for: Test")
                .rootCause()
                .hasMessageContaining("Uninitialized keystore");
    }

    @Test
    void shouldFailToSetSSLContextKeyStoreWithMissingServerAuthExtensionKeystore() throws FileNotFoundException {
        TlsContext tlsContext = createTlsContext(SERVER_PATH_INVALID, certificateWatcherProperties, securityProperties);
        String msg = "Invalid Server Certificate, Extended Key usage Server Auth missing for certificate :: " +
                "C=SE,ST=Stockholm,L=Stockholm,OU=IT SERVICES,O=Ericsson,CN=server";
        assertThatThrownBy(() -> customSslStoreBundle.setContext(tlsContext))
                .isInstanceOf(InternalRuntimeException.class)
                .hasMessageContaining(msg);
    }
}