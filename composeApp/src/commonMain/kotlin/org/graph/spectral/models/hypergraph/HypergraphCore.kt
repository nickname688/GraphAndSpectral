package org.graph.spectral.models.hypergraph

import org.graph.spectral.models.graphcore.GraphNodeOrdering

class HypergraphCore {
    private val nodes: MutableSet<String> = mutableSetOf()
    private val hyperedges: MutableSet<Hyperedge> = mutableSetOf()

    constructor()

    constructor(nodes: Iterable<String>, hyperedges: Iterable<Hyperedge> = emptyList()) {
        addNodes(nodes)
        hyperedges.forEach { addHyperedge(it.nodes) }
    }

    fun addNode(node: String): Boolean {
        val label = node.trim()
        require(Hyperedge.isNodeLabel(label)) { "节点标签只能包含字母或数字" }
        return nodes.add(label)
    }

    fun addNodes(nodes: Iterable<String>) {
        nodes.forEach(::addNode)
    }

    fun removeNode(node: String): Boolean {
        val label = node.trim()
        if (!nodes.remove(label)) return false
        hyperedges.removeAll { edge -> edge.contains(label) }
        return true
    }

    fun addHyperedge(nodes: List<String>): Boolean {
        val edge = Hyperedge.of(nodes)
        val currentUniformity = uniformity()
        require(currentUniformity == null || currentUniformity == edge.order) {
            "当前只支持k均匀超图：已有阶数为$currentUniformity，发现阶数为${edge.order}"
        }
        edge.nodes.forEach(::addNode)
        return hyperedges.add(edge)
    }

    fun removeHyperedge(nodes: List<String>): Boolean {
        return hyperedges.remove(Hyperedge.of(nodes))
    }

    fun containsNode(node: String): Boolean = node.trim() in nodes

    fun containsHyperedge(nodes: List<String>): Boolean {
        return Hyperedge.of(nodes) in hyperedges
    }

    fun nodes(): Set<String> = nodes.toSet()

    fun nodesSorted(comparator: Comparator<String> = GraphNodeOrdering): List<String> {
        return nodes.sortedWith(comparator)
    }

    fun hyperedges(): Set<Hyperedge> = hyperedges.toSet()

    fun hyperedgesSorted(): List<Hyperedge> {
        return hyperedges.sortedWith(hyperedgeComparator)
    }

    fun order(): Int = nodes.size

    fun size(): Int = hyperedges.size

    fun isEmpty(): Boolean = nodes.isEmpty() && hyperedges.isEmpty()

    fun uniformity(): Int? {
        val firstOrder = hyperedges.firstOrNull()?.order ?: return null
        return if (hyperedges.all { it.order == firstOrder }) firstOrder else null
    }

    fun copy(): HypergraphCore {
        return HypergraphCore(nodes, hyperedges)
    }

    fun connectedComponents(): List<HypergraphCore> {
        if (nodes.isEmpty()) return emptyList()

        val incident = mutableMapOf<String, MutableSet<String>>()
        nodes.forEach { incident[it] = mutableSetOf() }
        hyperedges.forEach { edge ->
            edge.nodes.forEach { node ->
                incident.getOrPut(node) { mutableSetOf() }.addAll(edge.nodes.filterNot { it == node })
            }
        }

        val visited = mutableSetOf<String>()
        val components = mutableListOf<HypergraphCore>()

        nodesSorted().forEach { start ->
            if (start in visited) return@forEach
            val queue = ArrayDeque<String>()
            val componentNodes = mutableSetOf<String>()
            queue.addLast(start)
            visited.add(start)

            while (queue.isNotEmpty()) {
                val node = queue.removeFirst()
                componentNodes.add(node)
                incident[node].orEmpty().sortedWith(GraphNodeOrdering).forEach { neighbor ->
                    if (visited.add(neighbor)) {
                        queue.addLast(neighbor)
                    }
                }
            }

            val componentEdges = hyperedges.filter { edge -> edge.nodes.all(componentNodes::contains) }
            components.add(HypergraphCore(componentNodes, componentEdges))
        }

        return components.sortedWith { left, right ->
            val leftFirst = left.nodesSorted().firstOrNull() ?: ""
            val rightFirst = right.nodesSorted().firstOrNull() ?: ""
            val firstCompare = GraphNodeOrdering.compare(leftFirst, rightFirst)
            if (firstCompare != 0) firstCompare else left.order().compareTo(right.order())
        }
    }

    override fun toString(): String {
        return "HypergraphCore(nodes=${nodesSorted()}, hyperedges=${hyperedgesSorted()})"
    }
}

private val hyperedgeComparator = Comparator<Hyperedge> { left, right ->
    val sizeCompare = left.nodes.size.compareTo(right.nodes.size)
    if (sizeCompare != 0) {
        sizeCompare
    } else {
        left.nodes.zip(right.nodes).firstNotNullOfOrNull { (leftNode, rightNode) ->
            GraphNodeOrdering.compare(leftNode, rightNode).takeIf { it != 0 }
        } ?: 0
    }
}
