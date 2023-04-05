package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.NavigationStateRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetIfGameShownUseCase(
    private val navigationStateRepository: NavigationStateRepository,
) {

    fun execute(isShown: Boolean) {
        with(navigationStateRepository) {
            updateNavigationState(
                getNavigationStateFlow().value.copy(
                    isShown = isShown,
                )
            )
        }
    }

}