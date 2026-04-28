package org.graph.spectral.models.hypergraph

import org.graph.spectral.models.graphcore.GraphNodeOrdering

data class Hyperedge(val nodes: List<String>) {
    init {
        require(nodes.size >= 2) { "超边至少包含2个不同节点" }
        require(nodes.all { isNodeLabel(it) }) { "节点标签只能包含字母或数字" }
        require(nodes.toSet().size == nodes.size) { "同一超边中不能包含重复节点" }
    }

    val order: Int get() = nodes.size

    fun contains(node: String): Boolean = node in nodes

    override fun toString(): String = nodes.joinToString(prefix = "(", postfix = ")")

    companion object {
        fun of(nodes: Iterable<String>): Hyperedge {
            return Hyperedge(nodes.map(String::trim).sortedWith(GraphNodeOrdering))
        }

        internal fun isNodeLabel(label: String): Boolean {
            return label.isNotEmpty() && label.all { it.isLetterOrDigit() }
        }
    }
}
