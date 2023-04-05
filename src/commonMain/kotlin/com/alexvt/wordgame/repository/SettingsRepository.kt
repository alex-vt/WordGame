package com.alexvt.wordgame.repository

import com.alexvt.wordgame.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository {

    private val settingsMutableFlow: MutableStateFlow<SettingsRecord> =
        MutableStateFlow(
            SettingsRecord(
                isPlayer1computer = false,
                isPlayer2computer = true,
                computerMaxWordLength = 5,
                computerMaxVocabularyNormalizedSize = 0.1,
                theme = ThemeRecord(
                    color = ThemeColors(
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
                    ),
                    font = ThemeFonts(
                        size = FontSizes(
                            small = 13,
                            normal = 14,
                            big = 16,
                            button = 18,
                            cell = 20,
                        )
                    )
                ),
            ) // todo persist
        )

    fun getSettingsFlow(): StateFlow<SettingsRecord> =
        settingsMutableFlow.asStateFlow()

    fun readSettings(): SettingsRecord =
        settingsMutableFlow.value

    fun updateSettings(newSettingsRecord: SettingsRecord) {
        settingsMutableFlow.tryEmit(newSettingsRecord)
    }

}