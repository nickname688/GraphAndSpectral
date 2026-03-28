package org.graph.spectral

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.graph.spectral.components.GraphVisualizer
import org.graph.spectral.models.EigenCalculator
import org.graph.spectral.models.Graph
import org.graph.spectral.models.GraphGenerator

@Composable
@Preview
fun App() {
    MaterialTheme {
        var graph by remember { mutableStateOf(Graph()) }
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
        
        val graphGenerator = GraphGenerator()
        val eigenCalculator = EigenCalculator()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "谱半径计算器",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

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
                        Text(text = "指令:", style = MaterialTheme.typography.bodyLarge)
                        OutlinedTextField(
                            value = command,
                            onValueChange = { command = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("例如: 1-2 2-3") }
                        )
                        Button(onClick = {
                            val resultGraph = graphGenerator.getGraphByCommand(graph, command)
                            if (resultGraph != null) {
                                graph = resultGraph
                                if (autoCompute) {
                                    computeResult(graph, eigenCalculator) { r, m ->
                                        result = r
                                        matrixResult = m
                                    }
                                }
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
                        Text(text = "增加边:", style = MaterialTheme.typography.bodyLarge)
                        OutlinedTextField(
                            value = node1,
                            onValueChange = { node1 = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            placeholder = { Text("节点1") }
                        )
                        OutlinedTextField(
                            value = node2,
                            onValueChange = { node2 = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            placeholder = { Text("节点2") }
                        )
                        Button(onClick = {
                            if (node1.isNotEmpty() && node2.isNotEmpty() && node1 != node2) {
                                graph.addEdge(node1, node2)
                                node1 = ""
                                node2 = ""
                                if (autoCompute) {
                                    computeResult(graph, eigenCalculator) { r, m ->
                                        result = r
                                        matrixResult = m
                                    }
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
                        Text(text = "删除边:", style = MaterialTheme.typography.bodyLarge)
                        OutlinedTextField(
                            value = delNode1,
                            onValueChange = { delNode1 = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            placeholder = { Text("节点1") }
                        )
                        OutlinedTextField(
                            value = delNode2,
                            onValueChange = { delNode2 = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            placeholder = { Text("节点2") }
                        )
                        Button(onClick = {
                            if (delNode1.isNotEmpty() && delNode2.isNotEmpty()) {
                                graph.removeEdge(delNode1, delNode2)
                                delNode1 = ""
                                delNode2 = ""
                                if (autoCompute) {
                                    computeResult(graph, eigenCalculator) { r, m ->
                                        result = r
                                        matrixResult = m
                                    }
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
                        Text(text = "删除点:", style = MaterialTheme.typography.bodyLarge)
                        OutlinedTextField(
                            value = delNode,
                            onValueChange = { delNode = it },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            placeholder = { Text("节点") }
                        )
                        Button(onClick = {
                            if (delNode.isNotEmpty()) {
                                graph.removeNode(delNode)
                                delNode = ""
                                if (autoCompute) {
                                    computeResult(graph, eigenCalculator) { r, m ->
                                        result = r
                                        matrixResult = m
                                    }
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
                            computeResult(graph, eigenCalculator) { r, m ->
                                result = r
                                matrixResult = m
                            }
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
                                listOf("选择预设图", "自定义", "C3/K3", "C4", "K4", "C5", "M5").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedGraph = option
                                            expanded = false
                                            if (option != "选择预设图" && option != "自定义") {
                                                graph = graphGenerator.getGraph(option)
                                                if (autoCompute) {
                                                    computeResult(graph, eigenCalculator) { r, m ->
                                                        result = r
                                                        matrixResult = m
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        Button(onClick = {
                            graph.clear()
                            result = ""
                            matrixResult = ""
                        }) {
                            Text("清空")
                        }
                    }
                }
            }

            // 右侧结果显示
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 计算结果
                Card(
                    modifier = Modifier.weight(1f)
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

                // 邻接矩阵
                Card(
                    modifier = Modifier.weight(1f)
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

            // 图可视化
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "图可视化",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GraphVisualizer(
                        graph = graph,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            }
        }
    }
}

private fun computeResult(graph: Graph, calculator: EigenCalculator, callback: (String, String) -> Unit) {
    val result = calculator.calculate(graph)
    if (result != null) {
        val resultText = calculator.formatResult(result)
        val matrixText = calculator.formatAdjacencyMatrix(graph.adjacencyMatrix())
        callback(resultText, matrixText)
    } else {
        callback("请先构建图", "")
    }
}
