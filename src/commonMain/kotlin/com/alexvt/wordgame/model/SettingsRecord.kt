package com.alexvt.wordgame.model

data class BorderColors(
    val unselected: Long,
    val neutral: Long,
    val selected: Long,
)

data class TextColors(
    val normal: Long,
    val inactive: Long,
    val dim: Long,
    val dimInactive: Long,
    val bright: Long,
    val accent: Long,
    val error: Long,
)

data class BackgroundColors(
    val normal: Long,
    val clickable: Long,
    val unselected: Long,
    val accent: Long,
)

data class ThemeColors(
    val background: BackgroundColors,
    val text: TextColors,
    val border: BorderColors,
)

data class FontSizes(
    val small: Int,
    val normal: Int,
    val big: Int,
    val button: Int,
    val cell: Int,
)

data class ThemeFonts(
    val size: FontSizes,
)

data class ThemeRecord(
    val color: ThemeColors,
    val font: ThemeFonts,
)

data class SettingsRecord(
    val isPlayer1computer: Boolean,
    val isPlayer2computer: Boolean,
    val computerMaxWordLength: Int,
    val computerMaxVocabularyNormalizedSize: Double,
    val theme: ThemeRecord,
)
