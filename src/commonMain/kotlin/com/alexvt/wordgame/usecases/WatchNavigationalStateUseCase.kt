package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.NavigationStateRecord
import com.alexvt.wordgame.repository.NavigationStateRepository
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class WatchNavigationalStateUseCase(
    private val navigationStateRepository: NavigationStateRepository,
) {

    fun execute(): StateFlow<NavigationStateRecord> {
        return navigationStateRepository.getNavigationStateFlow()
    }

}