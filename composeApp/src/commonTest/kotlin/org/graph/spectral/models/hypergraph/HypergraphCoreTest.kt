package org.graph.spectral.models.hypergraph

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HypergraphCoreTest {
    @Test
    fun storesHyperedgesInCanonicalNodeOrder() {
        val graph = HypergraphCore()

        assertTrue(graph.addHyperedge(listOf("3", "1", "2")))
        assertFalse(graph.addHyperedge(listOf("2", "3", "1")))

        assertEquals(3, graph.uniformity())
        assertEquals(listOf("1", "2", "3"), graph.nodesSorted())
        assertEquals(listOf(Hyperedge(listOf("1", "2", "3"))), graph.hyperedgesSorted())
    }

    @Test
    fun rejectsNonUniformHyperedges() {
        val graph = HypergraphCore()
        graph.addHyperedge(listOf("1", "2", "3"))

        assertFailsWith<IllegalArgumentException> {
            graph.addHyperedge(listOf("1", "2"))
        }
    }

    @Test
    fun rejectsRepeatedNodesInsideOneHyperedge() {
        assertFailsWith<IllegalArgumentException> {
            HypergraphCore().addHyperedge(listOf("1", "1", "2"))
        }
    }

    @Test
    fun removingHyperedgeKeepsIsolatedNodes() {
        val graph = HypergraphCore()
        graph.addHyperedge(listOf("1", "2", "3"))

        assertTrue(graph.removeHyperedge(listOf("3", "2", "1")))

        assertEquals(setOf("1", "2", "3"), graph.nodes())
        assertEquals(0, graph.size())
        assertEquals(null, graph.uniformity())
    }

    @Test
    fun connectedComponentsIncludeIsolatedNodes() {
        val graph = HypergraphCore()
        graph.addNodes(listOf("1", "2", "3", "4", "5", "6", "9"))
        graph.addHyperedge(listOf("1", "2", "3"))
        graph.addHyperedge(listOf("4", "5", "6"))

        val components = graph.connectedComponents()

        assertEquals(3, components.size)
        assertEquals(listOf("1", "2", "3"), components[0].nodesSorted())
        assertEquals(listOf("4", "5", "6"), components[1].nodesSorted())
        assertEquals(listOf("9"), components[2].nodesSorted())
    }
}
