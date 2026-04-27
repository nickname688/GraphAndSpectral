package org.graph.spectral.models.graphcore

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GraphCoreTest {
    @Test
    fun keepsIsolatedNodesUntilExplicitRemoval() {
        val graph = GraphCore()
        graph.addNodes(listOf("1", "2", "3"))
        graph.addEdge("1", "2")

        assertEquals(3, graph.order())
        assertEquals(1, graph.size())
        assertEquals(0, graph.degree("3"))

        graph.removeEdge("1", "2")
        assertEquals(setOf("1", "2", "3"), graph.nodes())
        assertEquals(setOf("1", "2", "3"), graph.isolatedNodes())

        val removed = graph.removeIsolatedNodes()
        assertEquals(setOf("1", "2", "3"), removed)
        assertTrue(graph.isEmpty())
    }

    @Test
    fun copyIsIndependent() {
        val original = GraphCore()
        original.addEdge("1", "2")

        val copy = original.copy()
        copy.addEdge("2", "3")

        assertTrue(original.containsEdge("1", "2"))
        assertFalse(original.containsNode("3"))
        assertTrue(copy.containsEdge("2", "3"))
    }

    @Test
    fun connectedComponentsIncludeIsolates() {
        val graph = GraphCore()
        graph.addNodes(listOf("1", "2", "3", "4", "5"))
        graph.addEdge("1", "2")
        graph.addEdge("2", "3")
        graph.addEdge("4", "5")
        graph.addNode("9")

        val components = graph.connectedComponents().map { it.toSet() }

        assertEquals(
            listOf(setOf("1", "2", "3"), setOf("4", "5"), setOf("9")),
            components
        )
    }

    @Test
    fun adjacencyMatrixUsesStableNumericNodeOrdering() {
        val graph = GraphCore()
        graph.addEdge("10", "2")
        graph.addEdge("1", "10")

        val matrix = graph.adjacencyMatrix()

        assertEquals(listOf("1", "2", "10"), matrix.nodes)
        assertEquals(
            listOf(
                listOf(0, 0, 1),
                listOf(0, 0, 1),
                listOf(1, 1, 0)
            ),
            matrix.asIntRows()
        )
    }

    @Test
    fun inducedSubgraphCopiesOnlySelectedEdges() {
        val graph = GraphCore()
        graph.addEdge("1", "2")
        graph.addEdge("2", "3")
        graph.addEdge("3", "4")

        val subgraph = graph.inducedSubgraph(listOf("1", "2", "3"))

        assertEquals(setOf("1", "2", "3"), subgraph.nodes())
        assertEquals(setOf(GraphEdge.of("1", "2"), GraphEdge.of("2", "3")), subgraph.edges())
        assertFalse(subgraph.containsNode("4"))
    }

    @Test
    fun subgraphViewReflectsSourceGraphChanges() {
        val graph = GraphCore()
        graph.addNodes(listOf("1", "2", "3"))
        graph.addEdge("1", "2")

        val view = graph.subgraphView(listOf("1", "2", "3"))
        assertEquals(1, view.size())

        graph.addEdge("2", "3")
        assertEquals(2, view.size())
        assertEquals(setOf("1", "3"), view.neighbors("2"))
    }

    @Test
    fun contractEdgeMergesEndpointNeighborhoods() {
        val graph = GraphCore()
        graph.addEdge("1", "2")
        graph.addEdge("1", "3")
        graph.addEdge("2", "4")
        graph.addEdge("4", "5")

        val contracted = graph.contractEdge(GraphEdge.of("1", "2"), "x")

        assertEquals(setOf("x", "3", "4", "5"), contracted.nodes())
        assertEquals(
            setOf(
                GraphEdge.of("x", "3"),
                GraphEdge.of("x", "4"),
                GraphEdge.of("4", "5")
            ),
            contracted.edges()
        )
    }
}
