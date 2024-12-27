package ru.morozovit.compat

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager

actual fun Modifier.clearFocusOnKeyboardDismiss() = this
@Composable actual fun removeNavScrim() = Result.success(Unit)

actual val dynamicTheme get() = false

@Composable
actual fun getDynamicColorScheme(darkTheme: Boolean): ColorScheme? = null
actual val LocalSupportClipboardManager: ProvidableCompositionLocal<SupportClipboardManager> = compositionLocalOf {
    throw IllegalStateException("This CompositionLocal hasn't been provided.")
}
actual val supportClipboardManagerImpl: SupportClipboardManager
    @Composable get() = LocalClipboardManager.current.toSupport()