package org.graph.spectral.models.graphcore

fun GraphCore.isolatedNodes(): Set<String> {
    return nodes().filter { degree(it) == 0 }.toSet()
}

fun GraphCore.removeIsolatedNodes(): Set<String> {
    val isolated = isolatedNodes()
    isolated.forEach(::removeNode)
    return isolated
}

fun GraphCore.withoutIsolatedNodes(): GraphCore {
    val graph = copy()
    graph.removeIsolatedNodes()
    return graph
}

fun GraphCore.inducedSubgraph(nodes: Iterable<String>): GraphCore {
    val selected = nodes.toSet()
    val missing = selected.filterNot(::containsNode)
    require(missing.isEmpty()) { "Unknown nodes in subgraph request: $missing" }

    val graph = GraphCore()
    selected.forEach(graph::addNode)
    edges()
        .filter { edge -> edge.first in selected && edge.second in selected }
        .forEach(graph::addEdge)
    return graph
}

fun GraphCore.subgraphView(nodes: Iterable<String>): GraphSubgraphView {
    val selected = nodes.toSet()
    val missing = selected.filterNot(::containsNode)
    require(missing.isEmpty()) { "Unknown nodes in subgraph view request: $missing" }
    return GraphSubgraphView(this, selected)
}

fun GraphCore.contractEdge(edge: GraphEdge): GraphCore {
    return contractEdge(edge.first, edge.second, contractedNodeName(edge.first, edge.second))
}

fun GraphCore.contractEdge(edge: GraphEdge, contractedNode: String): GraphCore {
    return contractEdge(edge.first, edge.second, contractedNode)
}

fun GraphCore.contractEdge(
    nodeA: String,
    nodeB: String,
    contractedNode: String
): GraphCore {
    require(containsEdge(nodeA, nodeB)) { "Cannot contract missing edge $nodeA-$nodeB." }
    require(contractedNode.isNotEmpty()) { "Contracted node label must not be empty." }

    val endpoints = setOf(nodeA, nodeB)
    require(contractedNode !in nodes() || contractedNode in endpoints) {
        "Contracted node label '$contractedNode' already exists outside the contracted edge."
    }

    val graph = GraphCore()
    nodes()
        .filterNot { it in endpoints }
        .forEach(graph::addNode)
    graph.addNode(contractedNode)

    edges().forEach { edge ->
        val first = if (edge.first in endpoints) contractedNode else edge.first
        val second = if (edge.second in endpoints) contractedNode else edge.second
        graph.addEdge(first, second)
    }

    return graph
}

private fun GraphCore.contractedNodeName(nodeA: String, nodeB: String): String {
    val base = "${GraphEdge.of(nodeA, nodeB).first}+${GraphEdge.of(nodeA, nodeB).second}"
    if (base !in nodes()) return base

    var suffix = 2
    while ("$base#$suffix" in nodes()) {
        suffix += 1
    }
    return "$base#$suffix"
}

class GraphSubgraphView internal constructor(
    private val source: GraphCore,
    private val selectedNodes: Set<String>
) {
    fun containsNode(node: String): Boolean = node in selectedNodes && source.containsNode(node)

    fun nodes(): Set<String> {
        return selectedNodes.filter(source::containsNode).toSet()
    }

    fun nodesSorted(comparator: Comparator<String> = GraphNodeOrdering): List<String> {
        return nodes().sortedWith(comparator)
    }

    fun neighbors(node: String): Set<String> {
        if (!containsNode(node)) return emptySet()
        return source.neighbors(node).filter { it in selectedNodes }.toSet()
    }

    fun degree(node: String): Int = neighbors(node).size

    fun edges(): Set<GraphEdge> {
        return source.edges()
            .filter { edge -> edge.first in selectedNodes && edge.second in selectedNodes }
            .toSet()
    }

    fun order(): Int = nodes().size

    fun size(): Int = edges().size

    fun connectedComponents(): List<Set<String>> = toGraph().connectedComponents()

    fun adjacencyMatrix(nodes: List<String> = nodesSorted()): AdjacencyMatrix {
        return toGraph().adjacencyMatrix(nodes)
    }

    fun toGraph(): GraphCore = source.inducedSubgraph(nodes())
}
