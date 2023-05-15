package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.NavigationStateRepository
import com.alexvt.wordgame.usecases.internal.HideBeginnerHintUseCase
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class PauseGameUseCase(
    private val hideBeginnerHintUseCase: HideBeginnerHintUseCase,
    private val navigationStateRepository: NavigationStateRepository,
) {

    fun execute() {
        with(navigationStateRepository) {
            updateNavigationState(
                getNavigationStateFlow().value.copy(
                    isPaused = true,
                )
            )
        }
        hideBeginnerHintUseCase.execute() // already knows how to see rules and settings
    }

}