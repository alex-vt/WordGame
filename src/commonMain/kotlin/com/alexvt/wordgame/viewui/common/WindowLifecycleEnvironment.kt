package com.alexvt.wordgame.viewui.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

data class WindowLifecycleEnvironment(
    val backgroundCoroutineDispatcher: CoroutineDispatcher,
    val windowVisibilityFlow: StateFlow<Boolean>,
    val backButtonPressFlow: Flow<Unit> = flow {},
    val onWindowDismiss: () -> Unit = {},
)
