package org.graph.spectral.ui.toolUI

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.graph.spectral.toolUI.CustomTextField

class CustomKeyboardHostState {
    var openInputId by mutableStateOf<String?>(null)
        private set

    private val handlers = mutableMapOf<String, CustomKeyboardHandler>()

    fun open(inputId: String) {
        openInputId = inputId
    }

    fun close(inputId: String? = null) {
        if (inputId == null || openInputId == inputId) {
            openInputId = null
        }
    }

    fun register(inputId: String, handler: CustomKeyboardHandler) {
        handlers[inputId] = handler
    }

    fun unregister(inputId: String) {
        handlers.remove(inputId)
        close(inputId)
    }

    fun handlerFor(inputId: String?): CustomKeyboardHandler? {
        return inputId?.let(handlers::get)
    }
}

data class CustomKeyboardHandler(
    val insert: (String) -> Unit,
    val backspace: () -> Unit,
    val clear: () -> Unit
)

private val LocalCustomKeyboardHostState = staticCompositionLocalOf<CustomKeyboardHostState?> {
    null
}

@Composable
fun CustomKeyboardHost(content: @Composable () -> Unit) {
    val hostState = remember { CustomKeyboardHostState() }
    CompositionLocalProvider(
        LocalCustomKeyboardHostState provides hostState,
        content = content
    )
}

@Composable
fun CommandKeyboardInput(
    inputId: String,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    actionText: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hostState = currentKeyboardHostState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
            CustomKeyboardTextField(
                inputId = inputId,
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    hostState.close(inputId)
                    onAction()
                }
            ) {
                Text(actionText)
            }
        }

        CustomKeyboardPanel(
            inputIds = setOf(inputId)
        )
    }
}

@Composable
fun CustomKeyboardTextField(
    inputId: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val hostState = currentKeyboardHostState()
    var fieldValue by remember(inputId) {
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }

    LaunchedEffect(value) {
        if (value != fieldValue.text) {
            fieldValue = TextFieldValue(value, selection = TextRange(value.length))
        }
    }

    fun updateTextField(nextValue: TextFieldValue) {
        fieldValue = nextValue
        onValueChange(nextValue.text)
    }

    fun replaceSelection(token: String) {
        val selectionStart = minOf(fieldValue.selection.start, fieldValue.selection.end)
        val selectionEnd = maxOf(fieldValue.selection.start, fieldValue.selection.end)
        val nextText = fieldValue.text.replaceRange(selectionStart, selectionEnd, token)
        val cursor = selectionStart + token.length
        updateTextField(TextFieldValue(nextText, selection = TextRange(cursor)))
    }

    fun backspace() {
        val selectionStart = minOf(fieldValue.selection.start, fieldValue.selection.end)
        val selectionEnd = maxOf(fieldValue.selection.start, fieldValue.selection.end)
        when {
            selectionStart != selectionEnd -> {
                val nextText = fieldValue.text.removeRange(selectionStart, selectionEnd)
                updateTextField(TextFieldValue(nextText, selection = TextRange(selectionStart)))
            }
            selectionStart > 0 -> {
                val nextText = fieldValue.text.removeRange(selectionStart - 1, selectionStart)
                updateTextField(TextFieldValue(nextText, selection = TextRange(selectionStart - 1)))
            }
        }
    }

    DisposableEffect(inputId) {
        hostState.register(
            inputId = inputId,
            handler = CustomKeyboardHandler(
                insert = ::replaceSelection,
                backspace = ::backspace,
                clear = { updateTextField(TextFieldValue("")) }
            )
        )
        onDispose { hostState.unregister(inputId) }
    }

    CustomTextField(
        value = fieldValue,
        onValueChange = ::updateTextField,
        placeholder = placeholder,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        showSoftwareKeyboard = false,
        onFocusChange = { focused ->
            if (focused) {
                hostState.open(inputId)
            }
        },
        onPointerDown = { hostState.open(inputId) }
    )
}

@Composable
fun CustomKeyboardPanel(
    inputIds: Set<String>,
    modifier: Modifier = Modifier
) {
    val hostState = currentKeyboardHostState()
    val openInputId = hostState.openInputId
    val handler = hostState.handlerFor(openInputId)

    if (openInputId in inputIds && handler != null) {
        CommandKeyboard(
            modifier = modifier,
            onInsert = handler.insert,
            onBackspace = handler.backspace,
            onClear = handler.clear,
            onDismiss = { hostState.close(openInputId) }
        )
    }
}

@Composable
private fun currentKeyboardHostState(): CustomKeyboardHostState {
    val provided = LocalCustomKeyboardHostState.current
    val fallback = remember { CustomKeyboardHostState() }
    return provided ?: fallback
}

@Composable
private fun CommandKeyboard(
    onInsert: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KeyRow(listOf("1", "2", "3", "(", ")"), onInsert)
        KeyRow(listOf("4", "5", "6", ",", ";"), onInsert)
        KeyRow(listOf("7", "8", "9", "-", "^"), onInsert)
        KeyRow(listOf("K", "S", "P", "0"), onInsert) {
            CommandKey(text = "空格", onClick = { onInsert(" ") })
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CommandKey(text = "退格", onClick = onBackspace)
            CommandKey(text = "清空", onClick = onClear)
            CommandKey(text = "收起", onClick = onDismiss)
        }
    }
}

@Composable
private fun KeyRow(
    keys: List<String>,
    onInsert: (String) -> Unit,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEach { key ->
            CommandKey(text = key, onClick = { onInsert(key) })
        }
        trailing()
    }
}

@Composable
private fun RowScope.CommandKey(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(PaddingValues(horizontal = 4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
