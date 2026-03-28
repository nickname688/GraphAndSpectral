package org.graph.spectral.models

import kotlin.math.abs
import kotlin.math.pow

class EigenCalculator {
    data class EigenResult(
        val maxEigenvalue: Double,
        val eigenvector: List<Double>,
        val nodes: List<String>
    )

    fun calculate(graph: Graph): EigenResult? {
        val matrix = graph.adjacencyMatrix()
        val size = matrix.size
        if (size == 0) return null

        // 使用幂法计算最大特征值和对应的特征向量
        val (maxEigenvalue, eigenvector) = powerMethod(matrix)
        val nodes = graph.nodes().toList().sorted()

        return EigenResult(maxEigenvalue, eigenvector, nodes)
    }

    private fun powerMethod(matrix: Array<DoubleArray>, maxIterations: Int = 1000, tolerance: Double = 1e-10): Pair<Double, List<Double>> {
        val n = matrix.size
        var x = DoubleArray(n) { 1.0 / n }
        var lambda = 0.0

        for (iter in 0 until maxIterations) {
            val y = DoubleArray(n) { i ->
                var sum = 0.0
                for (j in 0 until n) {
                    sum += matrix[i][j] * x[j]
                }
                sum
            }

            // 计算新的特征值（使用向量的和）
            val newLambda = y.sum()
            // 归一化特征向量
            x = y.map { it / newLambda }.toDoubleArray()

            if (abs(newLambda - lambda) < tolerance) {
                lambda = newLambda
                break
            }
            lambda = newLambda
        }

        // 确保特征向量的第一个元素为正（Perron-Frobenius向量）
        if (x[0] < 0) {
            x = x.map { -it }.toDoubleArray()
        }

        return lambda to x.toList()
    }

    fun formatResult(result: EigenResult): String {
        val sb = StringBuilder()
        sb.append("顶点数: ${result.nodes.size}")
        sb.append("\n特征值: ${result.maxEigenvalue.round(4)}") // 保留4位小数
        sb.append("\n顶点集: ${result.nodes.joinToString(", ")}")
        sb.append("\nPF向量: ${result.eigenvector.joinToString(", ") { it.round(3) }}") // 保留3位
        return sb.toString()
    }

    // 跨平台通用：保留 N 位小数
    private fun Double.round(decimals: Int): String {
        val multiplier = 10.0.pow(decimals)
        return (kotlin.math.round(this * multiplier) / multiplier).toString()
    }
    fun formatAdjacencyMatrix(matrix: Array<DoubleArray>): String {
        val sb = StringBuilder()
        for (row in matrix) {
            sb.append(row.joinToString(" ") { if (it == 1.0) "1" else "0" })
            sb.append("\n")
        }
        return sb.toString()
    }
}