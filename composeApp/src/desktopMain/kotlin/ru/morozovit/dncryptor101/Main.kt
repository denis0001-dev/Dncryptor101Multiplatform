package ru.morozovit.dncryptor101

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Dncryptor101",
    ) {
        MainScreen()
    }
}