package org.graph.spectral.toolUI

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

/**
 * 删除确认卡片
 */
@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    closeDialog: () -> Unit,
    name: String,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("确认删除") },
        text = { Text(name) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            TextButton(onClick = closeDialog) {
                Text("取消")
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    )
}