package com.alexvt.wordgame

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.alexvt.wordgame.viewui.MainNavigationView
import com.alexvt.wordgame.viewui.common.WindowLifecycleEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import moe.tlaster.precompose.PreComposeWindow
import java.awt.Dimension

@FlowPreview
fun main() = application {

    val dependencies: AppDependencies = remember { AppDependencies::class.create() }

    val state = rememberWindowState(width = 450.dp, height = 750.dp)
    PreComposeWindow(
        onCloseRequest = ::exitApplication,
        title = "Word Game",
        state = state,
        icon = BitmapPainter(useResource("ic_launcher.png", ::loadImageBitmap)),
    ) {
        window.minimumSize = Dimension(270, 700)

        val isWindowShownFlow = MutableStateFlow(true).apply {
            window.addWindowStateListener {
                tryEmit(!state.isMinimized)
            }
        }.asStateFlow()

        Box {
            MainNavigationView(
                dependencies,
                lifecycle = WindowLifecycleEnvironment(
                    windowVisibilityFlow = isWindowShownFlow,
                    backgroundCoroutineDispatcher = Dispatchers.Default,
                ),
            )
        }
    }

}