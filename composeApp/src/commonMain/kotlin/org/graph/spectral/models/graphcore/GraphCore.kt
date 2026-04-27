package org.graph.spectral.models.graphcore

class GraphCore {
    private val adjacency: MutableMap<String, MutableSet<String>> = mutableMapOf()

    constructor()

    constructor(nodes: Iterable<String>, edges: Iterable<GraphEdge> = emptyList()) {
        nodes.forEach(::addNode)
        edges.forEach(::addEdge)
    }

    fun addNode(node: String): Boolean {
        require(node.isNotEmpty()) { "Node label must not be empty." }
        if (node in adjacency) return false
        adjacency[node] = mutableSetOf()
        return true
    }

    fun addNodes(nodes: Iterable<String>) {
        nodes.forEach(::addNode)
    }

    fun removeNode(node: String): Boolean {
        val neighbors = adjacency.remove(node) ?: return false
        neighbors.forEach { neighbor ->
            adjacency[neighbor]?.remove(node)
        }
        return true
    }

    fun addEdge(nodeA: String, nodeB: String): Boolean {
        require(nodeA.isNotEmpty() && nodeB.isNotEmpty()) { "Node labels must not be empty." }
        if (nodeA == nodeB) return false

        addNode(nodeA)
        addNode(nodeB)

        val changedA = adjacency.getValue(nodeA).add(nodeB)
        val changedB = adjacency.getValue(nodeB).add(nodeA)
        return changedA || changedB
    }

    fun addEdge(edge: GraphEdge): Boolean = addEdge(edge.first, edge.second)

    fun removeEdge(nodeA: String, nodeB: String): Boolean {
        val changedA = adjacency[nodeA]?.remove(nodeB) ?: false
        val changedB = adjacency[nodeB]?.remove(nodeA) ?: false
        return changedA || changedB
    }

    fun removeEdge(edge: GraphEdge): Boolean = removeEdge(edge.first, edge.second)

    fun containsNode(node: String): Boolean = node in adjacency

    fun containsEdge(nodeA: String, nodeB: String): Boolean {
        if (nodeA == nodeB) return false
        return adjacency[nodeA]?.contains(nodeB) == true &&
            adjacency[nodeB]?.contains(nodeA) == true
    }

    fun containsEdge(edge: GraphEdge): Boolean = containsEdge(edge.first, edge.second)

    fun nodes(): Set<String> = adjacency.keys.toSet()

    fun nodesSorted(comparator: Comparator<String> = GraphNodeOrdering): List<String> {
        return nodes().sortedWith(comparator)
    }

    fun edges(): Set<GraphEdge> {
        val result = mutableSetOf<GraphEdge>()
        adjacency.forEach { (node, neighbors) ->
            neighbors.forEach { neighbor ->
                if (node != neighbor) {
                    result.add(GraphEdge.of(node, neighbor))
                }
            }
        }
        return result
    }

    fun neighbors(node: String): Set<String> = adjacency[node]?.toSet() ?: emptySet()

    fun neighborsSorted(
        node: String,
        comparator: Comparator<String> = GraphNodeOrdering
    ): List<String> {
        return neighbors(node).sortedWith(comparator)
    }

    fun degree(node: String): Int = adjacency[node]?.size ?: 0

    fun order(): Int = adjacency.size

    fun size(): Int = edges().size

    fun isEmpty(): Boolean = adjacency.isEmpty()

    fun clear() {
        adjacency.clear()
    }

    fun copy(): GraphCore {
        val copy = GraphCore()
        adjacency.keys.forEach(copy::addNode)
        edges().forEach(copy::addEdge)
        return copy
    }

    override fun toString(): String {
        return "GraphCore(nodes=${nodesSorted()}, edges=${edges().sortedWith(compareBy<GraphEdge> { it.first }.thenBy { it.second })})"
    }
}