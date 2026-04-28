package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    selectedHypergraph: String,
    onOpenPresetSheet: () -> Unit,
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
                inputId = "hypergraph-add-command",
                label = "超边:",
                value = hyperedgeCommand,
                onValueChange = onHyperedgeCommandChange,
                placeholder = "K5^3; S6^3; (1,2,3)",
                actionText = "加入",
                onAction = onAddHyperedges
            )

            HyperedgeInputRow(
                inputId = "hypergraph-delete-command",
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "预设超图:", style = MaterialTheme.typography.bodyLarge)
                OutlinedButton(
                    onClick = onOpenPresetSheet,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(selectedHypergraph)
                }
                Button(onClick = onClearHypergraph) {
                    Text("清空")
                }
            }
        }
    }
}

@Composable
private fun HyperedgeInputRow(
    inputId: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    actionText: String,
    onAction: () -> Unit
) {
    CommandKeyboardInput(
        inputId = inputId,
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        actionText = actionText,
        onAction = onAction
    )
}
