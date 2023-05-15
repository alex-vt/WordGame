package com.alexvt.wordgame.usecases.internal

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class HideBeginnerHintUseCase(
    private val settingsRepository: SettingsRepository
) {

    fun execute() {
        with(settingsRepository) {
            updateSettings(readSettings().copy(isBeginnerHintShown = false))
        }
    }

}