package com.alexvt.wordgame.viewmodel

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.ThemeRecord
import com.alexvt.wordgame.usecases.LoadWordsFromResourcesUseCase
import com.alexvt.wordgame.usecases.ResumeGameUseCase
import com.alexvt.wordgame.usecases.SetIfGameShownUseCase
import com.alexvt.wordgame.usecases.WatchVisualNavigationalStateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

@AppScope
@Inject
class MainNavigationViewModelUseCases(
    val watchVisualNavigationalStateUseCase: WatchVisualNavigationalStateUseCase,
    val resumeGameUseCase: ResumeGameUseCase,
    val setIfGameShownUseCase: SetIfGameShownUseCase,
    val loadWordsFromResourcesUseCase: LoadWordsFromResourcesUseCase,
)

class MainNavigationViewModel(
    private val useCases: MainNavigationViewModelUseCases,
    private val onWindowDismiss: () -> Unit,
) : ViewModel() {

    data class UiState(
        val isGamePaused: Boolean,
        val theme: ThemeRecord,
        val isThemeLight: Boolean,
        val isLoaded: Boolean,
    )

    fun onBackButton() {
        val isPaused =
            useCases.watchVisualNavigationalStateUseCase.execute(viewModelScope).value.isPaused
        if (isPaused) {
            useCases.resumeGameUseCase.execute()
        } else {
            onWindowDismiss()
        }
    }

    fun onIsWindowShown(isShown: Boolean) {
        useCases.setIfGameShownUseCase.execute(isShown)
    }

    fun getUiStateFlow(): StateFlow<UiState> =
        useCases.watchVisualNavigationalStateUseCase.execute(viewModelScope).combine(
            useCases.loadWordsFromResourcesUseCase.execute(viewModelScope)
        ) { visualState, isWordsLoaded ->
            visualState.toUiState(isWordsLoaded = isWordsLoaded)
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = useCases.watchVisualNavigationalStateUseCase
                .execute(viewModelScope).value.toUiState(isWordsLoaded = true)
        )

    private fun WatchVisualNavigationalStateUseCase.VisualNavigationalState.toUiState(
        isWordsLoaded: Boolean
    ): UiState =
        UiState(
            isPaused,
            theme,
            isThemeLight = theme.color.background.normal > theme.color.text.normal,
            isLoaded = isWordsLoaded,
        )

}