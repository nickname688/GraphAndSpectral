package org.graph.spectral.models

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.graph.spectral.models.graphcore.GraphCore
import org.graph.spectral.models.graphcore.adjacencyMatrix

class EigenCalculatorTest {
    private val generator = GraphGenerator()
    private val calculator = EigenCalculator()

    @Test
    fun knownPresetSpectralRadiiAreCorrect() {
        assertSpectralRadius("C3/K3", 2.0)
        assertSpectralRadius("C4", 2.0)
        assertSpectralRadius("K4", 3.0)
        assertSpectralRadius("C5", 2.0)
    }

    @Test
    fun matrixAndEigenvectorUseSameNodeOrder() {
        val graph = GraphCore().also {
            it.addEdge("10", "2")
            it.addEdge("1", "10")
        }

        val matrix = graph.adjacencyMatrix()
        val result = calculator.calculate(graph)

        assertNotNull(result)
        assertEquals(listOf("1", "2", "10"), matrix.nodes)
        assertEquals(matrix.nodes, result.nodes)
    }

    @Test
    fun resultIncludesSortedEdgeSet() {
        val graph = GraphCore().also {
            it.addEdge("10", "2")
            it.addEdge("1", "10")
        }

        val result = calculator.calculate(graph)

        assertNotNull(result)
        assertEquals(listOf("1-10", "2-10"), result.edges.map { it.toString() })
        assertTrue(calculator.formatResult(result).contains("边集: (1, 10), (2, 10)"))
    }

    @Test
    fun edgelessGraphHasZeroSpectralRadiusAndUndefinedPfVector() {
        val graph = GraphCore().also {
            it.addNode("1")
            it.addNode("2")
            it.addEdge("1", "2")
            it.removeEdge("1", "2")
        }

        val result = calculator.calculate(graph)

        assertNotNull(result)
        assertEquals(0.0, result.maxEigenvalue)
        assertNull(result.eigenvector)
        assertEquals(listOf("1", "2"), result.nodes)
    }

    private fun assertSpectralRadius(name: String, expected: Double) {
        val result = calculator.calculate(generator.getGraph(name))
        assertNotNull(result)
        assertTrue(abs(result.maxEigenvalue - expected) < 1e-6, "$name spectral radius")
    }
}
