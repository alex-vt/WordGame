package com.alexvt.wordgame.viewmodel

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.SettingsRecord
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
import kotlin.math.roundToInt

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
    val getDifficultySelectionIndexUseCase: GetDifficultySelectionIndexUseCase,
    val setDefaultDifficultyUseCase: SetDefaultDifficultyUseCase,
    val getColorThemeOptionsUseCase: GetColorThemeOptionsUseCase,
    val setColorThemeUseCase: SetColorThemeUseCase,
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
        val isComputerDifficultyVisible: Boolean,
        val isComputerDifficultyCustom: Boolean,
        val customDifficultyVocabularySliderValue: Int,
        val customDifficultyMaxWordLengthSliderValue: Int,
        val isOngoingGameWarningVisible: Boolean,
        val colorThemeOptions: List<ColorThemeOption>,
        val theme: ThemeRecord,
    )

    fun getUiStateFlow(): StateFlow<UiState> =
        useCases.watchSettingsUseCase.execute().map { it.toUiState() }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            initialValue = useCases.watchSettingsUseCase.execute().value.toUiState()
        )

    fun onColorThemeSelection(themeIndex: Int) {
        useCases.setColorThemeUseCase.execute(themeIndex)
    }

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
        useCases.setDefaultDifficultyUseCase.execute(
            isToBeCustom = !getUiStateFlow().value.isComputerDifficultyCustom,
        )
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

    private fun SettingsRecord.toUiState(): UiState =
        UiState(
            gameTypeSelectionIndex = when {
                !isPlayer1computer && !isPlayer2computer -> 0
                isPlayer1computer && isPlayer2computer -> 2
                else -> 1
            },
            computerDifficultySelectionIndex = useCases.getDifficultySelectionIndexUseCase
                .execute(computerDifficulty),
            isComputerDifficultyVisible = isPlayer1computer || isPlayer2computer,
            isComputerDifficultyCustom = computerDifficulty.isCustom,
            customDifficultyVocabularySliderValue = computerDifficulty.maxVocabularyNormalizedSize
                .toIntPercent(),
            customDifficultyMaxWordLengthSliderValue = computerDifficulty.maxWordLength,
            isOngoingGameWarningVisible = useCases.isCurrentGameLosableUseCase.execute(),
            colorThemeOptions = useCases.getColorThemeOptionsUseCase.execute(),
            theme,
        )

    private fun Double.toIntPercent(): Int = (this * 100).roundToInt()

}