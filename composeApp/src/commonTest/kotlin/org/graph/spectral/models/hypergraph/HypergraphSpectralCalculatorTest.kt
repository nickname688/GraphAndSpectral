package org.graph.spectral.models.hypergraph

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HypergraphSpectralCalculatorTest {
    private val calculator = HypergraphSpectralCalculator()

    @Test
    fun singleUniformHyperedgeHasSpectralRadiusOne() {
        val graph = HypergraphCore().also {
            it.addHyperedge(listOf("1", "2", "3", "4"))
        }

        val result = calculator.calculate(graph)

        assertClose(1.0, result.spectralRadius)
        assertTrue(result.converged)
        assertEquals(4, result.uniformity)
    }

    @Test
    fun completeThreeUniformHypergraphsHaveExpectedSpectralRadii() {
        assertClose(3.0, calculator.calculate(completeUniformHypergraph(4, 3)).spectralRadius)
        assertClose(6.0, calculator.calculate(completeUniformHypergraph(5, 3)).spectralRadius)
    }

    @Test
    fun twoUniformTriangleMatchesGraphSpectralRadius() {
        val triangle = completeUniformHypergraph(3, 2)

        val result = calculator.calculate(triangle)

        assertClose(2.0, result.spectralRadius)
        assertTrue(result.converged)
    }

    @Test
    fun emptyAndEdgelessHypergraphsHaveZeroSpectralRadius() {
        val emptyResult = calculator.calculate(HypergraphCore())
        assertClose(0.0, emptyResult.spectralRadius)
        assertNull(emptyResult.eigenvector)

        val edgeless = HypergraphCore().also { it.addNodes(listOf("1", "2", "3")) }
        val edgelessResult = calculator.calculate(edgeless)
        assertClose(0.0, edgelessResult.spectralRadius)
        assertNull(edgelessResult.eigenvector)
    }

    @Test
    fun disconnectedHypergraphUsesLargestComponentSpectralRadius() {
        val graph = completeUniformHypergraph(4, 3)
        graph.addHyperedge(listOf("8", "9", "10"))
        graph.addNode("20")

        val result = calculator.calculate(graph)

        assertClose(3.0, result.spectralRadius)
        assertEquals(8, result.nodeCount)
        assertEquals(5, result.edgeCount)
        assertTrue(result.eigenvector != null && result.eigenvector.size == result.nodes.size)
    }

    private fun completeUniformHypergraph(order: Int, uniformity: Int): HypergraphCore {
        val graph = HypergraphCore()
        combinations((1..order).map { it.toString() }, uniformity).forEach(graph::addHyperedge)
        return graph
    }

    private fun combinations(items: List<String>, size: Int): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val current = mutableListOf<String>()

        fun visit(start: Int) {
            if (current.size == size) {
                result.add(current.toList())
                return
            }
            for (index in start until items.size) {
                current.add(items[index])
                visit(index + 1)
                current.removeAt(current.lastIndex)
            }
        }

        visit(0)
        return result
    }

    private fun assertClose(expected: Double, actual: Double, tolerance: Double = 1e-6) {
        assertTrue(abs(expected - actual) < tolerance, "Expected $expected but was $actual")
    }
}
