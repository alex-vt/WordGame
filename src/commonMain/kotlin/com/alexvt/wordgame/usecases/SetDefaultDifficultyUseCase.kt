package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.DifficultyPresetRepository
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetDefaultDifficultyUseCase(
    private val difficultyPresetRepository: DifficultyPresetRepository,
    private val settingsRepository: SettingsRepository,
) {

    fun execute(isToBeCustom: Boolean) {
        with(settingsRepository) {
            updateSettings(
                readSettings().copy(
                    computerDifficulty = with(difficultyPresetRepository) {
                        if (isToBeCustom) {
                            getDefaultCustomDifficulty()
                        } else {
                            getDefaultDifficultyPreset()
                        }
                    }
                )
            )
        }
    }

}