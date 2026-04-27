package org.graph.spectral.models.graphcore

// 返回当前所有孤立点，但不修改图。
fun GraphCore.isolatedNodes(): Set<String> {
    return nodes().filter { degree(it) == 0 }.toSet()
}

// 显式删除孤立点；GraphCore.removeEdge() 不会自动做这件事。
fun GraphCore.removeIsolatedNodes(): Set<String> {
    val isolated = isolatedNodes()
    isolated.forEach(::removeNode)
    return isolated
}

// 返回去掉孤立点后的副本，原图保持不变。
fun GraphCore.withoutIsolatedNodes(): GraphCore {
    val graph = copy()
    graph.removeIsolatedNodes()
    return graph
}

/**
 * 诱导子图：复制选中节点，以及这些节点之间原本存在的边。
 */
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

/**
 * 子图视图：不复制数据，会反映 source 后续的边变化。
 */
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
    // 边收缩用于外平面 minor 检查：两个端点合并成一个新点，并继承两边的邻居。
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

/**
 * GraphCore 的动态子图视图。
 *
 * 它只保存“选中的节点集合”，边和邻接关系每次都从 source 读取。
 */
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
