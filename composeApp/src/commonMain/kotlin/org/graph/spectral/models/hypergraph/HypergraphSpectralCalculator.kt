package org.graph.spectral.models.hypergraph

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.pow

data class HyperSpectralOptions(
    val tolerance: Double = 1e-10,
    val maxIterations: Int = 5000
)

data class HyperSpectralResult(
    val spectralRadius: Double,
    val uniformity: Int?,
    val nodeCount: Int,
    val edgeCount: Int,
    val eigenvector: List<Double>?,
    val nodes: List<String>,
    val hyperedges: List<Hyperedge>,
    val iterations: Int,
    val residual: Double,
    val collatzLowerBound: Double,
    val collatzUpperBound: Double,
    val converged: Boolean
)

class HypergraphSpectralCalculator {
    fun calculate(
        graph: HypergraphCore,
        options: HyperSpectralOptions = HyperSpectralOptions()
    ): HyperSpectralResult {
        val nodes = graph.nodesSorted()
        val hyperedges = graph.hyperedgesSorted()
        val uniformity = graph.uniformity()

        require(options.tolerance > 0.0) { "tolerance must be positive." }
        require(options.maxIterations > 0) { "maxIterations must be positive." }

        if (nodes.isEmpty() || hyperedges.isEmpty()) {
            return zeroResult(graph, nodes, hyperedges, uniformity)
        }

        val k = requireNotNull(uniformity) { "当前只支持k均匀超图" }
        val componentResults = graph.connectedComponents()
            .filter { it.size() > 0 }
            .map { component -> calculateConnectedComponent(component, k, options) }

        if (componentResults.isEmpty()) {
            return zeroResult(graph, nodes, hyperedges, uniformity)
        }

        val best = componentResults.maxWith(
            compareBy<ComponentSpectralResult> { it.spectralRadius }
                .thenByDescending { it.converged }
        )
        val componentVector = best.nodes.zip(best.eigenvector).toMap()
        val fullVector = nodes.map { node -> componentVector[node] ?: 0.0 }

        return HyperSpectralResult(
            spectralRadius = best.spectralRadius,
            uniformity = uniformity,
            nodeCount = graph.order(),
            edgeCount = graph.size(),
            eigenvector = fullVector,
            nodes = nodes,
            hyperedges = hyperedges,
            iterations = best.iterations,
            residual = best.residual,
            collatzLowerBound = best.collatzLowerBound,
            collatzUpperBound = best.collatzUpperBound,
            converged = best.converged
        )
    }

    private fun calculateConnectedComponent(
        graph: HypergraphCore,
        uniformity: Int,
        options: HyperSpectralOptions
    ): ComponentSpectralResult {
        val nodes = graph.nodesSorted()
        val index = nodes.withIndex().associate { it.value to it.index }
        val edges = graph.hyperedgesSorted().map { edge ->
            edge.nodes.map(index::getValue).toIntArray()
        }
        val n = nodes.size
        var vector = DoubleArray(n) { n.toDouble().pow(-1.0 / uniformity) }
        var lambda = 0.0
        var lastState = spectralState(vector, edges, uniformity)
        val shift = 1.0

        for (iteration in 1..options.maxIterations) {
            val adjacencyProduct = adjacencyProduct(vector, edges)
            val shiftedProduct = DoubleArray(n) { i ->
                adjacencyProduct[i] + shift * vector[i].pow(uniformity - 1)
            }
            val nextVector = shiftedProduct
                .map { value -> value.coerceAtLeast(0.0).pow(1.0 / (uniformity - 1)) }
                .toDoubleArray()
                .normalizeByKNorm(uniformity)

            val state = spectralState(nextVector, edges, uniformity)
            val lambdaDelta = abs(state.lambda - lambda)
            vector = nextVector
            lambda = state.lambda
            lastState = state

            if (state.residual < options.tolerance && lambdaDelta < options.tolerance) {
                return ComponentSpectralResult(
                    spectralRadius = state.lambda,
                    eigenvector = vector.toList(),
                    nodes = nodes,
                    iterations = iteration,
                    residual = state.residual,
                    collatzLowerBound = state.lowerBound,
                    collatzUpperBound = state.upperBound,
                    converged = true
                )
            }
        }

        return ComponentSpectralResult(
            spectralRadius = lastState.lambda,
            eigenvector = vector.toList(),
            nodes = nodes,
            iterations = options.maxIterations,
            residual = lastState.residual,
            collatzLowerBound = lastState.lowerBound,
            collatzUpperBound = lastState.upperBound,
            converged = false
        )
    }

