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

        while (index < trimmed.length) {
            while (index < trimmed.length && trimmed[index].isOuterSeparator()) {
                index += 1
            }
            if (index >= trimmed.length) break

            val open = trimmed[index]
            require(open == '(' || open == '（') { "超边需要使用括号表示" }

            val closeIndex = trimmed.indexOfClosingParen(index + 1)
            require(closeIndex != -1) { "缺少右括号" }

            val content = trimmed.substring(index + 1, closeIndex)
            val edge = parseSingleHyperedge(content)
            val currentOrder = expectedOrder
            require(currentOrder == null || currentOrder == edge.order) {
                "当前只支持k均匀超图：第一条超边阶数为$currentOrder，发现阶数为${edge.order}"
            }
            expectedOrder = edge.order
            result.add(edge)
            index = closeIndex + 1
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
}
