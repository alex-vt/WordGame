package com.alexvt.wordgame.viewui.common

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.alexvt.wordgame.platform.loadFontFromResources

object Fonts {

    sealed class LoadableFontFamily(private val res: String) {
        private lateinit var fontFamily: FontFamily

        suspend fun load() {
            if (::fontFamily.isInitialized) return
            fontFamily = FontFamily(loadFontFromResources(res, FontWeight.Normal, FontStyle.Normal))
        }

        fun get(): FontFamily =
            fontFamily
    }

    object NotoSans : LoadableFontFamily("notosans_regular")
    object RobotoMono : LoadableFontFamily("robotomono_regular")

}
