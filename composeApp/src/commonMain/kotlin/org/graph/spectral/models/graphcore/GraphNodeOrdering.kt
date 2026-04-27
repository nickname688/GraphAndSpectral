package org.graph.spectral.models.graphcore

/**
 * 节点标签排序规则。
 *
 * Python 原型常用 "1"、"2" 这类数字字符串；这里优先按数字大小排，避免出现
 * 字典序里的 "1", "10", "2"。
 */
object GraphNodeOrdering : Comparator<String> {
    override fun compare(a: String, b: String): Int {
        val aInt = a.toIntOrNull()
        val bInt = b.toIntOrNull()
        if (aInt != null && bInt != null) {
            val numeric = aInt.compareTo(bInt)
            if (numeric != 0) return numeric
        }
        return a.compareTo(b)
    }
}
