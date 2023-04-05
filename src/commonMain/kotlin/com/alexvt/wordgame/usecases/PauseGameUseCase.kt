package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.NavigationStateRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class PauseGameUseCase(
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
    }

}