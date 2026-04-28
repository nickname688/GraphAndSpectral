package org.graph.spectral.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.graph.spectral.toolUI.ButtonCard

@Composable
fun SettingsScreen(paddingValues: PaddingValues) {
    var showGraphCommandHelp by remember { mutableStateOf(false) }
    var showHypergraphCommandHelp by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                ButtonCard(
                    modifier = Modifier.fillMaxWidth(),
                    name = "计算设置",
                    onNavigateToEdit = { /* 导航到计算设置页面 */ }
                )
            }

            item {
                ButtonCard(
                    modifier = Modifier.fillMaxWidth(),
                    name = "可视化设置",
                    onNavigateToEdit = { /* 导航到可视化设置页面 */ }
                )
            }

            item {
                ButtonCard(
                    modifier = Modifier.fillMaxWidth(),
                    name = "普通图指令教程",
                    onNavigateToEdit = { showGraphCommandHelp = true }
                )
            }

            item {
                ButtonCard(
                    modifier = Modifier.fillMaxWidth(),
                    name = "超图指令教程",
                    onNavigateToEdit = { showHypergraphCommandHelp = true }
                )
            }

            item {
                ButtonCard(
                    modifier = Modifier.fillMaxWidth(),
                    name = "关于",
                    onNavigateToEdit = { /* 导航到关于页面 */ }
                )
            }
        }
    }

    if (showGraphCommandHelp) {
        CommandHelpDialog(
            title = "普通图指令教程",
            text = graphCommandHelpText,
            onDismiss = { showGraphCommandHelp = false }
        )
    }

    if (showHypergraphCommandHelp) {
        CommandHelpDialog(
            title = "超图指令教程",
            text = hypergraphCommandHelpText,
            onDismiss = { showHypergraphCommandHelp = false }
        )
    }
}

@Composable
private fun CommandHelpDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.verticalScroll(rememberScrollState())
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("知道了")
            }
        }
    )
}

private val graphCommandHelpText = """
普通图指令用于快速添加边。

单条边:
1-2

多条边:
1-2 2-3 3-4

旧式连续写法:
12 表示 (1,2)
1234 表示 (1,2), (3,4)

完全二部连接:
(1-3)(4-5) 表示把 1,2,3 分别连接到 4,5。

分隔符:
空格、逗号、中文逗号、分号都可以分隔指令。
""".trimIndent()

private val hypergraphCommandHelpText = """
超图指令用于快速添加k均匀超边。第一条超边或指令会决定k，后续输入必须同阶。

显式超边:
(1,2,3); (1,3,4)

完整k均匀超图:
K5^3 或 K(5,3)
表示顶点 1..5 上所有3元超边。

星形k均匀超图:
S6^3 或 S(6,3)
表示顶点 1..6 上所有包含中心点1的3元超边。

松路径:
P4^3 或 P(4,3)
表示4条3元超边，形如 (1,2,3), (3,4,5), (5,6,7), (7,8,9)。

混合输入:
K4^3; (2,3,5)
只要所有超边阶数相同即可。

分隔符:
空格、逗号、中文逗号、分号都可以分隔指令；括号内用逗号、中文逗号或空格分隔节点。
""".trimIndent()
