package org.graph.spectral.models.graphcore

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
    val consideredNodes = if (ignoreIsolatedNodes) {
        nodes().filter { degree(it) > 0 }.toSet()
    } else {
        nodes()
    }
    if (consideredNodes.isEmpty()) return true
    return subgraphView(consideredNodes).connectedComponents().size == 1
}
