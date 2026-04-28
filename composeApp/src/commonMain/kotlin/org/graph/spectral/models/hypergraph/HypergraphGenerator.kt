package org.graph.spectral.models.hypergraph

data class PresetHypergraphOption(
    val id: String,
    val name: String
)

class HypergraphGenerator {
    val presetHypergraphs: List<PresetHypergraphOption> = listOf(
        PresetHypergraphOption("E3", "单条3元超边 E3"),
        PresetHypergraphOption("P3^3", "3均匀松路径 P3^3"),
        PresetHypergraphOption("P4^3", "3均匀松路径 P4^3"),
        PresetHypergraphOption("S5^3", "3均匀星形超图 S5^3"),
        PresetHypergraphOption("S6^3", "3均匀星形超图 S6^3"),
        PresetHypergraphOption("K4^3", "完整3均匀超图 K4^3"),
        PresetHypergraphOption("K5^3", "完整3均匀超图 K5^3"),
        PresetHypergraphOption("K4^2", "完整2均匀超图 K4^2")
    )

    private val parser = HypergraphCommandParser()

    fun getHypergraph(name: String): HypergraphCore {
        return when (name.toPresetId()) {
            "E3" -> buildFromCommand("(1,2,3)")
            "P3^3" -> buildFromCommand("P3^3")
            "P4^3" -> buildFromCommand("P4^3")
            "S5^3" -> buildFromCommand("S5^3")
            "S6^3" -> buildFromCommand("S6^3")
            "K4^3" -> buildFromCommand("K4^3")
            "K5^3" -> buildFromCommand("K5^3")
            "K4^2" -> buildFromCommand("K4^2")
            else -> HypergraphCore()
        }
    }

    fun getPresetDisplayName(id: String): String {
        return presetHypergraphs.firstOrNull { it.id == id }?.name ?: "选择预设超图"
    }

    private fun buildFromCommand(command: String): HypergraphCore {
        val parsed = parser.parseHyperedges(command)
        return HypergraphCore().also { graph ->
            parsed.hyperedges.forEach { edge -> graph.addHyperedge(edge.nodes) }
        }
    }

    private fun String.toPresetId(): String {
        val trimmed = trim()
        return presetHypergraphs.firstOrNull { it.name == trimmed }?.id ?: trimmed
    }
}
