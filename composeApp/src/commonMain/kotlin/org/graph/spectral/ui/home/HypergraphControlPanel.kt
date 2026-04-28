package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.graph.spectral.toolUI.CustomTextField

@Composable
internal fun HypergraphControlPanel(
    hyperedgeCommand: String,
    onHyperedgeCommandChange: (String) -> Unit,
    onAddHyperedges: () -> Unit,
    deleteHyperedgeCommand: String,
    onDeleteHyperedgeCommandChange: (String) -> Unit,
    onDeleteHyperedges: () -> Unit,
    autoCompute: Boolean,
    onAutoComputeChange: (Boolean) -> Unit,
    onRunCompute: () -> Unit,
    onClearHypergraph: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HyperedgeInputRow(
                label = "超边:",
                value = hyperedgeCommand,
                onValueChange = onHyperedgeCommandChange,
                placeholder = "(1,2,3); (1,3,4)",
                actionText = "加入",
                onAction = onAddHyperedges
            )

            HyperedgeInputRow(
                label = "删除:",
                value = deleteHyperedgeCommand,
                onValueChange = onDeleteHyperedgeCommandChange,
                placeholder = "(1,2,3)",
                actionText = "删除",
                onAction = onDeleteHyperedges
            )

            AutoComputeRow(
                autoCompute = autoCompute,
                onAutoComputeChange = onAutoComputeChange,
                onRunCompute = onRunCompute
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onClearHypergraph) {
                    Text("清空")
                }
            }
        }
    }
}

@Composable
private fun HyperedgeInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    actionText: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(55.dp)
        )
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            modifier = Modifier.weight(1f)
        )
        Button(onClick = onAction) {
            Text(actionText)
        }
    }
}
