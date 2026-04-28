package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
internal fun GraphEditOperationRow(
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
    onSubmitEditOperation: () -> Unit
) {
    val visibleInputIds = when (selectedEditMode) {
        GraphEditMode.AddEdge -> setOf("graph-add-node-1", "graph-add-node-2")
        GraphEditMode.DeleteEdge -> setOf("graph-delete-edge-node-1", "graph-delete-edge-node-2")
        GraphEditMode.DeleteNode -> setOf("graph-delete-node")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onOpenEditModeSheet,
                modifier = Modifier.width(55.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                Text(selectedEditMode.compactLabel)
            }

            when (selectedEditMode) {
                GraphEditMode.AddEdge -> {
                    NodeInput(
                        inputId = "graph-add-node-1",
                        value = node1,
                        onValueChange = onNode1Change,
                        placeholder = "节点1"
                    )
                    NodeInput(
                        inputId = "graph-add-node-2",
                        value = node2,
                        onValueChange = onNode2Change,
                        placeholder = "节点2"
                    )
                }
                GraphEditMode.DeleteEdge -> {
                    NodeInput(
                        inputId = "graph-delete-edge-node-1",
                        value = delNode1,
                        onValueChange = onDelNode1Change,
                        placeholder = "节点1"
                    )
                    NodeInput(
                        inputId = "graph-delete-edge-node-2",
                        value = delNode2,
                        onValueChange = onDelNode2Change,
                        placeholder = "节点2"
                    )
                }
                GraphEditMode.DeleteNode -> {
                    NodeInput(
                        inputId = "graph-delete-node",
                        value = delNode,
                        onValueChange = onDelNodeChange,
                        placeholder = "节点",
                        modifier = Modifier.weight(2f)
                    )
                }
            }

            Button(onClick = onSubmitEditOperation) {
                Text("确定")
            }
        }

        CustomKeyboardPanel(inputIds = visibleInputIds)
    }
}

@Composable
private fun RowScope.NodeInput(
    inputId: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier.weight(1f)
) {
    CustomKeyboardTextField(
        inputId = inputId,
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier
    )
}
