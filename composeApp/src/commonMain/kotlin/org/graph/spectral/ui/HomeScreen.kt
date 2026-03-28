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
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import org.graph.spectral.models.Graph
import org.graph.spectral.models.GraphGenerator
import org.graph.spectral.toolUI.CustomTextField

@Composable
fun HomeScreen(paddingValues: PaddingValues) {
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
    var showGraphVisualizer by remember { mutableStateOf(false) }

    val graphGenerator = GraphGenerator()
    val eigenCalculator = EigenCalculator()

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
                                                    graph = graphGenerator.getGraph(option)
                                                    if (autoCompute) {
                                                        computeResult(
                                                            graph,
                                                            eigenCalculator
                                                        ) { r, m ->
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

            if (!showGraphVisualizer) {
                item {
                    // 图可视化按钮
                    Button(
                        onClick = { showGraphVisualizer = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("显示图可视化")
                    }
                }
            }
        }

        // 图可视化窗口
        if (showGraphVisualizer) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                HorizontalDivider(
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // 标题栏
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "图可视化",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(
                                onClick = { showGraphVisualizer = false }
                            ) {
                                Text("关闭")
                            }
                        }

                        // 图可视化
                        GraphVisualizer(
                            graph = graph,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}