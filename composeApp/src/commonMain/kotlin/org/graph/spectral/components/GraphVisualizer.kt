package org.graph.spectral.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.graph.spectral.models.Graph
import kotlin.math.*

@Composable
fun GraphVisualizer(
    graph: Graph,
    modifier: Modifier = Modifier
        .width(300.dp)
        .height(200.dp)
) {
    val nodes = graph.nodes().toList()
    val edges = graph.edges().toList()
    
    val nodePositions by remember(graph) {
        derivedStateOf {
            layoutNodes(nodes, edges)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawEdges(edges, nodePositions)
        drawNodes(nodes, nodePositions)
    }
}

private fun layoutNodes(nodes: List<String>, edges: List<Pair<String, String>>): Map<String, Offset> {
    val positions = mutableMapOf<String, Offset>()
    val width = 300f
    val height = 200f
    val centerX = width / 2
    val centerY = height / 2
    val radius = min(centerX, centerY) - 20

    // 初始布局：圆形布局
    for ((index, node) in nodes.withIndex()) {
        val angle = 2 * PI * index / nodes.size
        val x = centerX + radius * cos(angle).toFloat()
        val y = centerY + radius * sin(angle).toFloat()
        positions[node] = Offset(x, y)
    }

    // 简单的力导向布局
    val iterations = 50
    val repulsionForce = 1000f
    val attractionForce = 0.1f
    val damping = 0.9f

    for (iteration in 0 until iterations) {
        val forces = mutableMapOf<String, Offset>()
        
        // 初始化力
        nodes.forEach { node ->
            forces[node] = Offset.Zero
        }

        // 计算排斥力
        for (i in nodes.indices) {
            for (j in i + 1 until nodes.size) {
                val node1 = nodes[i]
                val node2 = nodes[j]
                val pos1 = positions[node1]!!
                val pos2 = positions[node2]!!
                val delta = pos1 - pos2
                val distance = max(delta.getDistance(), 1f)
                val forceMagnitude = repulsionForce / (distance * distance)
                val force = delta * forceMagnitude
                
                forces[node1] = forces[node1]!! - force
                forces[node2] = forces[node2]!! + force
            }
        }

        // 计算吸引力
        edges.forEach { (node1, node2) ->
            val pos1 = positions[node1]!!
            val pos2 = positions[node2]!!
            val delta = pos2 - pos1
            val distance = delta.getDistance()
            val forceMagnitude = attractionForce * distance
            val force = delta * forceMagnitude
            
            forces[node1] = forces[node1]!! + force
            forces[node2] = forces[node2]!! - force
        }

        // 更新位置
        nodes.forEach { node ->
            val force = forces[node]!! * damping
            val newPosition = positions[node]!! + force
            // 限制在画布内
            val clampedX = max(20f, min(width - 20f, newPosition.x))
            val clampedY = max(20f, min(height - 20f, newPosition.y))
            positions[node] = Offset(clampedX, clampedY)
        }
    }

    return positions
}

private fun DrawScope.drawEdges(
    edges: List<Pair<String, String>>,
    nodePositions: Map<String, Offset>
) {
    edges.forEach { (node1, node2) ->
        val pos1 = nodePositions[node1]
        val pos2 = nodePositions[node2]
        if (pos1 != null && pos2 != null) {
            drawLine(
                color = Color.Black,
                start = pos1,
                end = pos2,
                strokeWidth = 2f
            )
        }
    }
}

private fun DrawScope.drawNodes(
    nodes: List<String>,
    nodePositions: Map<String, Offset>
) {
    nodes.forEach { node ->
        val position = nodePositions[node]
        if (position != null) {
            // 绘制节点
            drawCircle(
                color = Color(0xFF87CEEB), // SkyBlue
                center = position,
                radius = 15f
            )
        }
    }
}
