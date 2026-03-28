package org.graph.spectral.models

class Graph {
    private val adjacencyMap: MutableMap<String, MutableSet<String>> = mutableMapOf()

    fun addEdge(node1: String, node2: String) {
        if (node1 != node2) {
            adjacencyMap.getOrPut(node1) { mutableSetOf() }.add(node2)
            adjacencyMap.getOrPut(node2) { mutableSetOf() }.add(node1)
        }
    }

    fun removeEdge(node1: String, node2: String) {
        adjacencyMap[node1]?.remove(node2)
        adjacencyMap[node2]?.remove(node1)
        // 清理孤立节点
        if (adjacencyMap[node1]?.isEmpty() == true) {
            adjacencyMap.remove(node1)
        }
        if (adjacencyMap[node2]?.isEmpty() == true) {
            adjacencyMap.remove(node2)
        }
    }

    fun removeNode(node: String) {
        // 先移除所有与该节点相关的边
        adjacencyMap[node]?.forEach { neighbor ->
            adjacencyMap[neighbor]?.remove(node)
            if (adjacencyMap[neighbor]?.isEmpty() == true) {
                adjacencyMap.remove(neighbor)
            }
        }
        // 再移除节点本身
        adjacencyMap.remove(node)
    }

    fun nodes(): Set<String> {
        return adjacencyMap.keys
    }

    fun edges(): Set<Pair<String, String>> {
        val edges = mutableSetOf<Pair<String, String>>()
        adjacencyMap.forEach { (node, neighbors) ->
            neighbors.forEach { neighbor ->
                if (node < neighbor) { // 避免重复
                    edges.add(node to neighbor)
                }
            }
        }
        return edges
    }

    fun order(): Int {
        return adjacencyMap.size
    }

    fun size(): Int {
        return edges().size
    }

    fun adjacencyMatrix(): Array<DoubleArray> {
        val nodesList = nodes().toList()
        val size = nodesList.size
        val matrix = Array(size) { DoubleArray(size) }
        
        val nodeIndex = nodesList.withIndex().associate { it.value to it.index }
        
        adjacencyMap.forEach { (node, neighbors) ->
            val row = nodeIndex[node] ?: -1
            if (row != -1) {
                neighbors.forEach { neighbor ->
                    val col = nodeIndex[neighbor] ?: -1
                    if (col != -1) {
                        matrix[row][col] = 1.0
                    }
                }
            }
        }
        
        return matrix
    }

    fun clear() {
        adjacencyMap.clear()
    }

    override fun toString(): String {
        return "Graph(nodes=${nodes()}, edges=${edges()})"
    }
}
