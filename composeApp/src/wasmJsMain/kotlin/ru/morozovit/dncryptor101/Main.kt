package ru.morozovit.dncryptor101

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLPreElement

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    try {
        ComposeViewport(document.body!!) {
            MainScreen()
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        document.getElementById("err_overlay")!!.apply {
            this as HTMLDivElement
            style.display = "flex";
            classList.add("open");

            val errorMessage = document.getElementById("err_code") as HTMLPreElement;
            errorMessage.textContent = e.stackTraceToString();
        }
    }
}