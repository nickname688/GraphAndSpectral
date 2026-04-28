package org.graph.spectral.toolUI

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.PlatformTextInputInterceptor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.awaitCancellation

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Companion.Default,
    modifier: Modifier = Modifier.Companion,
    showSoftwareKeyboard: Boolean = true,
    onFocusChange: (Boolean) -> Unit = {},
    onPointerDown: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val effectiveKeyboardOptions = if (showSoftwareKeyboard) {
        keyboardOptions
    } else {
        keyboardOptions.copy(showKeyboardOnFocus = false)
    }

    TextInputKeyboardGate(showSoftwareKeyboard = showSoftwareKeyboard) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .height(40.dp)
                .notifyPointerDown(onPointerDown)
                .onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                    if (focusState.isFocused && !showSoftwareKeyboard) {
                        keyboardController?.hide()
                    }
                }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            singleLine = true,
            keyboardOptions = effectiveKeyboardOptions,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.Companion.fillMaxSize()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
fun CustomTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Companion.Default,
    modifier: Modifier = Modifier.Companion,
    showSoftwareKeyboard: Boolean = true,
    onFocusChange: (Boolean) -> Unit = {},
    onPointerDown: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val effectiveKeyboardOptions = if (showSoftwareKeyboard) {
        keyboardOptions
    } else {
        keyboardOptions.copy(showKeyboardOnFocus = false)
    }

    LaunchedEffect(value.selection, showSoftwareKeyboard) {
        if (!showSoftwareKeyboard) {
            keyboardController?.hide()
        }
    }

    TextInputKeyboardGate(showSoftwareKeyboard = showSoftwareKeyboard) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .height(40.dp)
                .notifyPointerDown(onPointerDown)
                .onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                    if (focusState.isFocused && !showSoftwareKeyboard) {
                        keyboardController?.hide()
                    }
                }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.small
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            singleLine = true,
            keyboardOptions = effectiveKeyboardOptions,
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.Companion.fillMaxSize()) {
                    if (value.text.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

private fun Modifier.notifyPointerDown(onPointerDown: () -> Unit): Modifier {
    return pointerInput(onPointerDown) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            onPointerDown()
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun TextInputKeyboardGate(
    showSoftwareKeyboard: Boolean,
    content: @Composable () -> Unit
) {
    if (showSoftwareKeyboard) {
        content()
    } else {
        val interceptor = remember {
            PlatformTextInputInterceptor { _, _ ->
                awaitCancellation()
            }
        }
        InterceptPlatformTextInput(
            interceptor = interceptor,
            content = content
        )
    }
}