    private fun adjacencyProduct(vector: DoubleArray, edges: List<IntArray>): DoubleArray {
        val result = DoubleArray(vector.size)
        edges.forEach { edge ->
            var product = 1.0
            edge.forEach { index -> product *= vector[index] }
            edge.forEach { index ->
                result[index] += product / vector[index]
            }
        }
        return result
    }

    private fun spectralState(
        vector: DoubleArray,
        edges: List<IntArray>,
        uniformity: Int
    ): SpectralState {
        val product = adjacencyProduct(vector, edges)
        var numerator = 0.0
        var denominator = 0.0
        var lowerBound = Double.POSITIVE_INFINITY
        var upperBound = 0.0

        for (i in vector.indices) {
            val denominatorPart = vector[i].pow(uniformity - 1)
            numerator += vector[i] * product[i]
            denominator += vector[i].pow(uniformity)
            val ratio = product[i] / denominatorPart
            lowerBound = minOf(lowerBound, ratio)
            upperBound = max(upperBound, ratio)
        }

        val lambda = if (denominator == 0.0) 0.0 else numerator / denominator
        var residual = 0.0
        for (i in vector.indices) {
            residual = max(residual, abs(product[i] - lambda * vector[i].pow(uniformity - 1)))
        }

        return SpectralState(
            lambda = lambda,
            residual = residual,
            lowerBound = lowerBound.takeIf(Double::isFinite) ?: 0.0,
            upperBound = upperBound.takeIf(Double::isFinite) ?: 0.0
        )
    }

    fun formatResult(result: HyperSpectralResult): String {
        val uniformityText = result.uniformity?.toString() ?: "未定义"
        val vectorText = result.eigenvector
            ?.let { vector ->
                result.nodes.zip(vector).joinToString(", ") { (node, value) ->
                    "$node: ${value.round(4)}"
                }
            }
            ?: "未定义（无超边）"
        val hyperedgeText = if (result.hyperedges.isEmpty()) {
            "空"
        } else {
            result.hyperedges.joinToString(", ")
        }

        return buildString {
            append("k: $uniformityText")
            append("\n顶点数: ${result.nodeCount}")
            append("\n超边数: ${result.edgeCount}")
            append("\n谱半径: ${result.spectralRadius.round(6)}")
            append("\n收敛: ${if (result.converged) "是" else "否"}")
            append("\n迭代次数: ${result.iterations}")
            append("\n残差: ${result.residual.round(8)}")
            append("\nCollatz区间: [${result.collatzLowerBound.round(6)}, ${result.collatzUpperBound.round(6)}]")
            append("\n顶点集: ${result.nodes.joinToString(", ")}")
            append("\n超边集: $hyperedgeText")
            append("\nPF向量: $vectorText")
        }
    }

    private fun zeroResult(
        graph: HypergraphCore,
        nodes: List<String>,
        hyperedges: List<Hyperedge>,
        uniformity: Int?
    ): HyperSpectralResult {
        return HyperSpectralResult(
            spectralRadius = 0.0,
            uniformity = uniformity,
            nodeCount = graph.order(),
            edgeCount = graph.size(),
            eigenvector = null,
            nodes = nodes,
            hyperedges = hyperedges,
            iterations = 0,
            residual = 0.0,
            collatzLowerBound = 0.0,
            collatzUpperBound = 0.0,
            converged = true
        )
    }

    private fun DoubleArray.normalizeByKNorm(uniformity: Int): DoubleArray {
        val norm = sumOf { value -> value.pow(uniformity) }.pow(1.0 / uniformity)
        if (norm == 0.0) return this
        return map { it / norm }.toDoubleArray()
    }

    private fun Double.round(decimals: Int): String {
        val multiplier = 10.0.pow(decimals)
        return (kotlin.math.round(this * multiplier) / multiplier).toString()
    }

    private data class SpectralState(
        val lambda: Double,
        val residual: Double,
        val lowerBound: Double,
        val upperBound: Double
    )

    private data class ComponentSpectralResult(
        val spectralRadius: Double,
        val eigenvector: List<Double>,
        val nodes: List<String>,
        val iterations: Int,
        val residual: Double,
        val collatzLowerBound: Double,
        val collatzUpperBound: Double,
        val converged: Boolean
    )
}
