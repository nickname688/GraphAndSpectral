package org.graph.spectral.models.graphcore

/**
 * 连通分量，孤立点会作为单节点分量返回。
 *
 * 这个行为对固定阶数图搜索很重要，因为“孤立点仍然属于图”。
 */
fun GraphCore.connectedComponents(): List<Set<String>> {
    val visited = mutableSetOf<String>()
    val components = mutableListOf<Set<String>>()

    for (start in nodesSorted()) {
        if (start in visited) continue

        val component = mutableSetOf<String>()
        val stack = mutableListOf(start)
        visited.add(start)

        while (stack.isNotEmpty()) {
            val node = stack.removeAt(stack.lastIndex)
            component.add(node)

            for (neighbor in neighborsSorted(node)) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    stack.add(neighbor)
                }
            }
        }

        components.add(component)
    }

    return components
}

fun GraphCore.componentContaining(node: String): Set<String> {
    if (!containsNode(node)) return emptySet()
    return connectedComponents().firstOrNull { node in it } ?: emptySet()
}

fun GraphCore.isConnected(ignoreIsolatedNodes: Boolean = false): Boolean {
    // 某些谱极值候选图会临时带孤立点，调用方可选择忽略它们检查非平凡部分。
    val consideredNodes = if (ignoreIsolatedNodes) {
        nodes().filter { degree(it) > 0 }.toSet()
    } else {
        nodes()
    }
    if (consideredNodes.isEmpty()) return true
    return subgraphView(consideredNodes).connectedComponents().size == 1
}
