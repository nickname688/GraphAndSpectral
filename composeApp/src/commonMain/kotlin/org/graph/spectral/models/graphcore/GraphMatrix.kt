package org.graph.spectral.models.graphcore

class AdjacencyMatrix(
    val nodes: List<String>,
    val values: Array<DoubleArray>
) {
    val order: Int get() = nodes.size

    operator fun get(row: Int, col: Int): Double = values[row][col]

    fun asIntRows(): List<List<Int>> {
        return values.map { row ->
            row.map { value -> if (value == 0.0) 0 else 1 }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AdjacencyMatrix) return false
        return nodes == other.nodes && values.contentDeepEquals(other.values)
    }

    override fun hashCode(): Int {
        return 31 * nodes.hashCode() + values.contentDeepHashCode()
    }
}

fun GraphCore.adjacencyMatrix(
    nodes: List<String> = nodesSorted()
): AdjacencyMatrix {
    require(nodes.toSet().size == nodes.size) { "Adjacency matrix node list must not contain duplicates." }
    val missing = nodes.filterNot(::containsNode)
    require(missing.isEmpty()) { "Unknown nodes in adjacency matrix request: $missing" }

    val index = nodes.withIndex().associate { it.value to it.index }
    val matrix = Array(nodes.size) { DoubleArray(nodes.size) }

    edges().forEach { edge ->
        val row = index.getValue(edge.first)
        val col = index.getValue(edge.second)
        matrix[row][col] = 1.0
        matrix[col][row] = 1.0
    }

    return AdjacencyMatrix(nodes, matrix)
}
