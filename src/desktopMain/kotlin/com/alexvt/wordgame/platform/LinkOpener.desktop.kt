package com.alexvt.wordgame.platform

import androidx.compose.ui.platform.UriHandler

actual fun browseLink(uriHandler: UriHandler, link: String) {
    Runtime.getRuntime().exec(arrayOf("bash", "-c", "open $link"))
}
