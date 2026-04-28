package org.graph.spectral.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.graph.spectral.toolUI.CustomTextField

@Composable
internal fun CommandInputRow(
    command: String,
    onCommandChange: (String) -> Unit,
    onSubmitCommand: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "指令:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(55.dp)
        )
        CustomTextField(
            value = command,
            onValueChange = onCommandChange,
            placeholder = "例如: 1-2 2-3",
            modifier = Modifier.weight(1f)
        )
        Button(onClick = onSubmitCommand) {
            Text("确定")
        }
    }
}
