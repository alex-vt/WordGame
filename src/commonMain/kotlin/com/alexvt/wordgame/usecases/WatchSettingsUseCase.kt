package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.SettingsRecord
import com.alexvt.wordgame.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class WatchSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {

    fun execute(): StateFlow<SettingsRecord> {
        return settingsRepository.getSettingsFlow()
    }

}