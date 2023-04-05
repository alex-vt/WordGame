package com.alexvt.wordgame.platform

import androidx.compose.ui.platform.UriHandler

expect fun browseLink(uriHandler: UriHandler, link: String)
