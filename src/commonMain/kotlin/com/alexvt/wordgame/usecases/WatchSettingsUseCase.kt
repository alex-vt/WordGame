package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.SettingsRecord
import com.alexvt.wordgame.model.ThemeRecord
import com.alexvt.wordgame.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

enum class GameType {
    PLAYER_VS_PLAYER, PLAYER_VS_COMPUTER, COMPUTER_VS_COMPUTER
}

enum class GameDifficulty {
    EASY, MEDIUM, HARD, ULTRA
}

data class Settings(
    val gameType: GameType,
    val gameDifficulty: GameDifficulty,
    val theme: ThemeRecord,
)

@AppScope
@Inject
class WatchSettingsUseCase(
    private val settingsRepository: SettingsRepository
) {

    fun execute(coroutineScope: CoroutineScope): StateFlow<Settings> {
        return settingsRepository.getSettingsFlow().map { it.toGameSettings() }.stateIn(
            coroutineScope,
            SharingStarted.Lazily,
            initialValue = settingsRepository.getSettingsFlow().value.toGameSettings()
        )
    }

    private fun SettingsRecord.toGameSettings(): Settings =
        Settings(
            gameType = when {
                !isPlayer1computer && !isPlayer2computer -> GameType.PLAYER_VS_PLAYER
                isPlayer1computer && isPlayer2computer -> GameType.COMPUTER_VS_COMPUTER
                else -> GameType.PLAYER_VS_COMPUTER
            },
            gameDifficulty = when (computerMaxWordLength) {
                4 -> GameDifficulty.EASY
                5 -> GameDifficulty.MEDIUM
                6 -> GameDifficulty.HARD
                else -> GameDifficulty.ULTRA
            }, // todo also use computerMaxVocabularyNormalizedSize
            theme,
        )

}