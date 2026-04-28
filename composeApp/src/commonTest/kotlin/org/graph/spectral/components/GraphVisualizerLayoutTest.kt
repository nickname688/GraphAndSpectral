package org.graph.spectral.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.graph.spectral.models.graphcore.GraphCore

class GraphVisualizerLayoutTest {
    @Test
    fun emptyGraphHasNoPositions() {
        val graph = GraphCore()

        val positions = layoutGraphNodes(graph, width = 320f, height = 240f, nodeRadius = 20f)

        assertTrue(positions.isEmpty())
    }

    @Test
    fun singleNodeIsCentered() {
        val graph = GraphCore().also { it.addNode("1") }

        val positions = layoutGraphNodes(graph, width = 320f, height = 240f, nodeRadius = 20f)

        assertEquals(160f, positions.getValue("1").x)
        assertEquals(120f, positions.getValue("1").y)
    }

    @Test
    fun allNodesStayInsideCanvas() {
        val graph = GraphCore().also {
            for (i in 1..12) {
                it.addEdge(i.toString(), ((i % 12) + 1).toString())
            }
        }

        val positions = layoutGraphNodes(graph, width = 500f, height = 360f, nodeRadius = 20f)

        assertEquals(12, positions.size)
        positions.values.forEach { position ->
            assertTrue(position.x in 0f..500f)
            assertTrue(position.y in 0f..360f)
        }
    }

    @Test
    fun disconnectedComponentsStillReceivePositions() {
        val graph = GraphCore().also {
            it.addEdge("1", "2")
            it.addEdge("3", "4")
            it.addNode("9")
        }

        val positions = layoutGraphNodes(graph, width = 480f, height = 320f, nodeRadius = 20f)

        assertEquals(setOf("1", "2", "3", "4", "9"), positions.keys)
    }
}
