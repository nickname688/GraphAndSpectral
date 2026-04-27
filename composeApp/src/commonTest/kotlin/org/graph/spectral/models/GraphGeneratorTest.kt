package org.graph.spectral.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.graph.spectral.models.graphcore.GraphCore
import org.graph.spectral.models.graphcore.GraphEdge

class GraphGeneratorTest {
    private val generator = GraphGenerator()

    @Test
    fun presetsMatchExpectedGraphs() {
        assertGraph("C3/K3", order = 3, size = 3)
        assertGraph("C4", order = 4, size = 4)
        assertGraph("K4", order = 4, size = 6)
        assertGraph("C5", order = 5, size = 5)
        assertGraph("M5", order = 5, size = 8)
    }

    @Test
    fun parsesWhitespaceSeparatedEdges() {
        val graph = generator.getGraphByCommand(GraphCore(), "1-2 2-3")

        assertNotNull(graph)
        assertEquals(setOf(GraphEdge.of("1", "2"), GraphEdge.of("2", "3")), graph.edges())
    }

    @Test
    fun parsesCommaSeparatedEdges() {
        val graph = generator.getGraphByCommand(GraphCore(), "1-2,2-3")

        assertNotNull(graph)
        assertEquals(setOf(GraphEdge.of("1", "2"), GraphEdge.of("2", "3")), graph.edges())
    }

    @Test
    fun parsesParenthesizedRangeGroups() {
        val graph = generator.getGraphByCommand(GraphCore(), "(1-3)(4-5)")

        assertNotNull(graph)
        assertEquals(5, graph.order())
        assertEquals(6, graph.size())
        assertEquals(
            setOf(
                GraphEdge.of("1", "4"),
                GraphEdge.of("1", "5"),
                GraphEdge.of("2", "4"),
                GraphEdge.of("2", "5"),
                GraphEdge.of("3", "4"),
                GraphEdge.of("3", "5")
            ),
            graph.edges()
        )
    }

    @Test
    fun parsesLegacyContinuousPairs() {
        val graph = generator.getGraphByCommand(GraphCore(), "12")

        assertNotNull(graph)
        assertEquals(setOf(GraphEdge.of("1", "2")), graph.edges())
    }

    @Test
    fun invalidCommandDoesNotMutateOriginalGraph() {
        val original = GraphCore().also { it.addEdge("1", "2") }
        val result = generator.getGraphByCommand(original, "1-")

        assertNull(result)
        assertEquals(setOf(GraphEdge.of("1", "2")), original.edges())
        assertFalse(original.containsNode("3"))
    }

    private fun assertGraph(name: String, order: Int, size: Int) {
        val graph = generator.getGraph(name)
        assertEquals(order, graph.order(), "$name order")
        assertEquals(size, graph.size(), "$name size")
    }
}
