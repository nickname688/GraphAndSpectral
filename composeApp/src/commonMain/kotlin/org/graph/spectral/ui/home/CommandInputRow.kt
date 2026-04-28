package org.graph.spectral.ui.home

import androidx.compose.runtime.Composable

@Composable
internal fun CommandInputRow(
    command: String,
    onCommandChange: (String) -> Unit,
    onSubmitCommand: () -> Unit
) {
    CommandKeyboardInput(
        inputId = "graph-command",
        label = "指令:",
        value = command,
        onValueChange = onCommandChange,
        placeholder = "例如: 1-2 2-3",
        actionText = "确定",
        onAction = onSubmitCommand
    )
}
