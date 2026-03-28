package org.graph.spectral

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.graph.spectral.models.EigenCalculator
import org.graph.spectral.models.Graph
import org.graph.spectral.ui.HomeScreen
import org.graph.spectral.ui.SettingsScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = remember { mutableStateOf(0) }
        
        Scaffold(
            topBar = {
                // 根据当前路由动态显示标题
                val title = when(navController.value) {
                    0 -> "谱半径计算器"
                    1 -> "设置"
                    else -> ""
                }
                MyTopAppBar(title)
            },
            bottomBar = {
                BottomAppBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Home, contentDescription = "主页") },
                        label = { Text("主页") },
                        selected = navController.value == 0,
                        onClick = { navController.value = 0 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "设置") },
                        label = { Text("设置") },
                        selected = navController.value == 1,
                        onClick = { navController.value = 1 }
                    )
                }
            }
        ) {paddingValues ->

            when (navController.value) {
                0 -> HomeScreen(paddingValues)
                1 -> SettingsScreen(paddingValues)
            }
        }
    }
}

fun computeResult(graph: Graph, calculator: EigenCalculator, callback: (String, String) -> Unit) {
    val result = calculator.calculate(graph)
    if (result != null) {
        val resultText = calculator.formatResult(result)
        val matrixText = calculator.formatAdjacencyMatrix(graph.adjacencyMatrix())
        callback(resultText, matrixText)
    } else {
        callback("请先构建图", "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(tabText: String) {
    TopAppBar(
        title = {
            Text(
                text = tabText,
                style = TextStyle(
                    color = Color.White,      // 标题文字颜色
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,   // ← 背景色（Material 3 中叫 containerColor）
            titleContentColor = Color.White       // ← 标题内容颜色
        ),
        modifier = Modifier
    )
}