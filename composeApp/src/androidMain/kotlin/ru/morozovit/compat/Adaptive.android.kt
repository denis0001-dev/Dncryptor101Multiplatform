package ru.morozovit.compat

import androidx.compose.runtime.Composable

@Composable
actual fun currentWindowSize() = androidx.compose.material3.adaptive.currentWindowSize()