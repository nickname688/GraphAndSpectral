package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import org.graph.spectral.toolUI.CustomTextField

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
                NodeInput(value = node1, onValueChange = onNode1Change, placeholder = "节点1")
                NodeInput(value = node2, onValueChange = onNode2Change, placeholder = "节点2")
            }
            GraphEditMode.DeleteEdge -> {
                NodeInput(value = delNode1, onValueChange = onDelNode1Change, placeholder = "节点1")
                NodeInput(value = delNode2, onValueChange = onDelNode2Change, placeholder = "节点2")
            }
            GraphEditMode.DeleteNode -> {
                NodeInput(
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
}

@Composable
private fun RowScope.NodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier.weight(1f)
) {
    CustomTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = modifier
    )
}
