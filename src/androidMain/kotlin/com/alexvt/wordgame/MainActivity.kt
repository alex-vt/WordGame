package com.alexvt.wordgame

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.alexvt.wordgame.viewui.MainNavigationView
import com.alexvt.wordgame.viewui.common.WindowLifecycleEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.lifecycle.LifecycleObserver
import moe.tlaster.precompose.lifecycle.PreComposeActivity
import moe.tlaster.precompose.lifecycle.setContent

class MainActivity : PreComposeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var backButtonCallback: OnBackPressedCallback
        val backButtonFlow = MutableSharedFlow<Unit>(
            extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
        ).apply {
            backButtonCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    tryEmit(Unit)
                }
            }
        }.asSharedFlow()
        onBackPressedDispatcher.addCallback(this, backButtonCallback)

        setContent {
            MainNavigationView(
                App.dependencies,
                lifecycle = WindowLifecycleEnvironment(
                    windowVisibilityFlow = getIsActiveStateFlow(),
                    backButtonPressFlow = backButtonFlow,
                    backgroundCoroutineDispatcher = Dispatchers.Default,
                    onWindowDismiss = {
                        backButtonCallback.isEnabled = false
                        onBackPressedDispatcher.onBackPressed() // won't loop back into callback
                        backButtonCallback.isEnabled = true
                    }
                ),
            )
        }
    }

    private fun getIsActiveStateFlow(): StateFlow<Boolean> =
        MutableStateFlow(false).apply {
            lifecycle.addObserver(object : LifecycleObserver {
                override fun onStateChanged(state: Lifecycle.State) {
                    tryEmit(state == Lifecycle.State.Active)
                }
            })
        }.asStateFlow()

}
