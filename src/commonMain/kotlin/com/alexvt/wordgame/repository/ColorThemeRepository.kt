package com.alexvt.wordgame.repository

import com.alexvt.wordgame.model.BackgroundColors
import com.alexvt.wordgame.model.BorderColors
import com.alexvt.wordgame.model.TextColors
import com.alexvt.wordgame.model.ThemeColors

class ColorThemeRepository() {

    private val darkColorTheme = ThemeColors(
        background = BackgroundColors(
            normal = 0xFF333333,
            clickable = 0xFF404040,
            unselected = 0xFF222222,
            accent = 0xFF996010,
        ),
        text = TextColors(
            normal = 0xFFEEEEEE,
            inactive = 0xFFAAAAAA,
            dim = 0xFFCCCCCC,
            dimInactive = 0xFF888888,
            bright = 0xFFFFFFFF,
            accent = 0xFFCC8811,
            error = 0xFFFF9999,
        ),
        border = BorderColors(
            unselected = 0xFF444444,
            neutral = 0xFF606060,
            selected = 0xFFCCCCCC,
        ),
    )

    private val cyberColorTheme = ThemeColors(
        background = BackgroundColors(
            normal = 0xFF502780,
            clickable = 0xFF774288,
            unselected = 0xFF402060,
            accent = 0xFF009985,
        ),
        text = TextColors(
            normal = 0xFFF0F6F2,
            inactive = 0xFFAAB0AC,
            dim = 0xFFCCD2CE,
            dimInactive = 0xFF888E8A,
            bright = 0xFFFFFFFF,
            accent = 0xFF00FFB0,
            error = 0xFFFFAAAA,
        ),
        border = BorderColors(
            unselected = 0xFF108070,
            neutral = 0xFF108070,
            selected = 0xFFB0FFE7,
        ),
    )

    private val springColorTheme = ThemeColors(
        background = BackgroundColors(
            normal = 0xFFD2F2D4,
            clickable = 0xFFF0FFF2,
            unselected = 0xFFC0E0C2,
            accent = 0xFFF0E010,
        ),
        text = TextColors(
            normal = 0xFF111111,
            inactive = 0xFF444444,
            dim = 0xFF505050,
            dimInactive = 0xFF777777,
            bright = 0xFF000000,
            accent = 0xFF999000,
            error = 0xFF772222,
        ),
        border = BorderColors(
            unselected = 0xFFB0D0B2,
            neutral = 0xFFB2D2B4,
            selected = 0xFF224424,
        ),
    )

    private val lightColorTheme = ThemeColors(
        background = BackgroundColors(
            normal = 0xFFE0E0E0,
            clickable = 0xFFF5F5F5,
            unselected = 0xFFCCCCCC,
            accent = 0xFFDD9011,
        ),
        text = TextColors(
            normal = 0xFF111111,
            inactive = 0xFF444444,
            dim = 0xFF505050,
            dimInactive = 0xFF777777,
            bright = 0xFF000000,
            accent = 0xFFAA7010,
            error = 0xFF772222,
        ),
        border = BorderColors(
            unselected = 0xFFBBBBBB,
            neutral = 0xFFBBBBBB,
            selected = 0xFF222222,
        ),
    )

    fun getColorThemes(): List<ThemeColors> =
        listOf(darkColorTheme, cyberColorTheme, springColorTheme, lightColorTheme)

    fun getDefaultColorTheme(): ThemeColors =
        darkColorTheme

}