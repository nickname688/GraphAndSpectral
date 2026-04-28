package org.graph.spectral.models.hypergraph

class HypergraphCommandParser {
    data class ParseResult(
        val hyperedges: List<Hyperedge>,
        val uniformity: Int
    )

    fun parseHyperedges(command: String): ParseResult {
        val trimmed = command.trim()
        require(trimmed.isNotEmpty()) { "请输入超边" }

        val result = mutableListOf<Hyperedge>()
        var expectedOrder: Int? = null
        var index = 0

        fun addEdges(edges: List<Hyperedge>) {
            edges.forEach { edge ->
                val currentOrder = expectedOrder
                require(currentOrder == null || currentOrder == edge.order) {
                    "当前只支持k均匀超图：第一条超边阶数为$currentOrder，发现阶数为${edge.order}"
                }
                expectedOrder = edge.order
                result.add(edge)
            }
        }

        while (index < trimmed.length) {
            while (index < trimmed.length && trimmed[index].isOuterSeparator()) {
                index += 1
            }
            if (index >= trimmed.length) break

            if (trimmed[index] == '(' || trimmed[index] == '（') {
                val closeIndex = trimmed.indexOfClosingParen(index + 1)
                require(closeIndex != -1) { "缺少右括号" }

                val content = trimmed.substring(index + 1, closeIndex)
                addEdges(listOf(parseSingleHyperedge(content)))
                index = closeIndex + 1
            } else {
                val nextIndex = trimmed.indexOfTokenEnd(index)
                val token = trimmed.substring(index, nextIndex).trim()
                require(token.isNotEmpty()) { "请输入超边" }
                addEdges(parseShortcut(token))
                index = nextIndex
            }
        }

        require(result.isNotEmpty()) { "请输入超边" }
        return ParseResult(result, expectedOrder ?: result.first().order)
    }

    private fun parseSingleHyperedge(content: String): Hyperedge {
        val parts = content.split(Regex("[,，\\s]+")).map(String::trim).filter(String::isNotEmpty)
        require(parts.size >= 2) { "超边至少包含2个不同节点" }
        require(parts.all { Hyperedge.isNodeLabel(it) }) { "节点标签只能包含字母或数字" }
        require(parts.toSet().size == parts.size) { "同一超边中不能包含重复节点" }
        return Hyperedge.of(parts)
    }

    private fun parseShortcut(token: String): List<Hyperedge> {
        val compact = token.filterNot { it.isWhitespace() }.uppercase()
        val caretMatch = Regex("""([KSP])(\d+)\^(\d+)""").matchEntire(compact)
        val parenMatch = Regex("""([KSP])\((\d+)[,，](\d+)\)""").matchEntire(compact)
        val match = caretMatch ?: parenMatch
        require(match != null) { "无法识别超图指令: $token" }

        val command = match.groupValues[1]
        val first = match.groupValues[2].toInt()
        val uniformity = match.groupValues[3].toInt()
        require(uniformity >= 2) { "k至少为2" }

        return when (command) {
            "K" -> generateComplete(first, uniformity)
            "S" -> generateStar(first, uniformity)
            "P" -> generateLoosePath(first, uniformity)
            else -> error("无法识别超图指令: $token")
        }
    }

    private fun generateComplete(order: Int, uniformity: Int): List<Hyperedge> {
        require(order >= uniformity) { "K指令需要满足顶点数n >= k" }
        return combinations((1..order).map { it.toString() }, uniformity)
            .also { requireGeneratedSize(it.size) }
            .map(Hyperedge::of)
    }

    private fun generateStar(order: Int, uniformity: Int): List<Hyperedge> {
        require(order >= uniformity) { "S指令需要满足顶点数n >= k" }
        return combinations((2..order).map { it.toString() }, uniformity - 1)
            .also { requireGeneratedSize(it.size) }
            .map { nodes -> Hyperedge.of(listOf("1") + nodes) }
    }

    private fun generateLoosePath(edgeCount: Int, uniformity: Int): List<Hyperedge> {
        require(edgeCount >= 1) { "P指令需要至少1条超边" }
        requireGeneratedSize(edgeCount)
        return (0 until edgeCount).map { edgeIndex ->
            val start = edgeIndex * (uniformity - 1) + 1
            Hyperedge.of((start until start + uniformity).map { it.toString() })
        }
    }

    private fun combinations(items: List<String>, size: Int): List<List<String>> {
        val count = combinationCount(items.size, size)
        requireGeneratedSize(count)

        val result = mutableListOf<List<String>>()
        val current = mutableListOf<String>()

        fun visit(start: Int) {
            if (current.size == size) {
                result.add(current.toList())
                return
            }

            for (index in start until items.size) {
                current.add(items[index])
                visit(index + 1)
                current.removeAt(current.lastIndex)
            }
        }

        visit(0)
        return result
    }

    private fun combinationCount(n: Int, k: Int): Int {
        if (k < 0 || k > n) return 0
        val effectiveK = minOf(k, n - k)
        var result = 1L
        for (i in 1..effectiveK) {
            result = result * (n - effectiveK + i) / i
            require(result <= MaxGeneratedHyperedges) { "生成的超边过多，最多支持${MaxGeneratedHyperedges}条" }
        }
        return result.toInt()
    }

    private fun requireGeneratedSize(size: Int) {
        require(size <= MaxGeneratedHyperedges) { "生成的超边过多，最多支持${MaxGeneratedHyperedges}条" }
    }

    private fun Char.isOuterSeparator(): Boolean {
        return isWhitespace() || this == ';' || this == '；' || this == ',' || this == '，'
    }

    private fun String.indexOfClosingParen(startIndex: Int): Int {
        for (i in startIndex until length) {
            if (this[i] == ')' || this[i] == '）') return i
            if (this[i] == '(' || this[i] == '（') return -1
        }
        return -1
    }

    private fun String.indexOfTokenEnd(startIndex: Int): Int {
        var depth = 0
        for (i in startIndex until length) {
            val char = this[i]
            when {
                char == '(' || char == '（' -> depth += 1
                char == ')' || char == '）' -> depth -= 1
                depth == 0 && char.isOuterSeparator() -> return i
            }
            require(depth >= 0) { "括号不匹配" }
        }
        require(depth == 0) { "括号不匹配" }
        return length
    }

    private companion object {
        const val MaxGeneratedHyperedges = 5000
    }
}
