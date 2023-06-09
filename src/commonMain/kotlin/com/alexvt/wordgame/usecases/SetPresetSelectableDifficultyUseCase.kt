package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.DifficultyPresetRepository
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetPresetSelectableDifficultyUseCase(
    private val difficultyPresetRepository: DifficultyPresetRepository,
    private val settingsRepository: SettingsRepository,
) {

    fun execute(difficultyLevelIndex: Int) {
        with(settingsRepository) {
            updateSettings(
                readSettings().copy(
                    computerDifficulty = with(difficultyPresetRepository) {
                        getDifficultyPresets().getOrElse(difficultyLevelIndex) {
                            getDefaultDifficultyPreset()
                        }
                    }
                )
            )
        }
    }

}