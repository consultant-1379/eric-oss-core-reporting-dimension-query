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

package com.ericsson.oss.air.cardq.config;

import java.io.IOException;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.EncodedResource;

public class YamlPropertySourceFactoryTest {

    private final YamlPropertySourceFactory yamlPropertySourceFactory = new YamlPropertySourceFactory();

    @Test
    void testCreatePropertySource() throws IOException {
        Assertions.assertNotNull(yamlPropertySourceFactory.createPropertySource("test",
            new EncodedResource(new UrlResource(Objects.requireNonNull(this.getClass().getClassLoader().getResource("fixtures"))))));
    }
}
