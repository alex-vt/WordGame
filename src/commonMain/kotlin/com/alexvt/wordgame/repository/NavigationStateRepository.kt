package com.alexvt.wordgame.repository

import com.alexvt.wordgame.model.NavigationStateRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationStateRepository {

    private val navigationStateMutableFlow: MutableStateFlow<NavigationStateRecord> =
        MutableStateFlow(value = NavigationStateRecord(isPaused = false, isShown = true))

    fun getNavigationStateFlow(): StateFlow<NavigationStateRecord> =
        navigationStateMutableFlow.asStateFlow()

    fun updateNavigationState(newNavigationStateRecord: NavigationStateRecord) {
        navigationStateMutableFlow.tryEmit(newNavigationStateRecord)
    }

}