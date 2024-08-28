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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class GraphTest {

    @Test
    public void testAddingVertex() {
        Graph graph = new Graph();
        graph.addVertex(new Vertex("Test-VirtualFunction-1", "vnf"));
        graph.addVertex("Test-VirtualFunction-2", "vnf");
        graph.addVertex("Test-VirtualFunction-2", "vnf");
        graph.addVertex("Test-VirtualFunction-3", "vnf", new HashMap<>());

        assertThat(graph.getAdjVertices().size()).isEqualTo(3);
    }

    @Test
    public void testAddingOfVertexAndEdges() {
        Graph graph = new Graph();
        graph.addVertex(new Vertex("Test-VirtualFunction", "vnf"));
        graph.addVertex(new Vertex("Test-NetFunction", "wirelessnetfunction"));
        graph.addEdge(new Vertex("Test-VirtualFunction", "vnf"), new Vertex("Test-NetFunction", "wirelessnetfunction"));
        graph.addEdge(new Vertex("Test-VirtualFunction", "vnf"), new Vertex("Test-NetFunction", "wirelessnetfunction"));

        assertThat(graph.getAdjVertices().size()).isEqualTo(2);
        assertThat(graph.getAdjVertices().get(new Vertex("Test-VirtualFunction", "vnf")).size()).isEqualTo(1);
    }

    @Test
    public void testAddingOfVertexAndEdgesOfSampleGraphObject() {
        Graph graph = getGraphDummyObject();

        assertThat(graph.getAdjVertices().size()).isEqualTo(7);
        assertThat(graph.getAdjVertices("Test-VirtualFunction", "vnf").size()).isEqualTo(1);
        assertThat(graph.getAdjVertices("Test-NSSI", "networkslicesubnet").size()).isEqualTo(2);
    }

    @Test
    public void testAddingVertexToGraph() {
        Graph graph = getGraphDummyObject();
        graph.addVertexToGraph(new Vertex("Test-VirtualFunction", "vnf"), new Vertex("Test-VirtualFunction-2", "vnf"));
        graph.addVertexToGraph(new Vertex("Test-VirtualFunction", "vnf"), new Vertex("Test-VirtualFunction-2", "vnf"));

        assertThat(graph.getAdjVertices().size()).isEqualTo(8);
        assertThat(graph.getAdjVertices("Test-VirtualFunction", "vnf").size()).isEqualTo(2);
    }

    @Test
    public void testAddingEdgeToNonExistentVertex() {
        Graph graph = new Graph();
        assertThrows(NullPointerException.class,
            () -> graph.addEdge(new Vertex("Test-VirtualFunction", "vnf"), new Vertex("Test-NetFunction", "wirelessnetfunction")));
    }

    public static Graph getGraphDummyObject() {
        Graph graph = new Graph();
        HashMap<String, String> hashMap = new HashMap<>();

        graph.addVertex("Test-ServiceProfile", "serviceprofile", hashMap);
        graph.addVertex("Test-VirtualFunction", "vnf");
        graph.addVertex("Test-NetFunction", "wirelessnetfunction");
        graph.addVertex("Test-NSSI", "networkslicesubnet");

        graph.addVertex("Test-SliceProfile", "sliceprofile", hashMap);
        graph.addVertex("Test-EntityCollection", "entitycollection");
        graph.addVertex("NSI", "networkslice");
        graph.addEdge(new Vertex("Test-VirtualFunction", "vnf"), new Vertex("Test-NetFunction", "wirelessnetfunction"));
        graph.addEdge(new Vertex("Test-NetFunction", "wirelessnetfunction"), new Vertex("Test-NSSI", "networkslicesubnet"));
        graph.addEdge(new Vertex("Test-NSSI", "networkslicesubnet"), new Vertex("Test-EntityCollection", "entitycollection"));
        graph.addEdge(new Vertex("Test-NSSI", "networkslicesubnet"), new Vertex("NSI", "networkslice"));

        return graph;
    }
}
