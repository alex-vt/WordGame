package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetGameTypeUseCase(
    private val settingsRepository: SettingsRepository
) {

    fun execute(gameTypeIndex: Int) {
        with(settingsRepository) {
            updateSettings(
                readSettings().copy(
                    isPlayer1computer = when (gameTypeIndex) {
                        0 -> false
                        1 -> false
                        else -> true
                    },
                    isPlayer2computer = when (gameTypeIndex) {
                        0 -> false
                        1 -> true
                        else -> true
                    },
                )
            )
        }
    }

}