package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetCustomDifficultyMaxWordLengthUseCase(
    private val settingsRepository: SettingsRepository,
) {

    fun execute(maxWordLength: Int) {
        with(settingsRepository) {
            val oldSettings = readSettings()
            updateSettings(
                oldSettings.copy(
                    computerDifficulty = oldSettings.computerDifficulty.copy(
                        maxWordLength = maxWordLength,
                        isCustom = true,
                    )
                )
            )
        }
    }

}