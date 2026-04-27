package org.graph.spectral.models.graphcore

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
