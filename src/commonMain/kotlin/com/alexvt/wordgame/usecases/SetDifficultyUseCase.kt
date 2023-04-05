package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetDifficultyUseCase(
    private val settingsRepository: SettingsRepository
) {

    fun execute(difficultyLevelIndex: Int) {
        with(settingsRepository) {
            updateSettings(
                readSettings().copy(
                    computerMaxWordLength = when (difficultyLevelIndex) {
                        0 -> 4
                        1 -> 5
                        2 -> 6
                        else -> 10
                    },
                    computerMaxVocabularyNormalizedSize = when (difficultyLevelIndex) {
                        0 -> 0.1
                        1 -> 0.1
                        2 -> 0.15
                        else -> 0.5
                    },
                )
            )
        }
    }

}