package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetPresetSelectableDifficultyUseCase(
    private val difficultyPresetsUseCase: GetDifficultyPresetsUseCase,
    private val settingsRepository: SettingsRepository,
) {

    fun execute(difficultyLevelIndex: Int) {
        val difficultyPresets = difficultyPresetsUseCase.execute()
        val inBoundsDifficultyLevelIndex =
            difficultyLevelIndex.coerceIn(0, difficultyPresets.size - 1)
        val selectedPresetParams = difficultyPresets[inBoundsDifficultyLevelIndex]
        with(settingsRepository) {
            updateSettings(
                readSettings().copy(
                    computerMaxWordLength = selectedPresetParams.maxWordLength,
                    computerMaxVocabularyNormalizedSize = selectedPresetParams
                        .maxVocabularyNormalizedSize,
                )
            )
        }
    }

}