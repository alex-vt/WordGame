package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.repository.ColorThemeRepository
import com.alexvt.wordgame.repository.SettingsRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SetColorThemeUseCase(
    private val settingsRepository: SettingsRepository,
    private val colorThemeRepository: ColorThemeRepository,
) {

    fun execute(colorThemeSelectionIndex: Int) {
        with(settingsRepository) {
            val oldSettings = readSettings()
            updateSettings(
                oldSettings.copy(
                    theme = oldSettings.theme.copy(
                        color = colorThemeRepository.getColorThemes()
                            .getOrElse(colorThemeSelectionIndex) {
                                colorThemeRepository.getDefaultColorTheme()
                            }
                    )
                )
            )
        }
    }

}
