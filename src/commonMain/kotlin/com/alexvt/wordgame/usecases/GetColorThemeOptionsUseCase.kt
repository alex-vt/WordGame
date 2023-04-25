package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.ColorThemeRepository
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class GetColorThemeOptionsUseCase(
    private val settingsRepository: SettingsRepository,
    private val colorThemeRepository: ColorThemeRepository,
) {

    fun execute(): List<ColorThemeOption> {
        val currentThemeColors = settingsRepository.readSettings().theme.color
        return colorThemeRepository.getColorThemes().map { themeColors ->
            ColorThemeOption(
                backgroundColor = themeColors.background.normal,
                textColor = themeColors.text.normal,
                isSelected = themeColors == currentThemeColors
            )
        }
    }

}

data class ColorThemeOption(
    val backgroundColor: Long,
    val textColor: Long,
    val isSelected: Boolean,
)