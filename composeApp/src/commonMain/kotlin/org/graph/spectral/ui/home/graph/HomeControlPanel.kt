package org.graph.spectral.ui.home.graph

import org.graph.spectral.ui.home.common.AutoComputeRow
import org.graph.spectral.ui.toolUI.CommandInputRow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeControlPanel(
    command: String,
    onCommandChange: (String) -> Unit,
    onSubmitCommand: () -> Unit,
    selectedEditMode: GraphEditMode,
    node1: String,
    onNode1Change: (String) -> Unit,
    node2: String,
    onNode2Change: (String) -> Unit,
    delNode1: String,
    onDelNode1Change: (String) -> Unit,
    delNode2: String,
    onDelNode2Change: (String) -> Unit,
    delNode: String,
    onDelNodeChange: (String) -> Unit,
    onOpenEditModeSheet: () -> Unit,
    onSubmitEditOperation: () -> Unit,
    autoCompute: Boolean,
    onAutoComputeChange: (Boolean) -> Unit,
    onRunCompute: () -> Unit,
    selectedGraph: String,
    onOpenPresetSheet: () -> Unit,
    onClearGraph: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CommandInputRow(
                command = command,
                onCommandChange = onCommandChange,
                onSubmitCommand = onSubmitCommand
            )

            GraphEditOperationRow(
                selectedEditMode = selectedEditMode,
                node1 = node1,
                onNode1Change = onNode1Change,
                node2 = node2,
                onNode2Change = onNode2Change,
                delNode1 = delNode1,
                onDelNode1Change = onDelNode1Change,
                delNode2 = delNode2,
                onDelNode2Change = onDelNode2Change,
                delNode = delNode,
                onDelNodeChange = onDelNodeChange,
                onOpenEditModeSheet = onOpenEditModeSheet,
                onSubmitEditOperation = onSubmitEditOperation
            )

            AutoComputeRow(
                autoCompute = autoCompute,
                onAutoComputeChange = onAutoComputeChange,
                onRunCompute = onRunCompute
            )

            PresetGraphRow(
                selectedGraph = selectedGraph,
                onOpenPresetSheet = onOpenPresetSheet,
                onClearGraph = onClearGraph
            )
        }
    }
}
