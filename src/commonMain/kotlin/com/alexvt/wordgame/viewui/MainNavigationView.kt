package com.alexvt.wordgame.viewui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alexvt.wordgame.AppDependencies
import com.alexvt.wordgame.viewmodel.MainNavigationViewModel
import com.alexvt.wordgame.viewui.common.Fonts
import com.alexvt.wordgame.viewui.common.WindowLifecycleEnvironment
import kotlinx.coroutines.launch
import moe.tlaster.precompose.ui.viewModel


@Composable
fun MainNavigationView(
    dependencies: AppDependencies,
    lifecycle: WindowLifecycleEnvironment,
) {
    val viewModel = viewModel {
        MainNavigationViewModel(
            dependencies.mainNavigationViewModelUseCases, lifecycle.onWindowDismiss,
        )
    }
    val uiState by viewModel.getUiStateFlow().collectAsState()
    var isUiResourcesLoaded by remember { mutableStateOf(false) }

    with(rememberCoroutineScope()) {
        launch {
            lifecycle.backButtonPressFlow.collect {
                viewModel.onBackButton()
            }
        }
        launch {
            lifecycle.windowVisibilityFlow.collect { isShown ->
                viewModel.onIsWindowShown(isShown)
            }
        }
        launch {
            Fonts.NotoSans.load()
            Fonts.RobotoMono.load()
            isUiResourcesLoaded = true
        }
    }

    DisableSelection {
        MaterialTheme(
            colors = Colors(
                primary = Color(uiState.theme.color.background.normal),
                primaryVariant = Color(uiState.theme.color.background.unselected),
                secondary = Color(uiState.theme.color.background.normal),
                secondaryVariant = Color(uiState.theme.color.background.normal),
                background = Color(uiState.theme.color.background.normal),
                surface = Color(uiState.theme.color.background.normal),
                error = Color(uiState.theme.color.background.normal),
                onPrimary = Color(uiState.theme.color.text.normal),
                onSecondary = Color(uiState.theme.color.text.normal),
                onBackground = Color(uiState.theme.color.text.normal),
                onSurface = Color(uiState.theme.color.text.normal),
                onError = Color(uiState.theme.color.text.normal),
                isLight = uiState.isThemeLight,
            )
        ) {
            Box(
                Modifier.fillMaxSize().background(Color(uiState.theme.color.background.normal)),
                contentAlignment = Alignment.Center,
            ) {
                if (!uiState.isLoaded || !isUiResourcesLoaded) {
                    Icon(
                        Icons.Filled.HourglassTop,
                        contentDescription = "Loading...",
                        tint = Color(uiState.theme.color.text.dim),
                        modifier = Modifier.size(32.dp).align(Alignment.Center)
                    )
                }
                if (uiState.isLoaded && isUiResourcesLoaded) {
                    WordGameView(dependencies, lifecycle)
                }
                AnimatedVisibility(
                    uiState.isGamePaused,
                    modifier = Modifier.fillMaxSize(),
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(Color(uiState.theme.color.background.normal))
                    ) {
                        Column(
                            Modifier.width(260.dp).align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (uiState.isLoaded && isUiResourcesLoaded) {
                                PauseMenuView(dependencies, lifecycle)
                            }
                        }
                    }
                }
            }
        }
    }
}
