package com.alexvt.wordgame.viewmodel

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.ThemeRecord
import com.alexvt.wordgame.usecases.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import me.tatarka.inject.annotations.Inject
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

@AppScope
@Inject
class PauseMenuViewModelUseCases(
    val watchSettingsUseCase: WatchSettingsUseCase,
    val newGameUseCase: NewGameUseCase,
    val resumeGameUseCase: ResumeGameUseCase,
    val setGameTypeUseCase: SetGameTypeUseCase,
    val setPresetSelectableDifficultyUseCase: SetPresetSelectableDifficultyUseCase,
    val setCustomDifficultyMaxWordLengthUseCase: SetCustomDifficultyMaxWordLengthUseCase,
    val setCustomDifficultyMaxVocabularyUseCase: SetCustomDifficultyMaxVocabularyUseCase,
    val isCurrentGameLosableUseCase: IsCurrentGameLosableUseCase,
)

class PauseMenuViewModel(
    private val useCases: PauseMenuViewModelUseCases,
    private val backgroundCoroutineDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val backgroundCoroutineScope = viewModelScope + backgroundCoroutineDispatcher

    data class UiState(
        val gameTypeSelectionIndex: Int,
        val computerDifficultySelectionIndex: Int,
        val isComputerDifficultyCustom: Boolean,
        val customDifficultyVocabularySliderValue: Int,
        val customDifficultyMaxWordLengthSliderValue: Int,
        val isOngoingGameWarningVisible: Boolean,
        val theme: ThemeRecord,
    )

    fun getUiStateFlow(): StateFlow<UiState> =
        useCases.watchSettingsUseCase.execute(viewModelScope).map { it.toUiState() }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                initialValue = useCases.watchSettingsUseCase.execute(viewModelScope)
                    .value.toUiState()
            )

    fun onGameTypeSelection(buttonIndex: Int) {
        useCases.setGameTypeUseCase.execute(buttonIndex)
    }

    fun onDifficultySelection(buttonIndex: Int) {
        useCases.setPresetSelectableDifficultyUseCase.execute(buttonIndex)
    }

    fun onCustomDifficultyVocabularySelection(maxVocabularySliderValue: Int) {
        useCases.setCustomDifficultyMaxVocabularyUseCase.execute(
            maxVocabularyPercentage = maxVocabularySliderValue
        )
    }

    fun onCustomDifficultyWordLengthSelection(maxWordLengthSliderValue: Int) {
        useCases.setCustomDifficultyMaxWordLengthUseCase.execute(
            maxWordLength = maxWordLengthSliderValue
        )
    }

    fun onCustomDifficultyModeClick() {
        if (getUiStateFlow().value.isComputerDifficultyCustom) {
            // switching to standard, 2nd button
            useCases.setPresetSelectableDifficultyUseCase.execute(difficultyLevelIndex = 1)
        } else {
            // switching to custom, settings other than any standard
            useCases.setCustomDifficultyMaxVocabularyUseCase.execute(maxVocabularyPercentage = 20)
        }
    }

    fun onBackToGame() {
        useCases.resumeGameUseCase.execute()
    }

    fun onNewGame() {
        backgroundCoroutineScope.launch {
            useCases.newGameUseCase.execute(beforeComputerMove = {
                // on single threaded platforms, UI will change to game before computing moves
                delay(500)
            })
        }
    }

    private fun Settings.toUiState(): UiState =
        UiState(
            gameTypeSelectionIndex = gameType.ordinal,
            computerDifficultySelectionIndex = presetSelectableDifficulty.ordinal,
            isComputerDifficultyCustom = isCustom,
            customDifficultyVocabularySliderValue = customDifficultyVocabularyPercentage,
            customDifficultyMaxWordLengthSliderValue = customDifficultyMaxWordLength,
            isOngoingGameWarningVisible = useCases.isCurrentGameLosableUseCase.execute(),
            theme,
        )

}