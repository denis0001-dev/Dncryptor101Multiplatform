package ru.morozovit.dncryptor101.ui

import androidx.compose.foundation.DarkDefaultContextMenuRepresentation
import androidx.compose.foundation.LightDefaultContextMenuRepresentation
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Suppress("NOTHING_TO_INLINE")
@Composable
actual inline fun ProvideContextMenuRepresentation(darkTheme: Boolean, noinline content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContextMenuRepresentation provides if (darkTheme) {
            DarkDefaultContextMenuRepresentation
        } else {
            LightDefaultContextMenuRepresentation
        },
        content
    )
}