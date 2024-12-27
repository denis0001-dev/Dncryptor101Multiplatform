package ru.morozovit.compat

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual fun Modifier.clearFocusOnKeyboardDismiss() = this
@Composable actual fun removeNavScrim() = Result.success(Unit)

actual val dynamicTheme get() = false

@Composable
actual fun getDynamicColorScheme(darkTheme: Boolean): ColorScheme? = null