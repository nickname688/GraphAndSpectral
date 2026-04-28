package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.graph.spectral.components.GraphVisualizer
import org.graph.spectral.models.graphcore.GraphCore

@Composable
internal fun GraphVisualizerSection(
    graph: GraphCore,
    showGraphVisualizer: Boolean,
    onShowGraphVisualizer: () -> Unit,
    onHideGraphVisualizer: () -> Unit
) {
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
                    Button(onClick = onHideGraphVisualizer) {
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
            onClick = onShowGraphVisualizer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("显示图可视化")
        }
    }
}

@Composable
internal fun ResultTextCard(
    title: String,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text)
        }
    }
}
