@file:Suppress("NOTHING_TO_INLINE", "UnusedReceiverParameter", "unused")
@file:JvmName("Extensions")

package ru.morozovit.compat

import androidx.compose.material.icons.Icons
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayoutBaseScope
import androidx.constraintlayout.compose.HorizontalAnchorable
import androidx.constraintlayout.compose.VerticalAnchorable
import dncryptor101multiplatform.composeapp.generated.resources.Res
import dncryptor101multiplatform.composeapp.generated.resources.content_paste_outlined
import dncryptor101multiplatform.composeapp.generated.resources.encrypted_filled
import dncryptor101multiplatform.composeapp.generated.resources.encrypted_off_filled
import dncryptor101multiplatform.composeapp.generated.resources.encrypted_off_outlined
import dncryptor101multiplatform.composeapp.generated.resources.encrypted_outlined
import dncryptor101multiplatform.composeapp.generated.resources.stop_circle_filled
import dncryptor101multiplatform.composeapp.generated.resources.stop_circle_outlined
import org.jetbrains.compose.resources.vectorResource
import kotlin.jvm.JvmName

val ConstrainScope.left get() = absoluteLeft
val ConstrainScope.right get() = absoluteRight

val ConstrainedLayoutReference.left get() = absoluteLeft
val ConstrainedLayoutReference.right get() = absoluteRight

inline infix fun HorizontalAnchorable.link(
    anchor: ConstraintLayoutBaseScope.HorizontalAnchor
) = linkTo(anchor)

inline infix fun VerticalAnchorable.link(
    anchor: ConstraintLayoutBaseScope.VerticalAnchor
) = linkTo(anchor)


expect fun Modifier.clearFocusOnKeyboardDismiss(): Modifier

operator fun Modifier.plus(other: Modifier) = then(other)

var ClipboardManager.text
    get() = getText()
    set(value) = setText(value!!)

@Composable
expect fun removeNavScrim(): Result<Unit>

expect val dynamicTheme: Boolean

@Composable
expect fun getDynamicColorScheme(darkTheme: Boolean): ColorScheme?

val Icons.Filled.Encrypted
    @Composable get() = vectorResource(Res.drawable.encrypted_filled)
val Icons.Outlined.Encrypted
    @Composable get() = vectorResource(Res.drawable.encrypted_outlined)

val Icons.Filled.EncryptedOff
    @Composable get() = vectorResource(Res.drawable.encrypted_off_filled)
val Icons.Outlined.EncryptedOff
    @Composable get() = vectorResource(Res.drawable.encrypted_off_outlined)

val Icons.Filled.StopCircle
    @Composable get() = vectorResource(Res.drawable.stop_circle_filled)
val Icons.Outlined.StopCircle
    @Composable get() = vectorResource(Res.drawable.stop_circle_outlined)

val Icons.Outlined.ContentPaste
    @Composable get() = vectorResource(Res.drawable.content_paste_outlined)

interface SupportClipboardManager {
    suspend fun setText(string: String)
    suspend fun getText(): String?
    suspend fun hasText(): Boolean = getText()?.isNotEmpty() ?: false

    fun setTextListener(listener: (String) -> Unit)
}

expect val LocalSupportClipboardManager: ProvidableCompositionLocal<SupportClipboardManager>

expect val supportClipboardManagerImpl: SupportClipboardManager @Composable get

inline fun ClipboardManager.toSupport() = object : SupportClipboardManager {
    private var listener: ((String) -> Unit)? = null

    override suspend fun setText(string: String) {
        this@toSupport.setText(string.toAnnotatedString())
        listener?.invoke(string)
    }
    override suspend fun getText() = this@toSupport.getText()?.toString() ?: ""

    override fun setTextListener(listener: (String) -> Unit) {
        this.listener = listener
    }
}

fun String.toAnnotatedString() = AnnotatedString(this)