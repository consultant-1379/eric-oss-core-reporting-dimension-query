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

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

import com.ericsson.oss.air.cardq.utils.CommonUtils;

public class Graph {
    private static final Map<String, Vertex> EMPTY_MAP = new HashMap<>();
    // Vertex Index
    Map<String, Map<String, Vertex>> verticesByTypeLabel = new HashMap<>();


    // TODO this structure is now redundant and should be removed. Kept only to avoid breaking existing code.
    @Getter
    private final Map<Vertex, List<Vertex>> adjVertices = new HashMap<>();
    @Getter
    private final List<Vertex> startingVertices = new ArrayList<>();

    public Vertex getVertex(String label, String type) {
        return verticesByTypeLabel.getOrDefault(type, EMPTY_MAP).get(label);
    }

    public List<Vertex> findVertexByAttributes(String type, Map<String, String> attributes) {
        return verticesByTypeLabel.getOrDefault(type, EMPTY_MAP).values().stream()
                    .filter(vertex -> CommonUtils.containsAll(vertex.getAttributes(), attributes)).collect(Collectors.toList());
    }

    public List<Vertex> findVertexByType(String type) {
        return verticesByTypeLabel.getOrDefault(type, EMPTY_MAP).values().stream().collect(Collectors.toList());
    }

    public List<Vertex> getAdjVertices(String label, String type, Map<String, String> attributes) {
        return getVertex(label, type).getAdjVertices();
    }

    public List<Vertex> getAdjVertices(String label, String type) {
        return getVertex(label, type).getAdjVertices();
    }

    public void addVertex(Vertex v) {
        verticesByTypeLabel.putIfAbsent(v.getType(), new HashMap<>());
        verticesByTypeLabel.get(v.getType()).putIfAbsent(v.getLabel(), v);

        adjVertices.putIfAbsent(v, new ArrayList<>());
    }

    public void addVertex(String label, String type) {
        Vertex newVertex = new Vertex(label, type);
        addVertex(newVertex);

        adjVertices.putIfAbsent(newVertex, new ArrayList<>());
    }

    public void addVertex(String label, String type, Map<String, String> attributes) {
        Vertex newVertex = new Vertex(label, type, attributes);
        addVertex(newVertex);

        adjVertices.putIfAbsent(newVertex, new ArrayList<>());
    }

    public void addEdge(Vertex source, Vertex destination) {
        Vertex internalDestination = internalVertexIfExists(destination);
        internalVertexIfExists(source).addEdge(internalDestination);

        if (!adjVertices.get(source).contains(internalDestination)) {
            adjVertices.get(source).add(internalDestination);
        }
    }

    public int getOrder() {
        int order = 0;
        for (Map<String, Vertex> vertexMap : verticesByTypeLabel.values()) {
            order += vertexMap.size();
        }
        return order;
    }

    public Boolean isVertexExists(Vertex vertex) {
        return getVertex(vertex.getLabel(), vertex.getType()) != null;
    }

    public boolean hasEdge(Vertex source, Vertex destination) {
        return internalVertexIfExists(source).hasEdge(internalVertexIfExists(destination));
    }

    public void addStartingVertex(Vertex vertex) {
        startingVertices.add(vertex);
    }

    public void addVertexToGraph(Vertex source, Vertex destination) {
        addVertex(destination);
        addEdge(internalVertexIfExists(source), destination);
    }

    private Vertex internalVertexIfExists(Vertex vertex) {
        Vertex internal = getVertex(vertex.getLabel(), vertex.getType());
        return internal != null ? internal : vertex;
    }
}
