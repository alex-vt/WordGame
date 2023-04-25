package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import com.alexvt.wordgame.model.DifficultyRecord
import com.alexvt.wordgame.repository.DifficultyPresetRepository
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class GetDifficultySelectionIndexUseCase(
    private val difficultyPresetRepository: DifficultyPresetRepository,
) {

    fun execute(difficultyRecord: DifficultyRecord): Int {
        // value -1 is safe when difficulty is custom - presets will be hidden from view
        return difficultyPresetRepository.getDifficultyPresets().indexOf(difficultyRecord)
    }

}
