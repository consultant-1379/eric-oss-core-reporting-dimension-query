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

package com.ericsson.oss.air.cardq.cache;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class VertexTest {

    @Test
    public void testEqualsSymmetric() {
        Vertex x = new Vertex("Test-EntityCollection", "entitycollection");
        Vertex y = new Vertex("Test-EntityCollection", "entitycollection");
        assertThat(x).isEqualTo(y);
        assertThat(y).isEqualTo(x);
        assertThat(x.hashCode()).isEqualTo(y.hashCode());
    }

    @Test
    public void testEqualsSymmetricWithAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("test", "value");
        Vertex x = new Vertex("Test-EntityCollection", "entitycollection", attributes);
        Vertex y = new Vertex("Test-EntityCollection", "entitycollection", attributes);
        assertThat(x).isEqualTo(y);
        assertThat(y).isEqualTo(x);
        assertThat(x.hashCode()).isEqualTo(y.hashCode());
    }

    @Test
    public void testNotEqualsSymmetricWithAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("test", "value");
        Vertex x = new Vertex("Test-EntityCollection1", "entitycollection", attributes);
        Vertex y = new Vertex("Test-EntityCollection2", "entitycollection", attributes);
        assertThat(x).isNotEqualTo(y);
        assertThat(y).isNotEqualTo(x);
        assertThat(x.hashCode()).isNotEqualTo(y.hashCode());
    }
}
