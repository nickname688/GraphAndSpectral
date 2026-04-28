package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun PresetGraphRow(
    selectedGraph: String,
    onOpenPresetSheet: () -> Unit,
    onClearGraph: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "预设图:", style = MaterialTheme.typography.bodyLarge)
        OutlinedButton(
            onClick = onOpenPresetSheet,
            modifier = Modifier.weight(1f)
        ) {
            Text(selectedGraph)
        }
        Button(onClick = onClearGraph) {
            Text("清空")
        }
    }
}
