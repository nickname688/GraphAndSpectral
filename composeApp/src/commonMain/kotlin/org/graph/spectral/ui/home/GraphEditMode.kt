package org.graph.spectral.ui.home

internal enum class GraphEditMode(
    val id: String,
    val label: String,
    val compactLabel: String
) {
    AddEdge("add_edge", "增加边", "加边"),
    DeleteEdge("delete_edge", "删除边", "删边"),
    DeleteNode("delete_node", "删除点", "删点");

    companion object {
        fun fromId(id: String): GraphEditMode {
            return entries.firstOrNull { it.id == id } ?: AddEdge
        }
    }
}
