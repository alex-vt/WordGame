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
            updateSettings(
                readSettings().copy(
                    computerMaxVocabularyNormalizedSize = maxVocabularyPercentage / 100.0,
                )
            )
        }
    }

}