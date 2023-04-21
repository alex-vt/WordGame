package com.alexvt.wordgame.usecases

import com.alexvt.wordgame.AppScope
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class GetDifficultyPresetsUseCase {

    // preset vocabulary sizes are intentionally not proportional to 0.05, to tell apart from custom
    private val difficultyParams = listOf(
        DifficultyParams(maxWordLength = 4, maxVocabularyNormalizedSize = 0.12),
        DifficultyParams(maxWordLength = 5, maxVocabularyNormalizedSize = 0.12),
        DifficultyParams(maxWordLength = 6, maxVocabularyNormalizedSize = 0.18),
        DifficultyParams(maxWordLength = 10, maxVocabularyNormalizedSize = 0.52),
    )

    fun execute(): List<DifficultyParams> {
        return difficultyParams
    }

}

data class DifficultyParams(
    val maxWordLength: Int,
    val maxVocabularyNormalizedSize: Double,
)