package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ComputeModeRow(
    selectedMode: SpectralComputeMode,
    onModeSelected: (SpectralComputeMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "模式:",
            style = MaterialTheme.typography.bodyLarge
        )
        SpectralComputeMode.entries.forEach { mode ->
            FilterChip(
                selected = selectedMode == mode,
                onClick = { onModeSelected(mode) },
                label = { Text(mode.label) }
            )
        }
    }
}
