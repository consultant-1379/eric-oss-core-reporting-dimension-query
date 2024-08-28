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

package com.ericsson.oss.air.cardq.security.utils;

import lombok.experimental.UtilityClass;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLContext;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.assertj.core.api.iterable.ThrowingExtractor;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@UtilityClass
public class SecurityTestUtils {
    private static SSLContext getSslContext(SSLHostConfig hostConfig) {
        return hostConfig.getCertificates().stream().findFirst().orElseThrow().getSslContext();
    }

    public static Enumeration<String> getAliasesFromConfigKeystore(SSLHostConfig hostConfig) {
        try {
            return hostConfig.getCertificates().stream().findFirst().orElseThrow().getCertificateKeystore().aliases();
        } catch (IOException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static SSLHostConfig getSslHostConfig(Http11NioProtocol protocol) {
        Stream<SSLHostConfig> sslHostConfigStream = Arrays.stream(protocol.findSslHostConfigs());
        return sslHostConfigStream
                .filter(hostConfig -> hostConfig.getHostName().equals("_default_"))
                .findFirst()
                .orElseThrow();
    }

    public static Function<SSLHostConfig, X509Certificate[]> getClientCertsFunction() {
        return hostConfig -> getSslContext(hostConfig).getAcceptedIssuers();
    }

    public static Function<SSLHostConfig, X509Certificate[]> getServerCertChainFunction(String alias) {
        return hostConfig -> getSslContext(hostConfig).getCertificateChain(alias);
    }

    public static void assertCert(X509Certificate[] x509Certificates, boolean isSubject, String... values) {
        ThrowingExtractor<X509Certificate, String, RuntimeException> extractor = cert -> isSubject ? getSubject(cert) : getIssuer(cert);

        assertThat(x509Certificates)
                .hasSize(values.length)
                .extracting(extractor)
                .containsExactlyInAnyOrder(values);
    }

    public static String getSubject(X509Certificate cert) {
        return cert.getSubjectX500Principal().getName();
    }

    public static String getIssuer(X509Certificate cert) {
        return cert.getIssuerX500Principal().getName();
    }
}
