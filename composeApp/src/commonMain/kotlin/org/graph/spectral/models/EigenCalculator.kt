package org.graph.spectral.models

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import org.graph.spectral.models.graphcore.AdjacencyMatrix
import org.graph.spectral.models.graphcore.GraphCore
import org.graph.spectral.models.graphcore.GraphEdge
import org.graph.spectral.models.graphcore.GraphNodeOrdering
import org.graph.spectral.models.graphcore.adjacencyMatrix

class EigenCalculator {
    data class EigenResult(
        val maxEigenvalue: Double,
        val edgeCount: Int,
        val eigenvector: List<Double>?,
        val nodes: List<String>,
        val edges: List<GraphEdge>
    )

    fun calculate(graph: GraphCore): EigenResult? {
        if (graph.order() == 0) return null

        val matrix = graph.adjacencyMatrix()
        val (maxEigenvalue, eigenvector) = powerMethod(matrix.values)

        return EigenResult(
            maxEigenvalue = maxEigenvalue,
            edgeCount = graph.size(),
            eigenvector = eigenvector,
            nodes = matrix.nodes,
            edges = graph.edges().sortedWith(edgeComparator)
        )
    }

    private val edgeComparator = Comparator<GraphEdge> { left, right ->
        val firstCompare = GraphNodeOrdering.compare(left.first, right.first)
        if (firstCompare != 0) {
            firstCompare
        } else {
            GraphNodeOrdering.compare(left.second, right.second)
        }
    }

    private fun powerMethod(
        matrix: Array<DoubleArray>,
        maxIterations: Int = 1000,
        tolerance: Double = 1e-10
    ): Pair<Double, List<Double>?> {
        val n = matrix.size
        if (n == 0) return 0.0 to null

        var vector = DoubleArray(n) { 1.0 / sqrt(n.toDouble()) }
        var lambda = rayleighQuotient(matrix, vector)

        for (iteration in 0 until maxIterations) {
            val product = multiply(matrix, vector)
            val norm = product.norm()
            if (norm <= tolerance) {
                return 0.0 to null
            }

            val nextVector = product.map { it / norm }.toDoubleArray()
            val nextLambda = rayleighQuotient(matrix, nextVector)
            if (abs(nextLambda - lambda) < tolerance) {
                return nextLambda to normalizeSign(nextVector).toList()
            }

            vector = nextVector
            lambda = nextLambda
        }

        return lambda to normalizeSign(vector).toList()
    }

    private fun multiply(matrix: Array<DoubleArray>, vector: DoubleArray): DoubleArray {
        return DoubleArray(matrix.size) { row ->
            var sum = 0.0
            for (col in vector.indices) {
                sum += matrix[row][col] * vector[col]
            }
            sum
        }
    }

    private fun rayleighQuotient(matrix: Array<DoubleArray>, vector: DoubleArray): Double {
        val product = multiply(matrix, vector)
        var numerator = 0.0
        var denominator = 0.0
        for (i in vector.indices) {
            numerator += vector[i] * product[i]
            denominator += vector[i] * vector[i]
        }
        return if (denominator == 0.0) 0.0 else numerator / denominator
    }

    private fun DoubleArray.norm(): Double {
        return sqrt(sumOf { it * it })
    }

    private fun normalizeSign(vector: DoubleArray): DoubleArray {
        val firstNonZero = vector.firstOrNull { abs(it) > 1e-12 } ?: return vector
        return if (firstNonZero < 0) vector.map { -it }.toDoubleArray() else vector
    }

    fun formatResult(result: EigenResult): String {
        val eigenvectorText = result.eigenvector
            ?.joinToString(", ") { it.round(3) }
            ?: "未定义（零矩阵）"

        return buildString {
            append("顶点数: ${result.nodes.size}")
            append("\n边数: ${result.edgeCount}")
            append("\n特征值: ${result.maxEigenvalue.round(4)}")
            append("\n顶点集: ${result.nodes.joinToString(", ")}")
            append("\n边集: ${formatEdges(result.edges)}")
            append("\nPF向量: $eigenvectorText")
        }
    }

    fun formatAdjacencyMatrix(matrix: AdjacencyMatrix): String {
        return matrix.asIntRows().joinToString("\n") { row ->
            row.joinToString(" ")
        }
    }

    private fun Double.round(decimals: Int): String {
        val multiplier = 10.0.pow(decimals)
        return (kotlin.math.round(this * multiplier) / multiplier).toString()
    }

    private fun formatEdges(edges: List<GraphEdge>): String {
        if (edges.isEmpty()) return "空"
        return edges.joinToString(", ") { "(${it.first}, ${it.second})" }
    }
}
