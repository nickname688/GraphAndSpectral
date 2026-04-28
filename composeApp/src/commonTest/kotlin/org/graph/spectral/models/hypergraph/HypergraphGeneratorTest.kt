package org.graph.spectral.models.hypergraph

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HypergraphGeneratorTest {
    private val generator = HypergraphGenerator()

    @Test
    fun presetsBuildExpectedUniformHypergraphs() {
        assertHypergraph("E3", order = 3, size = 1, uniformity = 3)
        assertHypergraph("P3^3", order = 7, size = 3, uniformity = 3)
        assertHypergraph("P4^3", order = 9, size = 4, uniformity = 3)
        assertHypergraph("S5^3", order = 5, size = 6, uniformity = 3)
        assertHypergraph("S6^3", order = 6, size = 10, uniformity = 3)
        assertHypergraph("K4^3", order = 4, size = 4, uniformity = 3)
        assertHypergraph("K5^3", order = 5, size = 10, uniformity = 3)
        assertHypergraph("K4^2", order = 4, size = 6, uniformity = 2)
    }

    @Test
    fun presetDisplayNamesCanBuildHypergraphs() {
        val graph = generator.getHypergraph("完整3均匀超图 K4^3")

        assertEquals(4, graph.order())
        assertEquals(4, graph.size())
        assertEquals("完整3均匀超图 K4^3", generator.getPresetDisplayName("K4^3"))
    }

    @Test
    fun unknownPresetReturnsEmptyHypergraph() {
        assertTrue(generator.getHypergraph("unknown").isEmpty())
    }

    private fun assertHypergraph(name: String, order: Int, size: Int, uniformity: Int) {
        val graph = generator.getHypergraph(name)
        assertEquals(order, graph.order(), "$name order")
        assertEquals(size, graph.size(), "$name size")
        assertEquals(uniformity, graph.uniformity(), "$name uniformity")
    }
}
