package org.graph.spectral.models

import org.graph.spectral.models.graphcore.GraphCore

class GraphGenerator {
    fun getGraph(name: String): GraphCore {
        return when (name) {
            "C3/K3" -> getC3()
            "C4" -> getC4()
            "K4" -> getK4()
            "C5" -> getC5()
            "M5" -> getM5()
            else -> GraphCore()
        }
    }

    private fun getC3(): GraphCore {
        return GraphCore().also { graph ->
            graph.addEdge("1", "2")
            graph.addEdge("2", "3")
            graph.addEdge("1", "3")
        }
    }

    private fun getC4(): GraphCore {
        return GraphCore().also { graph ->
            graph.addEdge("1", "2")
            graph.addEdge("2", "3")
            graph.addEdge("3", "4")
            graph.addEdge("1", "4")
        }
    }

    private fun getK4(): GraphCore {
        return GraphCore().also { graph ->
            for (i in 1..4) {
                for (j in i + 1..4) {
                    graph.addEdge(i.toString(), j.toString())
                }
            }
        }
    }

    private fun getC5(): GraphCore {
        return GraphCore().also { graph ->
            graph.addEdge("1", "2")
            graph.addEdge("2", "3")
            graph.addEdge("3", "4")
            graph.addEdge("4", "5")
            graph.addEdge("5", "1")
        }
    }

    private fun getM5(): GraphCore {
        return getC4().also { graph ->
            for (i in 1..4) {
                graph.addEdge("5", i.toString())
            }
        }
    }

    fun getGraphByCommand(base: GraphCore, command: String): GraphCore? {
        val edgeGroups = parseCommand(command) ?: return null
        if (edgeGroups.isEmpty()) return null

        val graph = base.copy()
        edgeGroups.forEach { (nodesA, nodesB) ->
            nodesA.forEach { nodeA ->
                nodesB.forEach { nodeB ->
                    if (nodeA != nodeB) {
                        graph.addEdge(nodeA, nodeB)
                    }
                }
            }
        }
        return graph
    }

    private fun parseCommand(command: String): List<Pair<List<String>, List<String>>>? {
        val result = mutableListOf<Pair<List<String>, List<String>>>()
        val pendingSpecs = mutableListOf<String>()
        var index = 0

        fun addSpec(spec: String): Boolean {
            if (expandSpec(spec) == null) return false
            pendingSpecs.add(spec)
            if (pendingSpecs.size == 2) {
                val nodesA = expandSpec(pendingSpecs[0]) ?: return false
                val nodesB = expandSpec(pendingSpecs[1]) ?: return false
                result.add(nodesA to nodesB)
                pendingSpecs.clear()
            }
            return true
        }

        fun addDirectEdge(nodeA: String, nodeB: String): Boolean {
            if (!isNodeLabel(nodeA) || !isNodeLabel(nodeB)) return false
            result.add(listOf(nodeA) to listOf(nodeB))
            return true
        }

        while (index < command.length) {
            val char = command[index]
            when {
                char.isCommandSeparator() -> index += 1
                char == '(' || char == '（' -> {
                    val closeIndex = command.indexOfClosingParen(index + 1)
                    if (closeIndex == -1) return null
                    val spec = command.substring(index + 1, closeIndex).trim()
                    if (spec.isEmpty() || !addSpec(spec)) return null
                    index = closeIndex + 1
                }
                char == ')' || char == '）' -> return null
                else -> {
                    val start = index
                    while (
                        index < command.length &&
                        !command[index].isCommandSeparator() &&
                        command[index] != '(' &&
                        command[index] != '（' &&
                        command[index] != ')' &&
                        command[index] != '）'
                    ) {
                        index += 1
                    }
                    val token = command.substring(start, index).trim()
                    if (token.isNotEmpty()) {
                        if (pendingSpecs.isNotEmpty()) {
                            if (!addSpec(token)) return null
                        } else if (token.isSimpleEdgeToken()) {
                            val parts = token.split("-")
                            if (!addDirectEdge(parts[0], parts[1])) return null
                        } else if (token.isLegacyPairToken()) {
                            token.chunked(2).forEach { pair ->
                                if (!addDirectEdge(pair[0].toString(), pair[1].toString())) return null
                            }
                        } else if (!addSpec(token)) {
                            return null
                        }
                    }
                }
            }
        }

        if (pendingSpecs.isNotEmpty()) return null
        return result
    }

    private fun expandSpec(spec: String): List<String>? {
        if (spec.isEmpty()) return null
        if (spec.count { it == '-' } > 1 || spec.startsWith('-') || spec.endsWith('-')) {
            return null
        }

        if (!spec.contains('-')) {
            return if (isNodeLabel(spec)) listOf(spec) else null
        }

        val parts = spec.split("-")
        if (parts.size != 2) return null

        val start = parts[0].toIntOrNull() ?: return null
        val end = parts[1].toIntOrNull() ?: return null
        if (end - start < 0 || end - start >= 50) return null

        return (start..end).map { it.toString() }
    }

    private fun String.isSimpleEdgeToken(): Boolean {
        if (count { it == '-' } != 1 || startsWith('-') || endsWith('-')) return false
        val parts = split("-")
        return parts.size == 2 && isNodeLabel(parts[0]) && isNodeLabel(parts[1])
    }

    private fun String.isLegacyPairToken(): Boolean {
        return length >= 2 && length % 2 == 0 && all { it.isLetterOrDigit() }
    }

    private fun isNodeLabel(label: String): Boolean {
        return label.isNotEmpty() && label.all { it.isLetterOrDigit() }
    }

    private fun Char.isCommandSeparator(): Boolean {
        return isWhitespace() || this == ',' || this == '，' || this == ';' || this == '；'
    }

    private fun String.indexOfClosingParen(startIndex: Int): Int {
        for (i in startIndex until length) {
            if (this[i] == ')' || this[i] == '）') return i
            if (this[i] == '(' || this[i] == '（') return -1
        }
        return -1
    }
}
