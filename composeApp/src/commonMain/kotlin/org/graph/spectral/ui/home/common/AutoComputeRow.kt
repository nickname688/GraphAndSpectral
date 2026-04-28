package org.graph.spectral.ui.home.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AutoComputeRow(
    autoCompute: Boolean,
    onAutoComputeChange: (Boolean) -> Unit,
    onRunCompute: () -> Unit
) {
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
                onCheckedChange = onAutoComputeChange
            )
            Text(text = "自动计算")
        }
        Button(onClick = onRunCompute) {
            Text("开始计算")
        }
    }
}
