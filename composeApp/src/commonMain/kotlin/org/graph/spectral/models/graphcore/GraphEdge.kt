package org.graph.spectral.models.graphcore

class GraphEdge private constructor(
    val first: String,
    val second: String
) {
    init {
        require(first != second) { "Self-loops are not allowed in a simple graph." }
    }

    fun contains(node: String): Boolean = node == first || node == second

    fun other(node: String): String? = when (node) {
        first -> second
        second -> first
        else -> null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GraphEdge) return false
        return first == other.first && second == other.second
    }

    override fun hashCode(): Int {
        return 31 * first.hashCode() + second.hashCode()
    }

    override fun toString(): String = "$first-$second"

    companion object {
        fun of(nodeA: String, nodeB: String): GraphEdge {
            require(nodeA != nodeB) { "Self-loops are not allowed in a simple graph." }
            return if (GraphNodeOrdering.compare(nodeA, nodeB) <= 0) {
                GraphEdge(nodeA, nodeB)
            } else {
                GraphEdge(nodeB, nodeA)
            }
        }
    }
}
