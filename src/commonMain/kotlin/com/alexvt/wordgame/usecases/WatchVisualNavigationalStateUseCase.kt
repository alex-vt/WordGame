package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.ThemeRecord
import com.alexvt.wordgame.repository.NavigationStateRepository
import com.alexvt.wordgame.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class WatchVisualNavigationalStateUseCase(
    private val navigationStateRepository: NavigationStateRepository,
    private val settingsRepository: SettingsRepository,
) {

    data class VisualNavigationalState(val isPaused: Boolean, val theme: ThemeRecord)

    fun execute(coroutineScope: CoroutineScope): StateFlow<VisualNavigationalState> {
        return navigationStateRepository.getNavigationStateFlow().combine(
            settingsRepository.getSettingsFlow()
        ) { navigationStateRecord, settingsRecord ->
            VisualNavigationalState(navigationStateRecord.isPaused, settingsRecord.theme)
        }.stateIn(
            coroutineScope,
            SharingStarted.Lazily,
            initialValue = VisualNavigationalState(
                navigationStateRepository.getNavigationStateFlow().value.isPaused,
                settingsRepository.getSettingsFlow().value.theme
            )
        )
    }

}