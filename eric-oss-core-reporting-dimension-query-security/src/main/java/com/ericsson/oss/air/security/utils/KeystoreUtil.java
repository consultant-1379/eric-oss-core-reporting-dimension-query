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
package com.ericsson.oss.air.security.utils;

import com.ericsson.oss.air.security.config.CertificateId;
import com.ericsson.oss.air.security.utils.exceptions.InternalRuntimeException;
import com.ericsson.oss.air.security.utils.logging.AuditLogFactory;
import com.ericsson.oss.air.security.utils.logging.AuditLogger;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility Class for Keystore operations
 */
@UtilityClass
public class KeystoreUtil {
    private static final AuditLogger LOGGER = AuditLogFactory.getLogger(KeystoreUtil.class);
    // id-kp-serverAuth OBJECT IDENTIFIER (OID).
    // See IETF RFC 5280: https://www.rfc-editor.org/rfc/rfc5280#section-4.2.1.12
    private static final String ID_KP_SERVER_AUTH_OID = "1.3.6.1.5.5.7.3.1";

    public static KeyStore initializeKeystore(String keyStorePass) {
        final KeyStore keyStore;
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, keyStorePass.toCharArray());
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new InternalRuntimeException("Failed to initialize keystore.", e);
        }
        return keyStore;
    }

    public static boolean addTruststoreCerts(KeyStore trustStoreCurrent, KeyStore trustStore, String... certificateId) {
        AtomicBoolean trustStoreUpdated = new AtomicBoolean(false);
        String generatedAlias = "";
        try {
            Map<String, Certificate> certificates = getCertEntries(trustStore);
            for (Map.Entry<String, Certificate> entry : certificates.entrySet()) {
                String alias = entry.getKey();
                generatedAlias = createAlias(alias, certificateId);
                Certificate certificate = entry.getValue();
                setCertEntry(trustStoreCurrent, generatedAlias, certificate, trustStoreUpdated);
            }
        } catch (KeyStoreException e) {
            throw new InternalRuntimeException(String.format("Failed to add certificates for alias: %s", generatedAlias), e);
        }
        return trustStoreUpdated.get();
    }

    private static Map<String, Certificate> getCertEntries(KeyStore trustStore) throws KeyStoreException {
        Enumeration<String> aliases = trustStore.aliases();
        Map<String, Certificate> certificates = new HashMap<>();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate certificate = trustStore.getCertificate(alias);
            certificates.put(alias, certificate);
        }
        return certificates;
    }

    private static void setCertEntry(KeyStore trustStoreCurrent, String alias, Certificate certificate, AtomicBoolean trustStoreUpdated) {
        try {
            if (certificateNoneMatch(certificate, getCertEntries(trustStoreCurrent).values())) {
                trustStoreCurrent.setCertificateEntry(alias, certificate);
                trustStoreUpdated.set(true);
            }
            logCertAddedMessage(trustStoreUpdated.get(), (X509Certificate) certificate, alias);
        } catch (KeyStoreException e) {
            throw new InternalRuntimeException(String.format("Failed to add certificate for alias: %s", alias), e);
        }
    }

    private static boolean certificateNoneMatch(Certificate certificate, Collection<Certificate> certificatesCurrent) {
        return certificatesCurrent.stream().noneMatch(certificateCurrent -> {
            try {
                return Arrays.equals(certificateCurrent.getEncoded(), certificate.getEncoded());
            } catch (CertificateEncodingException e) {
                throw new InternalRuntimeException(e.getMessage(), e);
            }
        });
    }

    private static String createAlias(String alias, String... args) {
        ArrayList<String> aliasArgs = new ArrayList<>(List.of(args));
        aliasArgs.add(alias);
        return StringUtils.collectionToDelimitedString(aliasArgs, "-");
    }


    public static boolean addKeystoreKeyPair(KeyStore keyStoreCurrent, KeyStore keyStoreTlsContext,
                                             String alias, String password, String keyPassword) {
        boolean shouldSet;
        try {
            Certificate[] certificateChainCurrent = keyStoreCurrent.getCertificateChain(alias);
            Certificate[] certificateChain = keyStoreTlsContext.getCertificateChain(alias);
            KeyStore.ProtectionParameter protectionParameterContext = new KeyStore.PasswordProtection(password.toCharArray());
            KeyStore.PrivateKeyEntry keyStoreEntry = ((KeyStore.PrivateKeyEntry) keyStoreTlsContext.getEntry(alias, protectionParameterContext));
            boolean match = Arrays.equals(certificateChainCurrent, certificateChain);
            X509Certificate certificate = (X509Certificate) keyStoreEntry.getCertificate();
            if (CertificateId.SERVER.getAlias().equals(alias)) {
                checkServerAuthExtension(certificate);
            }
            shouldSet = Objects.isNull(certificateChainCurrent) || !match;
            if (shouldSet) {
                KeyStore.ProtectionParameter protectionParameterCurrent = new KeyStore.PasswordProtection(keyPassword.toCharArray());
                keyStoreCurrent.setEntry(alias, keyStoreEntry, protectionParameterCurrent);
            }
            logCertAddedMessage(shouldSet, certificate, alias);
        } catch (KeyStoreException | UnrecoverableEntryException | NoSuchAlgorithmException | CertificateParsingException e) {
            throw new InternalRuntimeException(String.format("Failed to add/set keystore key pair for: %s", alias), e);
        }
        return shouldSet;
    }

    private static void checkServerAuthExtension(X509Certificate certificate) throws CertificateParsingException, InternalRuntimeException {
        List<String> extendedKeyUsage = certificate.getExtendedKeyUsage();
        if (Objects.isNull(extendedKeyUsage) || !extendedKeyUsage.contains(ID_KP_SERVER_AUTH_OID)) {
            LOGGER.debug("Extended Key Usage for Server Authentication is not available for server certificate :: {}", certificate);
            String msg = "Invalid Server Certificate, Extended Key usage Server Auth missing for certificate :: %s";
            throw new InternalRuntimeException(String.format(msg, certificate.getSubjectX500Principal().getName()));
        }
    }

    private static void logCertAddedMessage(boolean added, X509Certificate certificate, String alias) {
        final String certSkipMsg = "Certificate already present. skip processing..,";
        final String certAddedMsg = "Certificate not found. Signal will be processed and certificate will be added.";
        String message = added ? certAddedMsg : certSkipMsg;
        LOGGER.debug("{} Alias: {}, SN: {}", message, alias, certificate.getSubjectX500Principal());
    }
}
