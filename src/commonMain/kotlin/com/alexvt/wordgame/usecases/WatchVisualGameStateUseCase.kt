package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.GameState
import com.alexvt.wordgame.model.ThemeRecord
import com.alexvt.wordgame.repository.GameStateRepository
import com.alexvt.wordgame.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class WatchVisualGameStateUseCase(
    private val gameStateRepository: GameStateRepository,
    private val settingsRepository: SettingsRepository,
) {

    data class VisualGameState(val gameState: GameState, val theme: ThemeRecord)

    fun execute(coroutineScope: CoroutineScope): StateFlow<VisualGameState> {
        return gameStateRepository.getGameStateFlow().combine(
            settingsRepository.getSettingsFlow()
        ) { gameState, settings ->
            VisualGameState(gameState, settings.theme)
        }.stateIn(
            coroutineScope,
            SharingStarted.Lazily,
            initialValue = VisualGameState(
                gameStateRepository.getGameStateFlow().value,
                settingsRepository.getSettingsFlow().value.theme,
            )
        )
    }

}