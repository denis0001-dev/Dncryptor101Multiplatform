@file:Suppress("FunctionName")

package ru.morozovit.compat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.await
import kotlin.js.Promise


private fun clipboard_readText(): Promise<JsString> = js(
    "navigator.clipboard.readText()"
)

private fun clipboard_writeText(@Suppress("UNUSED_PARAMETER") text: String): Promise<JsAny> = js(
    "navigator.clipboard.writeText(text)"
)

private object JsSupportClipboardManagerImpl: SupportClipboardManager {
    private var listener: ((String) -> Unit)? = null

    override suspend fun getText() = try {
        clipboard_readText().await<JsString>().toString()
    } catch (e: JsException) {
        null
    }

    override suspend fun setText(string: String) {
        clipboard_writeText(string).await<JsAny>()
        listener?.invoke(string)
    }

    override fun setTextListener(listener: (String) -> Unit) {
        this.listener = listener
    }
}



actual val LocalSupportClipboardManager = compositionLocalOf<SupportClipboardManager> {
    JsSupportClipboardManagerImpl
}

actual val supportClipboardManagerImpl: SupportClipboardManager
    @Composable get() = JsSupportClipboardManagerImpl