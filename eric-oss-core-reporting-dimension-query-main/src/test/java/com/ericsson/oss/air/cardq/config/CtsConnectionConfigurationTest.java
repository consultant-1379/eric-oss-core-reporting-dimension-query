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

package com.ericsson.oss.air.cardq.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import com.ericsson.oss.air.cardq.AbstractTestSetup;

@SpringBootTest(classes = {CtsConnectionConfiguration.class}, properties = {
    "cts.url=https://eric-oss-cmn-topology-svc-core",
    "cts.user=ericsson",
    "cts.password=testpass",
    "cts.databaseName=testdb",
    "cts.databaseHost=testhost"})
@EnableConfigurationProperties
public class CtsConnectionConfigurationTest extends AbstractTestSetup {
    @Autowired
    private CtsConnectionConfiguration ctsConnectionConfiguration;

    @Test
    public void testCtsConnectionDetails() {
        assertThat(ctsConnectionConfiguration.getUrl()).isEqualTo("https://eric-oss-cmn-topology-svc-core");
        assertThat(ctsConnectionConfiguration.getUser()).isEqualTo("ericsson");
        assertThat(ctsConnectionConfiguration.getPassword()).isEqualTo("testpass");
        assertThat(ctsConnectionConfiguration.getDatabaseHost()).isEqualTo("testhost");
        assertThat(ctsConnectionConfiguration.getDatabaseName()).isEqualTo("testdb");
    }
}