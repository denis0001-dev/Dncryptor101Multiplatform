package ru.morozovit.dncryptor101.ui

import androidx.compose.runtime.Composable

@Composable
actual inline fun ProvideContextMenuRepresentation(darkTheme: Boolean, content: @Composable () -> Unit) {
    content()
}