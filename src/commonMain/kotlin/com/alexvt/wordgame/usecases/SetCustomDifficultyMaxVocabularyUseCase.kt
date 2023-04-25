package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetCustomDifficultyMaxVocabularyUseCase(
    private val settingsRepository: SettingsRepository,
) {

    fun execute(maxVocabularyPercentage: Int) {
        with(settingsRepository) {
            val oldSettings = readSettings()
            updateSettings(
                oldSettings.copy(
                    computerDifficulty = oldSettings.computerDifficulty.copy(
                        maxVocabularyNormalizedSize = maxVocabularyPercentage / 100.0,
                        isCustom = true,
                    )
                )
            )
        }
    }

}