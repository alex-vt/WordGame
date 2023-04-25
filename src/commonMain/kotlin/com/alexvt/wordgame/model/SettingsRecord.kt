package com.alexvt.wordgame.model

@kotlinx.serialization.Serializable
data class BorderColors(
    val unselected: Long,
    val neutral: Long,
    val selected: Long,
)

@kotlinx.serialization.Serializable
data class TextColors(
    val normal: Long,
    val inactive: Long,
    val dim: Long,
    val dimInactive: Long,
    val bright: Long,
    val accent: Long,
    val error: Long,
)

@kotlinx.serialization.Serializable
data class BackgroundColors(
    val normal: Long,
    val clickable: Long,
    val unselected: Long,
    val accent: Long,
)

@kotlinx.serialization.Serializable
data class ThemeColors(
    val background: BackgroundColors,
    val text: TextColors,
    val border: BorderColors,
)

@kotlinx.serialization.Serializable
data class FontSizes(
    val small: Int,
    val normal: Int,
    val big: Int,
    val button: Int,
    val cell: Int,
)

@kotlinx.serialization.Serializable
data class ThemeFonts(
    val size: FontSizes,
)

@kotlinx.serialization.Serializable
data class ThemeRecord(
    val color: ThemeColors,
    val font: ThemeFonts,
)

@kotlinx.serialization.Serializable
data class DifficultyRecord(
    val maxWordLength: Int,
    val maxVocabularyNormalizedSize: Double,
    val isCustom: Boolean,
)

@kotlinx.serialization.Serializable
data class SettingsRecord(
    val isPlayer1computer: Boolean,
    val isPlayer2computer: Boolean,
    val computerDifficulty: DifficultyRecord,
    val theme: ThemeRecord,
)
