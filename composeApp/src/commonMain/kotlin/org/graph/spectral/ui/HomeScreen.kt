package org.graph.spectral.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.graph.spectral.components.GraphVisualizer
import org.graph.spectral.computeResult
import org.graph.spectral.models.EigenCalculator
import org.graph.spectral.models.GraphGenerator
import org.graph.spectral.models.graphcore.GraphCore
import org.graph.spectral.toolUI.CustomTextField

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
    var autoCompute by remember { mutableStateOf(false) }
    var showGraphVisualizer by remember { mutableStateOf(false) }

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
                // 左侧控制面板
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 命令输入
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "指令:",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(55.dp)
                            )
                            CustomTextField(
                                value = command,
                                onValueChange = { command = it },
                                placeholder = "例如: 1-2 2-3",
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = {
                                val resultGraph = graphGenerator.getGraphByCommand(graph, command.trim())
                                if (resultGraph != null) {
                                    selectedGraph = "自定义"
                                    updateGraph(resultGraph)
                                } else {
                                    showError("输入有误")
                                }
                            }) {
                                Text("确定")
                            }
                        }

                        // 添加边
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "增加边:",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(55.dp)
                            )
                            CustomTextField(
                                value = node1,
                                onValueChange = { node1 = it },
                                placeholder = "节点1",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.weight(1f)
                            )
                            CustomTextField(
                                value = node2,
                                onValueChange = { node2 = it },
                                placeholder = "节点2",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = {
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
                            }) {
                                Text("确定")
                            }
                        }

                        // 删除边
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "删除边:", style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(55.dp)
                            )
                            CustomTextField(
                                value = delNode1,
                                onValueChange = { delNode1 = it },
                                placeholder = "节点1",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.weight(1f)
                            )
                            CustomTextField(
                                value = delNode2,
                                onValueChange = { delNode2 = it },
                                placeholder = "节点2",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = {
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
                            }) {
                                Text("确定")
                            }
                        }

                        // 删除节点
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "删除点:", style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.width(55.dp)
                            )
                            CustomTextField(
                                value = delNode,
                                onValueChange = { delNode = it },
                                placeholder = "节点",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                modifier = Modifier.weight(1f)
                            )
                            Button(onClick = {
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
                            }) {
                                Text("确定")
                            }
                        }

                        // 自动计算
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = autoCompute,
                                    onCheckedChange = { autoCompute = it }
                                )
                                Text(text = "自动计算")
                            }
                            Button(onClick = {
                                runCompute()
                            }) {
                                Text("开始计算")
                            }
                        }

                        // 预设图选择
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "预设图:", style = MaterialTheme.typography.bodyLarge)
                            var expanded by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(selectedGraph)
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    listOf(
                                        "选择预设图",
                                        "自定义",
                                        "C3/K3",
                                        "C4",
                                        "K4",
                                        "C5",
                                        "M5"
                                    ).forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                selectedGraph = option
                                                expanded = false
                                                if (option != "选择预设图" && option != "自定义") {
                                                    updateGraph(graphGenerator.getGraph(option))
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            Button(onClick = {
                                graph = GraphCore()
                                selectedGraph = "选择预设图"
                                result = ""
                                matrixResult = ""
                            }) {
                                Text("清空")
                            }
                        }
                    }
                }
            }

            item {
                if (showGraphVisualizer) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "图可视化",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Button(onClick = { showGraphVisualizer = false }) {
                                    Text("关闭")
                                }
                            }
                            GraphVisualizer(
                                graph = graph,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = { showGraphVisualizer = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("显示图可视化")
                    }
                }
            }

            if (result.isNotEmpty()) {
                item {
                    // 计算结果
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "计算结果",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(result)
                        }
                    }

                }
            }

            if (matrixResult.isNotEmpty()) {
                item {
                    // 邻接矩阵
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "邻接矩阵",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(matrixResult)
                        }
                    }
                }
            }
        }
    }
}
