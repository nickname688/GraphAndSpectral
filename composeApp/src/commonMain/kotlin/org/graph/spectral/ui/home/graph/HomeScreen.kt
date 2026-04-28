package org.graph.spectral.ui.home.graph

import org.graph.spectral.ui.home.common.AutoComputeRow
import org.graph.spectral.ui.home.common.EditModeBottomSheet
import org.graph.spectral.ui.home.common.PresetGraphBottomSheet
import org.graph.spectral.ui.home.common.ResultTextCard
import org.graph.spectral.ui.toolUI.CustomKeyboardHost

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
import org.graph.spectral.computeResult
import org.graph.spectral.models.EigenCalculator
import org.graph.spectral.models.GraphGenerator
import org.graph.spectral.models.graphcore.GraphCore

@Composable
fun HomeScreen(paddingValues: PaddingValues) {
    var graph by remember { mutableStateOf(GraphCore()) }
    var node1 by remember { mutableStateOf("") }
    var node2 by remember { mutableStateOf("") }
    var delNode1 by remember { mutableStateOf("") }
    var delNode2 by remember { mutableStateOf("") }
    var delNode by remember { mutableStateOf("") }
    var command by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var matrixResult by remember { mutableStateOf("") }
    var selectedGraph by remember { mutableStateOf("选择预设图") }
    var autoCompute by remember { mutableStateOf(true) }
    var selectedEditMode by remember { mutableStateOf(GraphEditMode.AddEdge) }
    var showEditModeSheet by remember { mutableStateOf(false) }
    var showGraphVisualizer by remember { mutableStateOf(false) }
    var showPresetSheet by remember { mutableStateOf(false) }

    val graphGenerator = GraphGenerator()
    val eigenCalculator = EigenCalculator()

    fun showError(message: String) {
        result = "错误：$message"
        matrixResult = ""
    }

    fun updateGraph(nextGraph: GraphCore) {
        graph = nextGraph
        if (autoCompute) {
            computeResult(nextGraph, eigenCalculator) { r, m ->
                result = r
                matrixResult = m
            }
        } else {
            result = ""
            matrixResult = ""
        }
    }

    fun runCompute() {
        computeResult(graph, eigenCalculator) { r, m ->
            result = r
            matrixResult = m
        }
    }

    fun firstInputOf(mode: GraphEditMode): String {
        return when (mode) {
            GraphEditMode.AddEdge -> node1
            GraphEditMode.DeleteEdge -> delNode1
            GraphEditMode.DeleteNode -> delNode
        }
    }

    fun secondInputOf(mode: GraphEditMode): String {
        return when (mode) {
            GraphEditMode.AddEdge -> node2
            GraphEditMode.DeleteEdge -> delNode2
            GraphEditMode.DeleteNode -> ""
        }
    }

    fun selectEditMode(mode: GraphEditMode) {
        if (mode == selectedEditMode) return

        val previousFirst = firstInputOf(selectedEditMode)
        val previousSecond = secondInputOf(selectedEditMode)

        when (mode) {
            GraphEditMode.AddEdge -> {
                if (node1.isEmpty()) node1 = previousFirst
                if (node2.isEmpty()) node2 = previousSecond
            }
            GraphEditMode.DeleteEdge -> {
                if (delNode1.isEmpty()) delNode1 = previousFirst
                if (delNode2.isEmpty()) delNode2 = previousSecond
            }
            GraphEditMode.DeleteNode -> {
                if (delNode.isEmpty()) delNode = previousFirst
            }
        }

        selectedEditMode = mode
    }

    fun submitEditOperation() {
        when (selectedEditMode) {
            GraphEditMode.AddEdge -> {
                val first = node1.trim()
                val second = node2.trim()
                when {
                    first.isEmpty() || second.isEmpty() -> showError("未输入参数")
                    first == second -> showError("不允许加入自环")
                    else -> {
                        val nextGraph = graph.copy().also { it.addEdge(first, second) }
                        node1 = ""
                        node2 = ""
                        selectedGraph = "自定义"
                        updateGraph(nextGraph)
                    }
                }
            }
            GraphEditMode.DeleteEdge -> {
                val first = delNode1.trim()
                val second = delNode2.trim()
                when {
                    first.isEmpty() || second.isEmpty() -> showError("未输入参数")
                    !graph.containsEdge(first, second) -> showError("不存在边")
                    else -> {
                        val nextGraph = graph.copy().also { it.removeEdge(first, second) }
                        delNode1 = ""
                        delNode2 = ""
                        selectedGraph = "自定义"
                        updateGraph(nextGraph)
                    }
                }
            }
            GraphEditMode.DeleteNode -> {
                val node = delNode.trim()
                when {
                    node.isEmpty() -> showError("未输入参数")
                    !graph.containsNode(node) -> showError("不存在点")
                    else -> {
                        val nextGraph = graph.copy().also { it.removeNode(node) }
                        delNode = ""
                        selectedGraph = "自定义"
                        updateGraph(nextGraph)
                    }
                }
            }
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
                    HomeControlPanel(
                        command = command,
                        onCommandChange = { command = it },
                        onSubmitCommand = {
                            val resultGraph = graphGenerator.getGraphByCommand(graph, command.trim())
                            if (resultGraph != null) {
                                selectedGraph = "自定义"
                                updateGraph(resultGraph)
                            } else {
                                showError("输入有误")
                            }
                        },
                        selectedEditMode = selectedEditMode,
                        node1 = node1,
                        onNode1Change = { node1 = it },
                        node2 = node2,
                        onNode2Change = { node2 = it },
                        delNode1 = delNode1,
                        onDelNode1Change = { delNode1 = it },
                        delNode2 = delNode2,
                        onDelNode2Change = { delNode2 = it },
                        delNode = delNode,
                        onDelNodeChange = { delNode = it },
                        onOpenEditModeSheet = { showEditModeSheet = true },
                        onSubmitEditOperation = { submitEditOperation() },
                        autoCompute = autoCompute,
                        onAutoComputeChange = { autoCompute = it },
                        onRunCompute = { runCompute() },
                        selectedGraph = selectedGraph,
                        onOpenPresetSheet = { showPresetSheet = true },
                        onClearGraph = {
                            graph = GraphCore()
                            selectedGraph = "选择预设图"
                            result = ""
                            matrixResult = ""
                        }
                    )
                }

                item {
                    GraphVisualizerSection(
                        graph = graph,
                        showGraphVisualizer = showGraphVisualizer,
                        onShowGraphVisualizer = { showGraphVisualizer = true },
                        onHideGraphVisualizer = { showGraphVisualizer = false }
                    )
                }

                if (result.isNotEmpty()) {
                    item {
                        ResultTextCard(title = "计算结果", text = result)
                    }
                }

                if (matrixResult.isNotEmpty()) {
                    item {
                        ResultTextCard(title = "邻接矩阵", text = matrixResult)
                    }
                }
            }

            if (showPresetSheet) {
                PresetGraphBottomSheet(
                    graphGenerator = graphGenerator,
                    selectedGraph = selectedGraph,
                    onPresetSelected = { presetId ->
                        selectedGraph = graphGenerator.getPresetDisplayName(presetId)
                        showPresetSheet = false
                        updateGraph(graphGenerator.getGraph(presetId))
                    },
                    onDismiss = { showPresetSheet = false }
                )
            }

            if (showEditModeSheet) {
                EditModeBottomSheet(
                    selectedEditMode = selectedEditMode,
                    onEditModeSelected = { mode ->
                        selectEditMode(mode)
                        showEditModeSheet = false
                    },
                    onDismiss = { showEditModeSheet = false }
                )
            }
        }
    }
}
