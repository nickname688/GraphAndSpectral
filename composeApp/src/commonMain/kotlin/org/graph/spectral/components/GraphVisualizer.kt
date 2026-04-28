package org.graph.spectral.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import org.graph.spectral.models.graphcore.GraphCore
import org.graph.spectral.models.graphcore.GraphEdge
import org.graph.spectral.models.graphcore.GraphNodeOrdering
import org.graph.spectral.models.graphcore.connectedComponents

@Composable
fun GraphVisualizer(
    graph: GraphCore,
    modifier: Modifier = Modifier.height(320.dp)
) {
    val nodes = graph.nodesSorted()
    val edges = graph.edges().sortedWith(edgeComparator)
    val density = LocalDensity.current
    val nodeDiameter = 40.dp
    val nodeRadiusPx = with(density) { (nodeDiameter / 2).toPx() }

    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val edgeColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.72f)
    val nodeColor = MaterialTheme.colorScheme.primaryContainer
    val nodeBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.72f)
    val nodeTextColor = MaterialTheme.colorScheme.onPrimaryContainer
    val emptyTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val positions = remember(graph, widthPx.roundToInt(), heightPx.roundToInt()) {
            layoutGraphNodes(graph, widthPx, heightPx, nodeRadiusPx)
        }

        if (nodes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无图形",
                    color = emptyTextColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            return@BoxWithConstraints
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawEdges(edges, positions, edgeColor, nodeRadiusPx)
        }

        nodes.forEach { node ->
            val position = positions[node] ?: return@forEach
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (position.x - nodeRadiusPx).roundToInt(),
                            y = (position.y - nodeRadiusPx).roundToInt()
                        )
                    }
                    .size(nodeDiameter)
                    .clip(CircleShape)
                    .background(nodeColor)
                    .border(1.dp, nodeBorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = node,
                    color = nodeTextColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = nodeLabelSize(node),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

internal fun layoutGraphNodes(
    graph: GraphCore,
    width: Float,
    height: Float,
    nodeRadius: Float
): Map<String, Offset> {
    if (width <= 0f || height <= 0f || graph.order() == 0) return emptyMap()

    val positions = mutableMapOf<String, Offset>()
    val components = graph.connectedComponents()
        .map { component -> component.sortedWith(GraphNodeOrdering) }
        .sortedWith(compareBy<List<String>> { it.firstOrNull() ?: "" })

    val columnCount = ceil(sqrt(components.size.toDouble())).toInt().coerceAtLeast(1)
    val rowCount = ceil(components.size / columnCount.toDouble()).toInt().coerceAtLeast(1)
    val cellWidth = width / columnCount
    val cellHeight = height / rowCount

    components.forEachIndexed { index, component ->
        val column = index % columnCount
        val row = index / columnCount
        val bounds = LayoutBounds(
            left = column * cellWidth,
            top = row * cellHeight,
            right = (column + 1) * cellWidth,
            bottom = (row + 1) * cellHeight
        ).inset(max(nodeRadius * 1.6f, 18f))

        val componentEdges = graph.edges()
            .filter { edge -> edge.first in component && edge.second in component }
        val componentPositions = if (component.size > 8) {
            layoutForceDirected(component, componentEdges, bounds, nodeRadius)
        } else {
            layoutCircular(component, bounds)
        }
        positions.putAll(componentPositions)
    }

    return positions
}

private fun layoutCircular(
    nodes: List<String>,
    bounds: LayoutBounds
): Map<String, Offset> {
    if (nodes.isEmpty()) return emptyMap()
    if (nodes.size == 1) return mapOf(nodes.first() to bounds.center)

    val radius = (min(bounds.width, bounds.height) / 2f).coerceAtLeast(1f)
    val center = bounds.center
    return nodes.mapIndexed { index, node ->
        val angle = -PI / 2.0 + 2.0 * PI * index / nodes.size
        node to Offset(
            x = center.x + radius * cos(angle).toFloat(),
            y = center.y + radius * sin(angle).toFloat()
        )
    }.toMap()
}

private fun layoutForceDirected(
    nodes: List<String>,
    edges: List<GraphEdge>,
    bounds: LayoutBounds,
    nodeRadius: Float
): Map<String, Offset> {
    val positions = layoutCircular(nodes, bounds).toMutableMap()
    val area = (bounds.width * bounds.height).coerceAtLeast(1f)
    val idealDistance = sqrt(area / nodes.size).coerceAtLeast(nodeRadius * 2.5f)
    var temperature = min(bounds.width, bounds.height) / 8f

    repeat(160) {
        val displacements = nodes.associateWith { Offset.Zero }.toMutableMap()

        for (i in nodes.indices) {
            for (j in i + 1 until nodes.size) {
                val a = nodes[i]
                val b = nodes[j]
                val delta = positions.getValue(a) - positions.getValue(b)
                val distance = delta.getDistance().coerceAtLeast(1f)
                val force = (idealDistance * idealDistance) / distance
                val offset = delta / distance * force
                displacements[a] = displacements.getValue(a) + offset
                displacements[b] = displacements.getValue(b) - offset
            }
        }

        edges.forEach { edge ->
            val delta = positions.getValue(edge.first) - positions.getValue(edge.second)
            val distance = delta.getDistance().coerceAtLeast(1f)
            val force = (distance * distance) / idealDistance
            val offset = delta / distance * force
            displacements[edge.first] = displacements.getValue(edge.first) - offset
            displacements[edge.second] = displacements.getValue(edge.second) + offset
        }

        nodes.forEach { node ->
            val displacement = displacements.getValue(node)
            val length = displacement.getDistance().coerceAtLeast(1f)
            val step = displacement / length * min(length, temperature)
            positions[node] = bounds.clamp(positions.getValue(node) + step)
        }

        temperature *= 0.94f
    }

    return positions
}

private fun DrawScope.drawEdges(
    edges: List<GraphEdge>,
    nodePositions: Map<String, Offset>,
    color: Color,
    nodeRadius: Float
) {
    edges.forEach { edge ->
        val start = nodePositions[edge.first]
        val end = nodePositions[edge.second]
        if (start != null && end != null) {
            val delta = end - start
            val distance = delta.getDistance()
            val direction = if (distance > 0f) delta / distance else Offset.Zero
            drawLine(
                color = color,
                start = start + direction * nodeRadius,
                end = end - direction * nodeRadius,
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

private fun nodeLabelSize(label: String) = when {
    label.length <= 2 -> 13.sp
    label.length <= 4 -> 11.sp
    else -> 9.sp
}

private val edgeComparator = Comparator<GraphEdge> { left, right ->
    val firstCompare = GraphNodeOrdering.compare(left.first, right.first)
    if (firstCompare != 0) {
        firstCompare
    } else {
        GraphNodeOrdering.compare(left.second, right.second)
    }
}

private data class LayoutBounds(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    val width: Float get() = (right - left).coerceAtLeast(1f)
    val height: Float get() = (bottom - top).coerceAtLeast(1f)
    val center: Offset get() = Offset((left + right) / 2f, (top + bottom) / 2f)

    fun inset(value: Float): LayoutBounds {
        val insetX = min(value, width / 2f)
        val insetY = min(value, height / 2f)
        return LayoutBounds(
            left = left + insetX,
            top = top + insetY,
            right = right - insetX,
            bottom = bottom - insetY
        )
    }

    fun clamp(offset: Offset): Offset {
        return Offset(
            x = offset.x.coerceIn(left, right),
            y = offset.y.coerceIn(top, bottom)
        )
    }
}
