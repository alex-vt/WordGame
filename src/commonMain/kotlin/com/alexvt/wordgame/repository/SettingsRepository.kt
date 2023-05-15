package com.alexvt.wordgame.repository

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.FontSizes
import com.alexvt.wordgame.model.SettingsRecord
import com.alexvt.wordgame.model.ThemeFonts
import com.alexvt.wordgame.model.ThemeRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

/**
 * Settings are in-memory since initial read from storage. Assuming no external writes.
 */
@AppScope
@Inject
class SettingsRepository(
    private val storageRepository: StorageRepository,
    colorThemeRepository: ColorThemeRepository,
    difficultyPresetRepository: DifficultyPresetRepository,
) {
    private val defaultFontTheme = ThemeFonts(
        size = FontSizes(
            small = 13,
            normal = 14,
            big = 16,
            button = 18,
            cell = 20,
        )
    )
    private val defaultSettings = SettingsRecord(
        isPlayer1computer = false,
        isPlayer2computer = true,
        computerDifficulty = difficultyPresetRepository.getDefaultDifficultyPreset(),
        isBeginnerHintShown = true,
        theme = ThemeRecord(
            color = colorThemeRepository.getDefaultColorTheme(),
            font = defaultFontTheme
        ),
    )

    private val storageKey = "settings"
    private val json = Json { prettyPrint = true }

    private val settingsMutableFlow: MutableStateFlow<SettingsRecord> =
        MutableStateFlow(
            storageRepository.readEntry(
                key = storageKey,
                defaultValue = json.encodeToString(defaultSettings)
            ).let { jsonString ->
                try {
                    json.decodeFromString(jsonString)
                } catch (t: SerializationException) {
                    // settings migration strategy on schema change: reset to default
                    defaultSettings
                }
            }
        )

    fun getSettingsFlow(): StateFlow<SettingsRecord> =
        settingsMutableFlow.asStateFlow()

    fun readSettings(): SettingsRecord =
        settingsMutableFlow.value

    fun updateSettings(newSettingsRecord: SettingsRecord) {
        if (newSettingsRecord == settingsMutableFlow.value) return // no storage overwrite
        settingsMutableFlow.tryEmit(newSettingsRecord).also {
            storageRepository.writeEntry(
                key = storageKey,
                value = json.encodeToString(newSettingsRecord)
            )
        }
    }

}