package ru.morozovit.dncryptor101.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import ru.morozovit.compat.dynamicTheme
import ru.morozovit.compat.getDynamicColorScheme

interface WindowInsetsScope {
    val systemBarInsets: WindowInsets
    val isWindowInsetsConsumed: Boolean

    val topInset: Int
    val bottomInset: Int
    val leftInset: Int
    val rightInset: Int
}

@Composable
expect inline fun ProvideContextMenuRepresentation(darkTheme: Boolean, noinline content: @Composable () -> Unit)

@Composable
inline fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    consumeWindowInsets: Boolean = false,
    crossinline content: @Composable WindowInsetsScope.() -> Unit
) {
    val colorScheme = when {
        dynamicTheme -> {
            getDynamicColorScheme(darkTheme)!!
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        val insets = WindowInsets(
            WindowInsets.systemBars.getLeft(
                LocalDensity.current,
                LocalLayoutDirection.current
            ),
            WindowInsets.systemBars.getTop(LocalDensity.current),
            WindowInsets.systemBars.getRight(
                LocalDensity.current,
                LocalLayoutDirection.current
            ),
            WindowInsets.systemBars.getBottom(LocalDensity.current)
        )

        ProvideContextMenuRepresentation(darkTheme) {
            Surface(
                contentColor = colorScheme.onSurface,
                modifier = Modifier.let {
                    val mod = it.fillMaxSize()
                    if (consumeWindowInsets) {
                        mod.consumeWindowInsets(
                            WindowInsets.navigationBars.only(WindowInsetsSides.Vertical)
                        )
                    }
                    mod
                }
            ) {
                val topInset = insets.getTop(LocalDensity.current)
                val bottomInset = insets.getBottom(LocalDensity.current)
                val leftInset = insets.getLeft(LocalDensity.current, LocalLayoutDirection.current)
                val rightInset = insets.getRight(LocalDensity.current, LocalLayoutDirection.current)

                content(object : WindowInsetsScope {
                    override val systemBarInsets: WindowInsets
                        get() = insets
                    override val isWindowInsetsConsumed: Boolean
                        get() = consumeWindowInsets
                    override val topInset: Int
                        get() = topInset
                    override val bottomInset: Int
                        get() = bottomInset
                    override val leftInset: Int
                        get() = leftInset
                    override val rightInset: Int
                        get() = rightInset
                })
            }
        }
    }
}