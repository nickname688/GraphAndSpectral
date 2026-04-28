package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.graph.spectral.models.hypergraph.HypergraphCommandParser
import org.graph.spectral.models.hypergraph.HypergraphCore
import org.graph.spectral.models.hypergraph.HypergraphGenerator
import org.graph.spectral.models.hypergraph.HypergraphSpectralCalculator

@Composable
fun HypergraphScreen(paddingValues: PaddingValues) {
    var hypergraph by remember { mutableStateOf(HypergraphCore()) }
    var hyperedgeCommand by remember { mutableStateOf("") }
    var deleteHyperedgeCommand by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var selectedHypergraph by remember { mutableStateOf("选择预设超图") }
    var autoCompute by remember { mutableStateOf(true) }
    var showPresetSheet by remember { mutableStateOf(false) }

    val hypergraphParser = HypergraphCommandParser()
    val hypergraphCalculator = HypergraphSpectralCalculator()
    val hypergraphGenerator = HypergraphGenerator()

    fun showError(message: String) {
        result = "错误：$message"
    }

    fun updateHypergraph(nextHypergraph: HypergraphCore) {
        hypergraph = nextHypergraph
        if (autoCompute) {
            val hypergraphResult = hypergraphCalculator.calculate(nextHypergraph)
            result = hypergraphCalculator.formatResult(hypergraphResult)
        } else {
            result = ""
        }
    }

    fun runHypergraphCompute() {
        val hypergraphResult = hypergraphCalculator.calculate(hypergraph)
        result = hypergraphCalculator.formatResult(hypergraphResult)
    }

    fun submitHyperedgeCommand() {
        runCatching {
            val parsed = hypergraphParser.parseHyperedges(hyperedgeCommand)
            val nextHypergraph = hypergraph.copy()
            parsed.hyperedges.forEach { edge ->
                nextHypergraph.addHyperedge(edge.nodes)
            }
            nextHypergraph
        }.onSuccess { nextHypergraph ->
            hyperedgeCommand = ""
            selectedHypergraph = "自定义"
            updateHypergraph(nextHypergraph)
        }.onFailure { error ->
            showError(error.message ?: "输入有误")
        }
    }

    fun submitDeleteHyperedgeCommand() {
        runCatching {
            val parsed = hypergraphParser.parseHyperedges(deleteHyperedgeCommand)
            val missing = parsed.hyperedges.filterNot { edge ->
                hypergraph.containsHyperedge(edge.nodes)
            }
            require(missing.isEmpty()) { "不存在超边: ${missing.joinToString(", ")}" }

            val nextHypergraph = hypergraph.copy()
            parsed.hyperedges.forEach { edge ->
                nextHypergraph.removeHyperedge(edge.nodes)
            }
            nextHypergraph
        }.onSuccess { nextHypergraph ->
            deleteHyperedgeCommand = ""
            selectedHypergraph = "自定义"
            updateHypergraph(nextHypergraph)
        }.onFailure { error ->
            showError(error.message ?: "输入有误")
        }
    }

    CustomKeyboardHost {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HypergraphControlPanel(
                        hyperedgeCommand = hyperedgeCommand,
                        onHyperedgeCommandChange = { hyperedgeCommand = it },
                        onAddHyperedges = { submitHyperedgeCommand() },
                        deleteHyperedgeCommand = deleteHyperedgeCommand,
                        onDeleteHyperedgeCommandChange = { deleteHyperedgeCommand = it },
                        onDeleteHyperedges = { submitDeleteHyperedgeCommand() },
                        autoCompute = autoCompute,
                        onAutoComputeChange = { autoCompute = it },
                        onRunCompute = { runHypergraphCompute() },
                        selectedHypergraph = selectedHypergraph,
                        onOpenPresetSheet = { showPresetSheet = true },
                        onClearHypergraph = {
                            hypergraph = HypergraphCore()
                            hyperedgeCommand = ""
                            deleteHyperedgeCommand = ""
                            selectedHypergraph = "选择预设超图"
                            result = ""
                        }
                    )
                }

                if (result.isNotEmpty()) {
                    item {
                        ResultTextCard(title = "计算结果", text = result)
                    }
                }
            }

            if (showPresetSheet) {
                PresetHypergraphBottomSheet(
                    hypergraphGenerator = hypergraphGenerator,
                    selectedHypergraph = selectedHypergraph,
                    onPresetSelected = { presetId ->
                        selectedHypergraph = hypergraphGenerator.getPresetDisplayName(presetId)
                        showPresetSheet = false
                        updateHypergraph(hypergraphGenerator.getHypergraph(presetId))
                    },
                    onDismiss = { showPresetSheet = false }
                )
            }
        }
    }
}
