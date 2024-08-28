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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ericsson.oss.air.cardq.utils.CommonUtils;

import lombok.Data;
import lombok.ToString;

@Data
public class Vertex {
    private static final Map<String, Vertex> EMPTY_MAP = new HashMap<>();
    private String label;
    private String type;
    private Map<String, String> attributes = new HashMap<>();

    @ToString.Exclude
    private Map<String, Map<String, Vertex>> edgesMap = new HashMap<>();

    public void addEdge(Vertex destination) {
        String destinationType = destination.getType();
        edgesMap.putIfAbsent(destinationType, new HashMap<>());
        edgesMap.get(destinationType).putIfAbsent(destination.getLabel(), destination);
    }

    public List<Vertex> getAdjVertices() {
        return edgesMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
    }

    public Collection<Vertex> getAdjVerticesByType(String type) {
        return edgesMap.getOrDefault(type, EMPTY_MAP).values();
    }

    public List<Vertex> getAdjVerticesByAttr(String type, Map<String, String> attributes) {
        return edgesMap.getOrDefault(type, EMPTY_MAP).values().stream()
                    .filter(vertex -> CommonUtils.containsAll(this.attributes, attributes)).collect(Collectors.toList());
    }

    public boolean hasEdge(Vertex destination) {
        return edgesMap.getOrDefault(destination.getType(), EMPTY_MAP).get(destination.getLabel()) != null;
    }

    public Vertex(String label, String type) {
        this.label = label;
        this.type = type;
    }

    public Vertex(String label, String type, Map<String, String> attributes) {
        this.label = label;
        this.type = type;
        this.attributes = attributes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Vertex vertex = (Vertex) o;
        return Objects.equals(label, vertex.label) && Objects.equals(type, vertex.type) && Objects.equals(attributes,
                                                                                                          vertex.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, type, attributes);
    }
}
